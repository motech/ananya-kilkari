package org.motechproject.ananya.kilkari.scheduler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.CronSchedulableJob;
import org.motechproject.scheduler.domain.MotechEvent;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class OBDSchedulerTest {

    @Mock
    NewMessagesSenderJob newMessagesSenderJob;
    @Mock
    RetryMessagesSenderJob retryMessagesSenderJob;
    @Mock
    MotechSchedulerService schedulerService;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldScheduleANewAndRetryMessageJobs() {

        CronSchedulableJob cronSchedulableJobForNewMessages = new CronSchedulableJob(new MotechEvent("subject.new"), "cronexpression.new");
        CronSchedulableJob cronSchedulableJobForRetryMessages = new CronSchedulableJob(new MotechEvent("subject.retry"), "cronexpression.retry");
        when(newMessagesSenderJob.getCronJob()).thenReturn(cronSchedulableJobForNewMessages);
        when(retryMessagesSenderJob.getCronJob()).thenReturn(cronSchedulableJobForRetryMessages);

        new OBDScheduler(schedulerService, newMessagesSenderJob, retryMessagesSenderJob);

        ArgumentCaptor<CronSchedulableJob> captor = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(schedulerService, times(2)).safeScheduleJob(captor.capture());

        List<CronSchedulableJob> cronSchedulableJobs = captor.getAllValues();

        assertEquals(2, cronSchedulableJobs.size());
        assertTrue(cronSchedulableJobs.contains(cronSchedulableJobForNewMessages));
        assertTrue(cronSchedulableJobs.contains(cronSchedulableJobForRetryMessages));
    }

}
