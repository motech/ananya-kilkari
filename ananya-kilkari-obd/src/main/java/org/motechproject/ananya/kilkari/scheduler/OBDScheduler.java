package org.motechproject.ananya.kilkari.scheduler;

import org.motechproject.scheduler.MotechSchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Lazy(false)
public class OBDScheduler {

    @Autowired
    public OBDScheduler(MotechSchedulerService schedulerService, FreshMessagesSenderJob freshMessagesSenderJob, RetryMessagesSenderJob retryMessagesSenderJob) {
        scheduleJob(schedulerService, freshMessagesSenderJob);
        scheduleJob(schedulerService, retryMessagesSenderJob);
    }

    private void scheduleJob(MotechSchedulerService motechSchedulerService, MessagesSenderJob messagesSenderJob) {
        motechSchedulerService.safeScheduleJob(messagesSenderJob.getCronJob());
    }
}