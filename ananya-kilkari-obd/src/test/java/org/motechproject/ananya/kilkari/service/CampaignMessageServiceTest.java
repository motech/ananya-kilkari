package org.motechproject.ananya.kilkari.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.domain.CampaignMessage;
import org.motechproject.ananya.kilkari.repository.AllCampaignMessages;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CampaignMessageServiceTest {

    private CampaignMessageService campaignMessageService;

    @Mock
    private AllCampaignMessages allCampaignMessages;

    @Before
    public void setUp() {
        initMocks(this);
        campaignMessageService = new CampaignMessageService(allCampaignMessages);
    }

    @Test
    public void shouldSaveTheCampaignMessageToDB() {
        String subscriptionId = "subscriptionId";
        String messageId = "messageId";
        campaignMessageService.scheduleCampaignMessage(subscriptionId, messageId);
        ArgumentCaptor<CampaignMessage> captor = ArgumentCaptor.forClass(CampaignMessage.class);
        verify(allCampaignMessages).add(captor.capture());

        CampaignMessage value = captor.getValue();

        assertEquals(subscriptionId, value.getSubscriptionId());
        assertEquals(messageId, value.getMessageId());
    }

    @Test
    public void sendFreshMessagesShouldFetchFreshMessages() {
        CampaignMessage expectedCampaignMessage1 = new CampaignMessage("subsriptionId1", "messageId1");
        CampaignMessage expectedCampaignMessage2 = new CampaignMessage("subsriptionId2", "messageId2");
        when(allCampaignMessages.getAllUnsentNewMessages()).thenReturn(Arrays.asList(expectedCampaignMessage1, expectedCampaignMessage2));

        campaignMessageService.sendFreshMessages();

        verify(allCampaignMessages).getAllUnsentNewMessages();
        ArgumentCaptor<CampaignMessage> captor = ArgumentCaptor.forClass(CampaignMessage.class);
        verify(allCampaignMessages, times(2)).update(captor.capture());

        List<CampaignMessage> actualCampaignMessages = captor.getAllValues();
        assertEquals(2, actualCampaignMessages.size());
        CampaignMessage actualCampaignMessage1 = actualCampaignMessages.get(0);
        CampaignMessage actualCampaignMessage2 = actualCampaignMessages.get(1);

        assertEquals(expectedCampaignMessage1.getSubscriptionId(), actualCampaignMessage1.getSubscriptionId());
        assertEquals(expectedCampaignMessage1.getMessageId(), actualCampaignMessage1.getMessageId());
        assertTrue(actualCampaignMessage1.isSent());

        assertEquals(expectedCampaignMessage2.getSubscriptionId(), actualCampaignMessage2.getSubscriptionId());
        assertEquals(expectedCampaignMessage2.getMessageId(), actualCampaignMessage2.getMessageId());
        assertTrue(actualCampaignMessage2.isSent());
    }

    @Test
    public void sendRetryMessagesShouldFetchRetryMessages() {
        CampaignMessage expectedCampaignMessage1 = new CampaignMessage("subsriptionId1", "messageId1");
        CampaignMessage expectedCampaignMessage2 = new CampaignMessage("subsriptionId2", "messageId2");
        when(allCampaignMessages.getAllUnsentRetryMessages()).thenReturn(Arrays.asList(expectedCampaignMessage1, expectedCampaignMessage2));

        campaignMessageService.sendRetryMessages();

        verify(allCampaignMessages).getAllUnsentRetryMessages();
        ArgumentCaptor<CampaignMessage> captor = ArgumentCaptor.forClass(CampaignMessage.class);
        verify(allCampaignMessages, times(2)).update(captor.capture());

        List<CampaignMessage> actualCampaignMessages = captor.getAllValues();
        assertEquals(2, actualCampaignMessages.size());
        CampaignMessage actualCampaignMessage1 = actualCampaignMessages.get(0);
        CampaignMessage actualCampaignMessage2 = actualCampaignMessages.get(1);

        assertEquals(expectedCampaignMessage1.getSubscriptionId(), actualCampaignMessage1.getSubscriptionId());
        assertEquals(expectedCampaignMessage1.getMessageId(), actualCampaignMessage1.getMessageId());
        assertTrue(actualCampaignMessage1.isSent());

        assertEquals(expectedCampaignMessage2.getSubscriptionId(), actualCampaignMessage2.getSubscriptionId());
        assertEquals(expectedCampaignMessage2.getMessageId(), actualCampaignMessage2.getMessageId());
        assertTrue(actualCampaignMessage2.isSent());
    }
}
