package org.motechproject.ananya.kilkari.service;

import junit.framework.Assert;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.subscription.domain.InboxMessage;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionEventKeys;
import org.motechproject.ananya.kilkari.subscription.repository.AllInboxMessages;
import org.motechproject.ananya.kilkari.subscription.service.KilkariInboxService;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.RunOnceSchedulableJob;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class KilkariInboxServiceTest {

    private KilkariInboxService kilkariInboxService;
    @Mock
    private AllInboxMessages allInboxMessages;
    @Mock
    private MotechSchedulerService motechSchedulerService;

    @Before
    public void setup(){
        initMocks(this);
        kilkariInboxService = new KilkariInboxService(allInboxMessages, motechSchedulerService);
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

        verify(inboxMessage).update(messageId);
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

    @Test
    public void shouldScheduleInboxDeletionEvent() {
        String subscriptionId = "subscriptionId";
        DateTime messageExpiryDate = DateTime.now().plusWeeks(1);
        Subscription mockedSubscription = mock(Subscription.class);

        when(mockedSubscription.getSubscriptionId()).thenReturn(subscriptionId);
        when(mockedSubscription.currentWeeksMessageExpiryDate()).thenReturn(messageExpiryDate);

        kilkariInboxService.scheduleInboxDeletion(mockedSubscription);

        ArgumentCaptor<RunOnceSchedulableJob> runOnceSchedulableJobArgumentCaptor = ArgumentCaptor.forClass(RunOnceSchedulableJob.class);
        verify(motechSchedulerService).safeScheduleRunOnceJob(runOnceSchedulableJobArgumentCaptor.capture());
        RunOnceSchedulableJob runOnceSchedulableJob = runOnceSchedulableJobArgumentCaptor.getValue();
        Assert.assertEquals(SubscriptionEventKeys.DELETE_INBOX, runOnceSchedulableJob.getMotechEvent().getSubject());
        Assert.assertEquals(subscriptionId, runOnceSchedulableJob.getMotechEvent().getParameters().get(MotechSchedulerService.JOB_ID_KEY));
        Assert.assertEquals(subscriptionId, runOnceSchedulableJob.getMotechEvent().getParameters().get("0"));
        Assert.assertEquals(messageExpiryDate.toDate(), runOnceSchedulableJob.getStartDate());
    }

    @Test
    public void shouldDeleteInbox(){
        String subscriptionId = "subscriptionId";

        kilkariInboxService.deleteInbox(subscriptionId);

        verify(allInboxMessages).deleteFor(subscriptionId);
    }
}
