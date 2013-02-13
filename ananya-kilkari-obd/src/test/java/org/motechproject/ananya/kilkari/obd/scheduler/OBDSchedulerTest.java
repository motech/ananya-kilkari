package org.motechproject.ananya.kilkari.obd.scheduler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.event.MotechEvent;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.CronSchedulableJob;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class OBDSchedulerTest {

    @Mock
    FirstMainSubSlotMessagesSenderJob firstMainSubSlotMessagesSenderJob;
    @Mock
    RetrySlotMessagesSenderJob retrySlotMessagesSenderJob;
    @Mock
    MotechSchedulerService schedulerService;
    @Mock
    private ThirdMainSubSlotMessagesSenderJob thirdMainSubSlotMessagesSenderJob;
    @Mock
    private SecondMainSubSlotMessagesSenderJob secondMainSubSlotMessagesSenderJob;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldScheduleCronJobs() {
        ArrayList<CronSchedulableJob> firstMainSubSlotCronJob = new ArrayList<>();
        firstMainSubSlotCronJob.add(new CronSchedulableJob(new MotechEvent("subject.new"), "cronexpression.main.first.expression"));
        ArrayList<CronSchedulableJob> secondMainSubSlotCronJob = new ArrayList<>();
        secondMainSubSlotCronJob.add(new CronSchedulableJob(new MotechEvent("subject.new.and.retry"), "cronexpression.main.second.expression"));
        ArrayList<CronSchedulableJob> thirdMainSubSlotCronJob = new ArrayList<>();
        thirdMainSubSlotCronJob.add(new CronSchedulableJob(new MotechEvent("subject.new.and.retry"), "cronexpression.main.third.expression"));
        ArrayList<CronSchedulableJob> retrySlotCronJobs = new ArrayList<>();
        retrySlotCronJobs.add(new CronSchedulableJob(new MotechEvent("subject.retry"), "cronexpression.retry.first.expression"));
        retrySlotCronJobs.add(new CronSchedulableJob(new MotechEvent("subject.retry"), "cronexpression.retry.second.expression"));

        when(firstMainSubSlotMessagesSenderJob.getCronJobs()).thenReturn(firstMainSubSlotCronJob);
        when(secondMainSubSlotMessagesSenderJob.getCronJobs()).thenReturn(secondMainSubSlotCronJob);
        when(thirdMainSubSlotMessagesSenderJob.getCronJobs()).thenReturn(thirdMainSubSlotCronJob);
        when(retrySlotMessagesSenderJob.getCronJobs()).thenReturn(retrySlotCronJobs);

        new OBDScheduler(schedulerService, firstMainSubSlotMessagesSenderJob, secondMainSubSlotMessagesSenderJob, thirdMainSubSlotMessagesSenderJob, retrySlotMessagesSenderJob);

        ArgumentCaptor<CronSchedulableJob> captor = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(schedulerService, times(5)).safeScheduleJob(captor.capture());
        List<CronSchedulableJob> cronSchedulableJobs = captor.getAllValues();
        assertEquals(5, cronSchedulableJobs.size());
        assertTrue(cronSchedulableJobs.contains(firstMainSubSlotCronJob.get(0)));
        assertTrue(cronSchedulableJobs.contains(retrySlotCronJobs.get(0)));
        assertTrue(cronSchedulableJobs.contains(retrySlotCronJobs.get(1)));
        assertTrue(cronSchedulableJobs.contains(secondMainSubSlotCronJob.get(0)));
        assertTrue(cronSchedulableJobs.contains(thirdMainSubSlotCronJob.get(0)));
    }

}
