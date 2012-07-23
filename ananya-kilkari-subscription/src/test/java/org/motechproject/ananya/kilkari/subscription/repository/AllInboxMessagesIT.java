package org.motechproject.ananya.kilkari.subscription.repository;

import org.junit.Test;
import org.motechproject.ananya.kilkari.subscription.domain.InboxMessage;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

public class AllInboxMessagesIT extends SpringIntegrationTest {

    @Autowired
    private AllInboxMessages allInboxMessages;

    @Test
    public void shouldFindAInboxMessageBySubscriptionId(){
        String subscriptionId = "subscriptionId";
        String messageId = "WEEK13";
        InboxMessage inboxMessage = new InboxMessage(subscriptionId, messageId);
        kilkariSubscriptionDbConnector.create(inboxMessage);
        markForDeletion(inboxMessage);

        InboxMessage actualInboxMessage = allInboxMessages.findBySubscriptionId(subscriptionId);

        assertEquals(subscriptionId, actualInboxMessage.getSubscriptionId());
        assertEquals(messageId, actualInboxMessage.getMessageId());

    }

    // TODO write a test for deletion
}
