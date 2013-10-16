package org.motechproject.ananya.kilkari.message.domain;

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
