package org.motechproject.ananya.kilkari.message.service;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.message.domain.InboxMessage;
import org.motechproject.ananya.kilkari.message.repository.AllInboxMessages;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.scheduler.domain.RunOnceSchedulableJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class InboxService {

    private AllInboxMessages allInboxMessages;
    private MotechSchedulerService motechSchedulerService;

    @Autowired
    public InboxService(AllInboxMessages allInboxMessages, MotechSchedulerService motechSchedulerService) {
        this.allInboxMessages = allInboxMessages;
        this.motechSchedulerService = motechSchedulerService;
    }

    public void newMessage(String subscriptionId, String messageId) {
        InboxMessage inboxMessage = allInboxMessages.findBySubscriptionId(subscriptionId);
        if (inboxMessage == null)
            allInboxMessages.add(new InboxMessage(subscriptionId, messageId));
        else {
            inboxMessage.update(messageId);
            allInboxMessages.update(inboxMessage);
        }
    }

    public String getMessageFor(String subscriptionId) {
        InboxMessage inboxMessage = allInboxMessages.findBySubscriptionId(subscriptionId);
        return inboxMessage == null ? null : inboxMessage.getMessageId();
    }

    public void scheduleInboxDeletion(String subscriptionId, DateTime expiryDate) {
        String subjectKey = InboxEventKeys.DELETE_INBOX;

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(MotechSchedulerService.JOB_ID_KEY, subscriptionId);
        parameters.put("0", subscriptionId);
        MotechEvent motechEvent = new MotechEvent(subjectKey, parameters);

        RunOnceSchedulableJob runOnceSchedulableJob = new RunOnceSchedulableJob(motechEvent, expiryDate.toDate());

        motechSchedulerService.safeScheduleRunOnceJob(runOnceSchedulableJob);
    }

    public void deleteInbox(String subscriptionId) {
        allInboxMessages.deleteFor(subscriptionId);
    }
}
