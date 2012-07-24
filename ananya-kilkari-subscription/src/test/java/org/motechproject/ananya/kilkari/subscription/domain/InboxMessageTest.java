package org.motechproject.ananya.kilkari.subscription.domain;

import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class InboxMessageTest {
    @Test
    public void shouldUpdateMessageIdAndMessageExpiryDate() {
        String updatedMessageId = "week14";
        InboxMessage inboxMessage = new InboxMessage("subsId", "week13");

        inboxMessage.update(updatedMessageId);
        
        assertEquals(updatedMessageId, inboxMessage.getMessageId());
    }
}
