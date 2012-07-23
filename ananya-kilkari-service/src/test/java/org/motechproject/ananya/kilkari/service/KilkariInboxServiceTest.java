package org.motechproject.ananya.kilkari.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.subscription.domain.InboxMessage;
import org.motechproject.ananya.kilkari.subscription.repository.AllInboxMessages;
import org.motechproject.ananya.kilkari.subscription.service.KilkariInboxService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class KilkariInboxServiceTest {

    private KilkariInboxService kilkariInboxService;
    @Mock
    private AllInboxMessages allInboxMessages;

    @Before
    public void setup(){
        initMocks(this);
        kilkariInboxService = new KilkariInboxService(allInboxMessages);
    }

    @Test
    public void shouldAddNewMessageToInbox_whenMessageDoesNotAlreadyExistForThatSubscriptionId(){
        String subscriptionId = "subscriptionId";
        String messageId = "WEEK13";

        when(allInboxMessages.findBySubscriptionId(subscriptionId)).thenReturn(null);

        kilkariInboxService.newMessage(subscriptionId, messageId);

        ArgumentCaptor<InboxMessage> inboxMessageArgumentCaptor = ArgumentCaptor.forClass(InboxMessage.class);
        verify(allInboxMessages).add(inboxMessageArgumentCaptor.capture());
        InboxMessage inboxMessage = inboxMessageArgumentCaptor.getValue();
        assertEquals(subscriptionId, inboxMessage.getSubscriptionId());
        assertEquals(messageId, inboxMessage.getMessageId());
    }

    @Test
    public void shouldUpdateMessageInInbox_whenMessageAlreadyExistsForThatSubscriptionId(){
        String subscriptionId = "subscriptionId";
        String messageId = "WEEK13";
        InboxMessage inboxMessage = mock(InboxMessage.class);

        when(allInboxMessages.findBySubscriptionId(subscriptionId)).thenReturn(inboxMessage);

        kilkariInboxService.newMessage(subscriptionId, messageId);

        verify(inboxMessage).setMessageId(messageId);
        verify(allInboxMessages).update(inboxMessage);
    }
    
    @Test
    public void shouldReturnMessageForASubscriptionId(){
        String subscriptionId = "subsId";
        String expectedMessageId = "week13";
        when(allInboxMessages.findBySubscriptionId(subscriptionId)).thenReturn(new InboxMessage(subscriptionId, expectedMessageId));

        String actualMessageId = kilkariInboxService.getMessageFor(subscriptionId);

        assertEquals(expectedMessageId, actualMessageId);
    }

    @Test
    public void shouldReturnNullMessageForASubscriptionIdDoesNotExist(){
        String subscriptionId = "subsId";
        InboxMessage inboxMessage = null;

        when(allInboxMessages.findBySubscriptionId(subscriptionId)).thenReturn(inboxMessage);

        String actualMessageId = kilkariInboxService.getMessageFor(subscriptionId);

        assertNull(actualMessageId);
    }
}
