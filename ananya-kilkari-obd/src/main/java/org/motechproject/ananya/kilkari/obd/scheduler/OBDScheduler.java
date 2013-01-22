package org.motechproject.ananya.kilkari.obd.scheduler;

import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.CronSchedulableJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@Lazy(false)
public class OBDScheduler {

    @Autowired
    public OBDScheduler(MotechSchedulerService schedulerService, NewMessagesSenderJob newMessagesSenderJob, RetryMessagesSenderJob retryMessagesSenderJob) {
        scheduleJob(schedulerService, newMessagesSenderJob);
        scheduleJob(schedulerService, retryMessagesSenderJob);
    }

    private void scheduleJob(MotechSchedulerService motechSchedulerService, MessagesSenderJob messagesSenderJob) {
        ArrayList<CronSchedulableJob> cronJobs = messagesSenderJob.getCronJobs();
        for (CronSchedulableJob cronJob : cronJobs) {
            motechSchedulerService.safeScheduleJob(cronJob);
        }
    }
}