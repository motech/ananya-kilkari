package org.motechproject.ananya.kilkari.obd.scheduler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.CronSchedulableJob;
import org.motechproject.event.MotechEvent;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
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
    public void shouldScheduleCronJobs() {
        ArrayList<CronSchedulableJob> newMessagesCronJob = new ArrayList<>();
        newMessagesCronJob.add(new CronSchedulableJob(new MotechEvent("subject.new"), "cronexpression.new"));
        ArrayList<CronSchedulableJob> retryMessagesCronJob = new ArrayList<>();
        retryMessagesCronJob.add(new CronSchedulableJob(new MotechEvent("subject.retry"), "cronexpression.retry.first.expression"));
        retryMessagesCronJob.add(new CronSchedulableJob(new MotechEvent("subject.retry"), "cronexpression.retry.second.expression"));
        when(newMessagesSenderJob.getCronJobs()).thenReturn(newMessagesCronJob);
        when(retryMessagesSenderJob.getCronJobs()).thenReturn(retryMessagesCronJob);

        new OBDScheduler(schedulerService, newMessagesSenderJob, retryMessagesSenderJob);

        ArgumentCaptor<CronSchedulableJob> captor = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(schedulerService, times(3)).safeScheduleJob(captor.capture());

        List<CronSchedulableJob> cronSchedulableJobs = captor.getAllValues();

        assertEquals(3, cronSchedulableJobs.size());
        assertTrue(cronSchedulableJobs.contains(newMessagesCronJob.get(0)));
        assertTrue(cronSchedulableJobs.contains(retryMessagesCronJob.get(0)));
        assertTrue(cronSchedulableJobs.contains(retryMessagesCronJob.get(1)));
    }

}
