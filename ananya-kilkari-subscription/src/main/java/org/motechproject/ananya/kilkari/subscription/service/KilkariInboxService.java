package org.motechproject.ananya.kilkari.subscription.service;

import org.motechproject.ananya.kilkari.subscription.domain.InboxMessage;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionEventKeys;
import org.motechproject.ananya.kilkari.subscription.repository.AllInboxMessages;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.scheduler.domain.RunOnceSchedulableJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;

@Service
public class KilkariInboxService {

    private AllInboxMessages allInboxMessages;
    private MotechSchedulerService motechSchedulerService;

    @Autowired
    public KilkariInboxService(AllInboxMessages allInboxMessages, MotechSchedulerService motechSchedulerService) {
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

    public void scheduleInboxDeletion(Subscription subscription) {
        String subjectKey = SubscriptionEventKeys.DELETE_INBOX;
        Date startDate = subscription.currentWeeksMessageExpiryDate().toDate();

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(MotechSchedulerService.JOB_ID_KEY, subscription.getSubscriptionId());
        parameters.put("0", subscription.getSubscriptionId());
        MotechEvent motechEvent = new MotechEvent(subjectKey, parameters);

        RunOnceSchedulableJob runOnceSchedulableJob = new RunOnceSchedulableJob(motechEvent, startDate);

        motechSchedulerService.safeScheduleRunOnceJob(runOnceSchedulableJob);
    }

    public void deleteInbox(String subscriptionId) {
        allInboxMessages.deleteFor(subscriptionId);
    }
}
