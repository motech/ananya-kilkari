package org.motechproject.ananya.kilkari.message.service;

import junit.framework.Assert;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.message.domain.InboxMessage;
import org.motechproject.ananya.kilkari.message.repository.AllInboxMessages;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.RunOnceSchedulableJob;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class InboxServiceTest {

    private InboxService inboxService;
    @Mock
    private AllInboxMessages allInboxMessages;
    @Mock
    private MotechSchedulerService motechSchedulerService;

    @Before
    public void setup(){
        initMocks(this);
        inboxService = new InboxService(allInboxMessages, motechSchedulerService);
    }

    @Test
    public void shouldAddNewMessageToInbox_whenMessageDoesNotAlreadyExistForThatSubscriptionId(){
        String subscriptionId = "subscriptionId";
        String messageId = "WEEK13";

        when(allInboxMessages.findBySubscriptionId(subscriptionId)).thenReturn(null);

        inboxService.newMessage(subscriptionId, messageId);

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

        inboxService.newMessage(subscriptionId, messageId);

        verify(inboxMessage).update(messageId);
        verify(allInboxMessages).update(inboxMessage);
    }
    
    @Test
    public void shouldReturnMessageForASubscriptionId(){
        String subscriptionId = "subsId";
        String expectedMessageId = "week13";
        when(allInboxMessages.findBySubscriptionId(subscriptionId)).thenReturn(new InboxMessage(subscriptionId, expectedMessageId));

        String actualMessageId = inboxService.getMessageFor(subscriptionId);

        assertEquals(expectedMessageId, actualMessageId);
    }

    @Test
    public void shouldReturnNullMessageForASubscriptionIdDoesNotExist(){
        String subscriptionId = "subsId";
        InboxMessage inboxMessage = null;

        when(allInboxMessages.findBySubscriptionId(subscriptionId)).thenReturn(inboxMessage);

        String actualMessageId = inboxService.getMessageFor(subscriptionId);

        assertNull(actualMessageId);
    }

    @Test
    public void shouldScheduleInboxDeletionEvent() {
        String subscriptionId = "subscriptionId";
        DateTime messageExpiryDate = DateTime.now().plusWeeks(1);

        inboxService.scheduleInboxDeletion(subscriptionId, messageExpiryDate);

        ArgumentCaptor<RunOnceSchedulableJob> runOnceSchedulableJobArgumentCaptor = ArgumentCaptor.forClass(RunOnceSchedulableJob.class);
        verify(motechSchedulerService).safeScheduleRunOnceJob(runOnceSchedulableJobArgumentCaptor.capture());
        RunOnceSchedulableJob runOnceSchedulableJob = runOnceSchedulableJobArgumentCaptor.getValue();
        Assert.assertEquals(InboxEventKeys.DELETE_INBOX, runOnceSchedulableJob.getMotechEvent().getSubject());
        Assert.assertEquals(subscriptionId, runOnceSchedulableJob.getMotechEvent().getParameters().get(MotechSchedulerService.JOB_ID_KEY));
        Assert.assertEquals(subscriptionId, runOnceSchedulableJob.getMotechEvent().getParameters().get("0"));
        Assert.assertEquals(messageExpiryDate.toDate(), runOnceSchedulableJob.getStartDate());
    }

    @Test
    public void shouldDeleteInbox(){
        String subscriptionId = "subscriptionId";

        inboxService.deleteInbox(subscriptionId);

        verify(allInboxMessages).deleteFor(subscriptionId);
    }
}
