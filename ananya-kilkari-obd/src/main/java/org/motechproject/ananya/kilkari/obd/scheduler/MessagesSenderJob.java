package org.motechproject.ananya.kilkari.obd.scheduler;

import org.motechproject.scheduler.domain.CronSchedulableJob;
import org.motechproject.scheduler.domain.MotechEvent;

public abstract class MessagesSenderJob {

    private final String subject;
    private final String cronExpression;

    public MessagesSenderJob(String subject, String cronExpression) {
        this.subject = subject;
        this.cronExpression = cronExpression;
    }

    public CronSchedulableJob getCronJob() {
        MotechEvent motechEvent = new MotechEvent(subject);
        return new CronSchedulableJob(motechEvent, cronExpression);
    }
}
