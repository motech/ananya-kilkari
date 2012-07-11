package org.motechproject.ananya.kilkari.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.builder.CampaignMessageCSVBuilder;
import org.motechproject.ananya.kilkari.domain.CampaignMessage;
import org.motechproject.ananya.kilkari.gateway.OnMobileOBDGateway;
import org.motechproject.ananya.kilkari.repository.AllCampaignMessages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class CampaignMessageServiceTest {

    private CampaignMessageService campaignMessageService;

    @Mock
    private AllCampaignMessages allCampaignMessages;

    @Mock
    private OnMobileOBDGateway onMobileOBDGateway;

    @Mock
    private CampaignMessageCSVBuilder campaignMessageCSVBuilder;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        initMocks(this);
        campaignMessageService = new CampaignMessageService(allCampaignMessages, onMobileOBDGateway, campaignMessageCSVBuilder);
    }

    @Test
    public void shouldSaveTheCampaignMessageToDB() {
        String subscriptionId = "subscriptionId";
        String messageId = "messageId";
        campaignMessageService.scheduleCampaignMessage(subscriptionId, messageId, null, null);
        ArgumentCaptor<CampaignMessage> captor = ArgumentCaptor.forClass(CampaignMessage.class);
        verify(allCampaignMessages).add(captor.capture());

        CampaignMessage value = captor.getValue();

        assertEquals(subscriptionId, value.getSubscriptionId());
        assertEquals(messageId, value.getMessageId());
    }

    @Test
    public void sendNewMessagesShouldFetchNewAndDidNotCallMessages() {
        CampaignMessage expectedCampaignMessage1 = new CampaignMessage("subsriptionId1", "messageId1", "msisdn1", "operator1");
        CampaignMessage expectedCampaignMessage2 = new CampaignMessage("subsriptionId2", "messageId2", "msisdn2", "operator2");
        List<CampaignMessage> campaignMessages = Arrays.asList(expectedCampaignMessage1, expectedCampaignMessage2);
        when(allCampaignMessages.getAllUnsentNewMessages()).thenReturn(campaignMessages);
        String csvContent = "csvContent";
        when(campaignMessageCSVBuilder.getCSV(campaignMessages)).thenReturn(csvContent);
        campaignMessageService.sendNewMessages();

        verify(allCampaignMessages).getAllUnsentNewMessages();
        verify(onMobileOBDGateway).send(csvContent);

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
    public void shouldNotSaveNewCampaignMessagesAsSentIfSendingFailed() {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("myruntimeexception");

        CampaignMessage expectedCampaignMessage1 = new CampaignMessage("subsriptionId1", "messageId1", "msisdn1", "operator1");
        CampaignMessage expectedCampaignMessage2 = new CampaignMessage("subsriptionId2", "messageId2", "msisdn2", "operator2");
        List<CampaignMessage> campaignMessages = Arrays.asList(expectedCampaignMessage1, expectedCampaignMessage2);
        when(allCampaignMessages.getAllUnsentNewMessages()).thenReturn(campaignMessages);

        doThrow(new RuntimeException("myruntimeexception")).when(onMobileOBDGateway).send(anyString());

        try {
            campaignMessageService.sendNewMessages();
        } finally {
            verify(allCampaignMessages, never()).update(any(CampaignMessage.class));
        }
    }

    @Test
    public void sendRetryMessagesShouldFetchRetryMessages() {
        CampaignMessage expectedCampaignMessage1 = new CampaignMessage("subsriptionId1", "messageId1", "msisdn1", "operator1");
        CampaignMessage expectedCampaignMessage2 = new CampaignMessage("subsriptionId2", "messageId2", "msisdn2", "operator2");
        List<CampaignMessage> campaignMessages = Arrays.asList(expectedCampaignMessage1, expectedCampaignMessage2);
        when(allCampaignMessages.getAllUnsentRetryMessages()).thenReturn(campaignMessages);
        String csvContent = "csvContent";
        when(campaignMessageCSVBuilder.getCSV(campaignMessages)).thenReturn(csvContent);


        campaignMessageService.sendRetryMessages();

        verify(allCampaignMessages).getAllUnsentRetryMessages();
        ArgumentCaptor<CampaignMessage> captor = ArgumentCaptor.forClass(CampaignMessage.class);
        verify(allCampaignMessages, times(2)).update(captor.capture());

        verify(onMobileOBDGateway).send(csvContent);

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
    public void shouldNotSaveRetryCampaignMessagesAsSentIfSendingFailed() {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("myruntimeexception");

        CampaignMessage expectedCampaignMessage1 = new CampaignMessage("subsriptionId1", "messageId1", "msisdn1", "operator1");
        CampaignMessage expectedCampaignMessage2 = new CampaignMessage("subsriptionId2", "messageId2", "msisdn2", "operator2");
        List<CampaignMessage> campaignMessages = Arrays.asList(expectedCampaignMessage1, expectedCampaignMessage2);
        when(allCampaignMessages.getAllUnsentRetryMessages()).thenReturn(campaignMessages);

        doThrow(new RuntimeException("myruntimeexception")).when(onMobileOBDGateway).send(anyString());

        try {
            campaignMessageService.sendRetryMessages();
        } finally {
            verify(allCampaignMessages, never()).update(any(CampaignMessage.class));
        }
    }

    @Test
    public void shouldNotSendNewMessagesIfNoneExist() {
        when(allCampaignMessages.getAllUnsentNewMessages()).thenReturn(new ArrayList<CampaignMessage>());

        campaignMessageService.sendNewMessages();

        verify(allCampaignMessages, never()).update(any(CampaignMessage.class));
        verifyZeroInteractions(onMobileOBDGateway);
    }

    @Test
    public void shouldNotSendRetryMessagesIfNoneExist() {
        when(allCampaignMessages.getAllUnsentRetryMessages()).thenReturn(new ArrayList<CampaignMessage>());

        campaignMessageService.sendRetryMessages();

        verify(allCampaignMessages, never()).update(any(CampaignMessage.class));
        verifyZeroInteractions(onMobileOBDGateway);
    }
}
