package org.motechproject.ananya.kilkari.obd.scheduler;

import org.joda.time.DateTime;
import org.motechproject.event.MotechEvent;
import org.motechproject.scheduler.domain.CronSchedulableJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class MessagesSenderJob {

    private final String subject;
    private final String cronExpression;

    private static final Logger logger = LoggerFactory.getLogger(MessagesSenderJob.class);

    public MessagesSenderJob(String subject, String cronExpression) {
        this.subject = subject;
        this.cronExpression = cronExpression;
    }

    public CronSchedulableJob getCronJob() {
        MotechEvent motechEvent = new MotechEvent(subject);
        return new CronSchedulableJob(motechEvent, cronExpression);
    }

    protected boolean canSendMessages(int slotStartTimeHours, int slotStartTimeMinutes) {
        DateTime now = DateTime.now();
        boolean canSendMessages = !now.isAfter(now.withTime(slotStartTimeHours, slotStartTimeMinutes, 0, 0));
        if(!canSendMessages) {
            logger.info("Current Time : " + now + " has passed the slot start time.");
        }
        return canSendMessages;
    }
}
