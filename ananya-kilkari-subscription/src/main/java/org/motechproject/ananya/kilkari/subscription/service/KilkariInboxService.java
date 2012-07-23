package org.motechproject.ananya.kilkari.subscription.service;

import org.motechproject.ananya.kilkari.subscription.domain.InboxMessage;
import org.motechproject.ananya.kilkari.subscription.repository.AllInboxMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KilkariInboxService {

    private AllInboxMessages allInboxMessages;

    @Autowired
    public KilkariInboxService(AllInboxMessages allInboxMessages) {
        this.allInboxMessages = allInboxMessages;
    }

    public void newMessage(String subscriptionId, String messageId) {
        InboxMessage inboxMessage = allInboxMessages.findBySubscriptionId(subscriptionId);
        if (inboxMessage == null)
            allInboxMessages.add(new InboxMessage(subscriptionId, messageId));
        else {
            inboxMessage.setMessageId(messageId);
            allInboxMessages.update(inboxMessage);
        }
    }

    public String getMessageFor(String subscriptionId) {
        InboxMessage inboxMessage = allInboxMessages.findBySubscriptionId(subscriptionId);
        return inboxMessage == null ? null : inboxMessage.getMessageId();
    }
}
