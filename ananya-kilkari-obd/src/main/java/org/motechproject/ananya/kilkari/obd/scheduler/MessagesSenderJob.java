package org.motechproject.ananya.kilkari.obd.scheduler;

import org.joda.time.DateTime;
import org.motechproject.event.MotechEvent;
import org.motechproject.scheduler.domain.CronSchedulableJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class MessagesSenderJob {

    private final String subject;
    private Map<SubSlot, String> cronJobDetails;
    protected static final String SUB_SLOT_KEY = "sub_slot";

    private static final Logger logger = LoggerFactory.getLogger(MessagesSenderJob.class);

    public MessagesSenderJob(String subject, Map<SubSlot, String> cronJobDetails) {
        this.subject = subject;
        this.cronJobDetails = cronJobDetails;
    }

    public ArrayList<CronSchedulableJob> getCronJobs() {
        ArrayList<CronSchedulableJob> cronJobs = new ArrayList<>();
        for (SubSlot subSlot : cronJobDetails.keySet()) {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put(SUB_SLOT_KEY, subSlot);
            MotechEvent motechEvent = new MotechEvent(subject, parameters);
            cronJobs.add(new CronSchedulableJob(motechEvent, cronJobDetails.get(subSlot)));
        }
        return cronJobs;
    }

    protected boolean canSendMessages(DateTime slotStartTime) {
        DateTime now = DateTime.now();
        boolean canSendMessages = !now.isAfter(now.withTime(slotStartTime.getHourOfDay(), slotStartTime.getMinuteOfHour(), 0, 0));
        if (!canSendMessages) {
            logger.info("Current Time : " + now + " has passed the slot start time.");
        }
        return canSendMessages;
    }
}
