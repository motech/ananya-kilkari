package org.motechproject.ananya.kilkari.obd.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.obd.domain.*;
import org.motechproject.ananya.kilkari.obd.repository.AllCampaignMessages;
import org.motechproject.ananya.kilkari.obd.repository.OnMobileOBDGateway;
import org.motechproject.ananya.kilkari.obd.service.request.FailedCallReport;
import org.motechproject.ananya.kilkari.obd.service.utils.RetryTask;
import org.motechproject.ananya.kilkari.obd.service.utils.RetryTaskExecutor;
import org.motechproject.ananya.kilkari.reporting.service.ReportingService;
import org.motechproject.ananya.reports.kilkari.contract.request.CallDetailsReportRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class CampaignMessageServiceTest {
    public static final int MAX_OBD_RETRY_DAYS = 7;

    private CampaignMessageService campaignMessageService;

    @Mock
    private AllCampaignMessages allCampaignMessages;

    @Mock
    private OnMobileOBDGateway onMobileOBDGateway;

    @Mock
    private CampaignMessageCSVBuilder campaignMessageCSVBuilder;

    @Mock
    private OBDProperties obdProperties;

    @Mock
    private ReportingService reportingService;

    @Mock
    private RetryTaskExecutor retryTaskExecutor;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        initMocks(this);
        campaignMessageService = new CampaignMessageService(allCampaignMessages, onMobileOBDGateway, campaignMessageCSVBuilder, reportingService, obdProperties, retryTaskExecutor);
    }

    @Test
    public void shouldSaveTheCampaignMessageToDB() {
        String subscriptionId = "subscriptionId";
        String messageId = "messageId";
        campaignMessageService.scheduleCampaignMessage(subscriptionId, messageId, "1234567890", null, DateTime.now().plusDays(2), DateTime.now());
        ArgumentCaptor<CampaignMessage> captor = ArgumentCaptor.forClass(CampaignMessage.class);
        verify(allCampaignMessages).add(captor.capture());

        CampaignMessage value = captor.getValue();

        assertEquals(subscriptionId, value.getSubscriptionId());
        assertEquals(messageId, value.getMessageId());
    }

    @Test
    public void shouldFindACampaignMessageBasedOnSubscriptionIdAndMessageId() {
        String subscriptionId = "subscriptionId";
        String messageId = "messageId";
        CampaignMessage campaignMessage = mock(CampaignMessage.class);
        when(allCampaignMessages.find(subscriptionId, messageId)).thenReturn(campaignMessage);

        CampaignMessage actualCampaignMessage = campaignMessageService.find(subscriptionId, messageId);

        verify(allCampaignMessages).find(subscriptionId, messageId);
        assertEquals(campaignMessage, actualCampaignMessage);
    }

    @Test
    public void sendFirstMainSubSlotMessagesShouldFetchNewMessagesAndSendOnlyGivenPercentage() {
        CampaignMessage campaignMessage1 = new CampaignMessage("subsriptionId1", "messageId1", DateTime.now().minusMinutes(4), "1234567890", "operator1", DateTime.now().plusDays(2));
        CampaignMessage campaignMessage2 = new CampaignMessage("subsriptionId2", "messageId2", DateTime.now().minusMinutes(3), "1234567891", "operator2", DateTime.now().plusDays(2));
        CampaignMessage campaignMessage3 = new CampaignMessage("subsriptionId3", "messageId3", DateTime.now().minusMinutes(2), "1234567892", "operator3", DateTime.now().plusDays(2));
        CampaignMessage campaignMessage4 = new CampaignMessage("subsriptionId4", "messageId4", DateTime.now().minusMinutes(1), "1234567893", "operator4", DateTime.now().plusDays(2));
        List<CampaignMessage> campaignMessages = Arrays.asList(campaignMessage1, campaignMessage2, campaignMessage3, campaignMessage4);
        List<CampaignMessage> expectedCampaignMessagesToBeSent = Arrays.asList(campaignMessage1, campaignMessage2);
        when(allCampaignMessages.getAllUnsentNewMessages()).thenReturn(campaignMessages);
        when(obdProperties.getSlotMessagePercentageFor(MainSubSlot.ONE)).thenReturn(30);
        String csvContent = "csvContent";
        when(campaignMessageCSVBuilder.getCSV(expectedCampaignMessagesToBeSent)).thenReturn(csvContent);

        campaignMessageService.sendFirstMainSubSlotMessages(MainSubSlot.ONE);

        verify(campaignMessageCSVBuilder).getCSV(expectedCampaignMessagesToBeSent);
        verify(allCampaignMessages).getAllUnsentNewMessages();
        verify(onMobileOBDGateway).sendMessages(csvContent, MainSubSlot.ONE);
        verifyCampaignMessageUpdate(expectedCampaignMessagesToBeSent);
    }

    @Test
    public void shouldNotSaveNewCampaignMessagesAsSentIfSendingFailed() {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("myruntimeexception");

        CampaignMessage expectedCampaignMessage1 = new CampaignMessage("subsriptionId1", "messageId1", DateTime.now(), "1234567890", "operator1", DateTime.now().plusDays(2));
        CampaignMessage expectedCampaignMessage2 = new CampaignMessage("subsriptionId2", "messageId2", DateTime.now(), "1234567891", "operator2", DateTime.now().plusDays(2));
        List<CampaignMessage> campaignMessages = Arrays.asList(expectedCampaignMessage1, expectedCampaignMessage2);
        when(obdProperties.getSlotMessagePercentageFor(MainSubSlot.ONE)).thenReturn(100);
        when(allCampaignMessages.getAllUnsentNewMessages()).thenReturn(campaignMessages);

        doThrow(new RuntimeException("myruntimeexception")).when(onMobileOBDGateway).sendMessages(anyString(), any(MainSubSlot.class));

        try {
            campaignMessageService.sendFirstMainSubSlotMessages(MainSubSlot.ONE);
        } finally {
            verify(allCampaignMessages, never()).update(any(CampaignMessage.class));
        }
    }

    @Test
    public void sendRetrySlotMessagesShouldFetchRetryMessages() {
        CampaignMessage expectedCampaignMessage1 = new CampaignMessage("subsriptionId1", "messageId1", DateTime.now(), "1234567890", "operator1", DateTime.now().plusDays(2));
        CampaignMessage expectedCampaignMessage2 = new CampaignMessage("subsriptionId2", "messageId2", DateTime.now(), "1234567891", "operator2", DateTime.now().plusDays(2));
        List<CampaignMessage> campaignMessages = Arrays.asList(expectedCampaignMessage1, expectedCampaignMessage2);
        when(allCampaignMessages.getAllUnsentNAMessages()).thenReturn(campaignMessages);
        String csvContent = "csvContent";
        when(campaignMessageCSVBuilder.getCSV(campaignMessages)).thenReturn(csvContent);

        campaignMessageService.sendRetrySlotMessages(RetrySubSlot.ONE);

        verify(allCampaignMessages).getAllUnsentNAMessages();
        verify(onMobileOBDGateway).sendMessages(csvContent, RetrySubSlot.ONE);
        verifyCampaignMessageUpdate(campaignMessages);
    }

    @Test
    public void shouldNotSaveRetryCampaignMessagesAsSentIfSendingFailed() {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("myruntimeexception");

        CampaignMessage expectedCampaignMessage1 = new CampaignMessage("subsriptionId1", "messageId1", DateTime.now(), "1234567890", "operator1", DateTime.now().plusDays(2));
        CampaignMessage expectedCampaignMessage2 = new CampaignMessage("subsriptionId2", "messageId2", DateTime.now(), "1234567891", "operator2", DateTime.now().plusDays(2));
        List<CampaignMessage> campaignMessages = Arrays.asList(expectedCampaignMessage1, expectedCampaignMessage2);
        when(allCampaignMessages.getAllUnsentNAMessages()).thenReturn(campaignMessages);

        doThrow(new RuntimeException("myruntimeexception")).when(onMobileOBDGateway).sendMessages(anyString(), any(RetrySubSlot.class));

        try {
            campaignMessageService.sendRetrySlotMessages(RetrySubSlot.THREE);
        } finally {
            verify(allCampaignMessages, never()).update(any(CampaignMessage.class));
        }
    }

    @Test
    public void shouldNotSendFirstMainSubSlotMessagesIfNoneExist() {
        when(allCampaignMessages.getAllUnsentNewMessages()).thenReturn(new ArrayList<CampaignMessage>());

        campaignMessageService.sendFirstMainSubSlotMessages(MainSubSlot.ONE);

        verify(allCampaignMessages, never()).update(any(CampaignMessage.class));
        verifyZeroInteractions(onMobileOBDGateway);
    }

    @Test
    public void shouldNotSendRetrySlotMessagesIfNoneExist() {
        when(allCampaignMessages.getAllUnsentNAMessages()).thenReturn(new ArrayList<CampaignMessage>());

        campaignMessageService.sendRetrySlotMessages(RetrySubSlot.ONE);

        verify(allCampaignMessages, never()).update(any(CampaignMessage.class));
        verifyZeroInteractions(onMobileOBDGateway);
    }

    @Test
    public void shouldDeleteTheCampaignMessageOnlyIfItExists() {
        String subscriptionId = "subscriptionId";
        String campaignId = "campaignId";

        when(allCampaignMessages.find(subscriptionId, campaignId)).thenReturn(new CampaignMessage(subscriptionId, campaignId, DateTime.now(), "1234567890", null, DateTime.now().plusDays(2)));

        campaignMessageService.deleteCampaignMessageIfExists(subscriptionId, campaignId);

        ArgumentCaptor<CampaignMessage> campaignMessageArgumentCaptor = ArgumentCaptor.forClass(CampaignMessage.class);
        verify(allCampaignMessages).delete(campaignMessageArgumentCaptor.capture());
        CampaignMessage campaignMessage = campaignMessageArgumentCaptor.getValue();

        assertEquals(subscriptionId, campaignMessage.getSubscriptionId());
        assertEquals(campaignId, campaignMessage.getMessageId());
    }

    @Test
    public void shouldNotDeleteTheCampaignMessageIfItDoesNotExists() {
        String subscriptionId = "subscriptionId";
        String campaignId = "campaignId";

        when(allCampaignMessages.find(subscriptionId, campaignId)).thenReturn(null);

        campaignMessageService.deleteCampaignMessageIfExists(subscriptionId, campaignId);

        verify(allCampaignMessages, never()).delete(any(CampaignMessage.class));
    }

    @Test
    public void shouldDeleteACampaignMessage() {
        CampaignMessage campaignMessage = new CampaignMessage();

        campaignMessageService.deleteCampaignMessage(campaignMessage);

        verify(allCampaignMessages).delete(campaignMessage);
    }

    @Test
    public void shouldUpdateACampaignMessage() {
        CampaignMessage campaignMessage = new CampaignMessage();
        campaignMessageService.update(campaignMessage);

        verify(allCampaignMessages).update(campaignMessage);
    }

    @Test
    public void shouldUpdateCampaignMessageStatus() {
        String subscriptionId = "subscriptionId";
        String campaignId = "WEEK13";
        ValidFailedCallReport failedCallReport = new ValidFailedCallReport(subscriptionId, "msisdn", campaignId, CampaignMessageStatus.NA, DateTime.now());
        DateTime now = DateTime.now();
        CampaignMessage campaignMessage = new CampaignMessage(subscriptionId, campaignId, now.minusDays(4), "1234567890", "airtel", now.plusDays(1));

        when(obdProperties.getMaximumOBDRetryDays()).thenReturn(MAX_OBD_RETRY_DAYS);
        when(allCampaignMessages.find(subscriptionId, campaignId)).thenReturn(campaignMessage);

        campaignMessageService.processValidCallDeliveryFailureRecords(failedCallReport);

        verify(allCampaignMessages).find(subscriptionId, campaignId);
        verify(reportingService).reportCampaignMessageDeliveryStatus(any(CallDetailsReportRequest.class));
        ArgumentCaptor<CampaignMessage> campaignMessageArgumentCaptor = ArgumentCaptor.forClass(CampaignMessage.class);
        verify(allCampaignMessages).update(campaignMessageArgumentCaptor.capture());
        CampaignMessage actualCampaignMessage = campaignMessageArgumentCaptor.getValue();
        assertEquals(CampaignMessageStatus.NA, actualCampaignMessage.getStatus());
    }

    @Test
    public void shouldNotUpdateCampaignMessageStatusIfCampaignMessageIsNull() {
        String subscriptionId = "subscriptionId";
        String campaignId = "WEEK13";
        ValidFailedCallReport failedCallReport = new ValidFailedCallReport(subscriptionId, "msisdn", campaignId, CampaignMessageStatus.NA, DateTime.now());

        when(allCampaignMessages.find(subscriptionId, campaignId)).thenReturn(null);

        campaignMessageService.processValidCallDeliveryFailureRecords(failedCallReport);

        verify(allCampaignMessages).find(subscriptionId, campaignId);
        verify(reportingService, never()).reportCampaignMessageDeliveryStatus(any(CallDetailsReportRequest.class));
        verify(allCampaignMessages, never()).update(any(CampaignMessage.class));
    }

    @Test
    public void shouldDeleteAllExistingMessagesForASubscription() {
        String subscriptionId = "subscriptionId";

        campaignMessageService.deleteCampaignMessagesFor(subscriptionId);

        verify(allCampaignMessages).removeAll(subscriptionId);
    }

    @Test
    public void shouldGetStatusCode() {
        FailedCallReport failedCallReport = new FailedCallReport("subscriptionId", "msisdn", "WEEK13", "iu_dnp");

        failedCallReport.setStatusCode("iu_dnc");
        when(obdProperties.getCampaignMessageStatusFor("iu_dnc")).thenReturn(CampaignMessageStatus.ND);
        assertEquals(CampaignMessageStatus.ND, campaignMessageService.getCampaignMessageStatusFor(failedCallReport));

        failedCallReport.setStatusCode("iu_dnp");
        when(obdProperties.getCampaignMessageStatusFor("iu_dnp")).thenReturn(CampaignMessageStatus.NA);
        assertEquals(CampaignMessageStatus.NA, campaignMessageService.getCampaignMessageStatusFor(failedCallReport));

        failedCallReport.setStatusCode("iu_dnc123");
        CampaignMessageStatus defaultStatus = CampaignMessageStatus.NA;
        when(obdProperties.getDefaultCampaignMessageStatus()).thenReturn(defaultStatus);
        assertEquals(defaultStatus, campaignMessageService.getCampaignMessageStatusFor(failedCallReport));


        failedCallReport.setStatusCode("iu_dnc123");
        when(obdProperties.getDefaultCampaignMessageStatus()).thenReturn(null);
        assertNull(campaignMessageService.getCampaignMessageStatusFor(failedCallReport));
    }

    @Test
    public void shouldFetchNewAndNAMessagesWhenSendingThirdMainSubSlotMessages() {
        CampaignMessage expectedCampaignMessage1 = new CampaignMessage("subsriptionId1", "messageId1", DateTime.now(), "1234567890", "operator1", DateTime.now().plusDays(2));
        CampaignMessage expectedCampaignMessage2 = new CampaignMessage("subsriptionId2", "messageId2", DateTime.now(), "1234567891", "operator2", DateTime.now().plusDays(2));
        List<CampaignMessage> campaignMessages = Arrays.asList(expectedCampaignMessage1, expectedCampaignMessage2);
        when(allCampaignMessages.getAllUnsentNewAndNAMessages()).thenReturn(campaignMessages);
        String csvContent = "csvContent";
        when(campaignMessageCSVBuilder.getCSV(campaignMessages)).thenReturn(csvContent);

        campaignMessageService.sendThirdMainSubSlotMessages(MainSubSlot.THREE);

        verify(allCampaignMessages).getAllUnsentNewAndNAMessages();
        verify(onMobileOBDGateway).sendMessages(csvContent, MainSubSlot.THREE);
        verifyCampaignMessageUpdate(campaignMessages);
    }

    @Test
    public void shouldNotSaveNewAndNACampaignMessagesAsSentIfSendingFailed() {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("myruntimeexception");

        CampaignMessage expectedCampaignMessage1 = new CampaignMessage("subsriptionId1", "messageId1", DateTime.now(), "1234567890", "operator1", DateTime.now().plusDays(2));
        CampaignMessage expectedCampaignMessage2 = new CampaignMessage("subsriptionId2", "messageId2", DateTime.now(), "1234567891", "operator2", DateTime.now().plusDays(2));
        List<CampaignMessage> campaignMessages = Arrays.asList(expectedCampaignMessage1, expectedCampaignMessage2);
        when(allCampaignMessages.getAllUnsentNewAndNAMessages()).thenReturn(campaignMessages);

        doThrow(new RuntimeException("myruntimeexception")).when(onMobileOBDGateway).sendMessages(anyString(), any(MainSubSlot.class));

        try {
            campaignMessageService.sendThirdMainSubSlotMessages(MainSubSlot.THREE);
        } finally {
            verify(allCampaignMessages, never()).update(any(CampaignMessage.class));
        }
    }

    @Test
    public void shouldNotSendThirdMainSubSlotMessagesIfNoneExist() {
        when(allCampaignMessages.getAllUnsentNewAndNAMessages()).thenReturn(new ArrayList<CampaignMessage>());

        campaignMessageService.sendThirdMainSubSlotMessages(MainSubSlot.THREE);

        verify(allCampaignMessages, never()).update(any(CampaignMessage.class));
        verifyZeroInteractions(onMobileOBDGateway);
    }

    @Test
    public void shouldFetchNewAndRetryStatusMessagesAndSendOnlyGivenPercentageInSecondMainSubSlot() {
        CampaignMessage campaignMessage1 = new CampaignMessage("subsriptionId1", "messageId1", DateTime.now(), "1234567890", "operator1", DateTime.now().plusDays(2));
        CampaignMessage campaignMessage2 = new CampaignMessage("subsriptionId2", "messageId2", DateTime.now(), "1234567891", "operator2", DateTime.now().plusDays(2));
        CampaignMessage campaignMessage3 = new CampaignMessage("subsriptionId3", "messageId3", DateTime.now(), "1234567892", "operator3", DateTime.now().plusDays(2));
        CampaignMessage campaignMessage4 = new CampaignMessage("subsriptionId4", "messageId4", DateTime.now(), "1234567893", "operator4", DateTime.now().plusDays(2));
        List<CampaignMessage> retryCampaignMessages = new ArrayList<>(Arrays.asList(campaignMessage1));
        List<CampaignMessage> newCampaignMessages = Arrays.asList(campaignMessage4, campaignMessage3, campaignMessage2, campaignMessage1);
        List<CampaignMessage> expectedCampaignMessagesToBeSent = Arrays.asList(campaignMessage1, campaignMessage4, campaignMessage3, campaignMessage2);
        when(allCampaignMessages.getAllUnsentNewMessages()).thenReturn(newCampaignMessages);
        when(allCampaignMessages.getAllUnsentRetryMessages()).thenReturn(retryCampaignMessages);
        String csvContent = "csvContent";
        when(campaignMessageCSVBuilder.getCSV(expectedCampaignMessagesToBeSent)).thenReturn(csvContent);
        when(obdProperties.getSlotMessagePercentageFor(MainSubSlot.TWO)).thenReturn(40);
        when(obdProperties.getSlotMessagePercentageFor(MainSubSlot.ONE)).thenReturn(30);

        campaignMessageService.sendSecondMainSubSlotMessages(MainSubSlot.TWO);

        verify(campaignMessageCSVBuilder).getCSV(expectedCampaignMessagesToBeSent);
        verify(allCampaignMessages).getAllUnsentRetryMessages();
        verify(onMobileOBDGateway).sendMessages(csvContent, MainSubSlot.TWO);
        verifyCampaignMessageUpdate(expectedCampaignMessagesToBeSent);
    }

    @Test
    public void shouldNotSaveAllCampaignMessagesAsSentIfSendingFailed() {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("myruntimeexception");

        CampaignMessage expectedCampaignMessage1 = new CampaignMessage("subsriptionId1", "messageId1", DateTime.now(), "1234567890", "operator1", DateTime.now().plusDays(2));
        CampaignMessage expectedCampaignMessage2 = new CampaignMessage("subsriptionId2", "messageId2", DateTime.now(), "1234567891", "operator2", DateTime.now().plusDays(2));
        List<CampaignMessage> campaignMessages = Arrays.asList(expectedCampaignMessage1, expectedCampaignMessage2);
        when(allCampaignMessages.getAllUnsentRetryMessages()).thenReturn(campaignMessages);
        when(allCampaignMessages.getAllUnsentNewMessages()).thenReturn(campaignMessages);

        doThrow(new RuntimeException("myruntimeexception")).when(onMobileOBDGateway).sendMessages(anyString(), any(MainSubSlot.class));

        try {
            campaignMessageService.sendSecondMainSubSlotMessages(MainSubSlot.TWO);
        } finally {
            verify(allCampaignMessages, never()).update(any(CampaignMessage.class));
        }
    }

    @Test
    public void shouldNotSendSecondSubSlotMessagesIfNoneExist() {
        when(allCampaignMessages.getAllUnsentRetryMessages()).thenReturn(new ArrayList<CampaignMessage>());
        when(allCampaignMessages.getAllUnsentNewMessages()).thenReturn(new ArrayList<CampaignMessage>());

        campaignMessageService.sendSecondMainSubSlotMessages(MainSubSlot.TWO);

        verify(allCampaignMessages, never()).update(any(CampaignMessage.class));
        verifyZeroInteractions(onMobileOBDGateway);
    }

    @Test
    public void shouldNotThrowTheExceptionAndRetryUpdatingWhenThrownDuringUpdateOfSentMessages() {
        CampaignMessage campaignMessage = new CampaignMessage("subsriptionId1", "messageId1", DateTime.now(), "1234567890", "operator1", DateTime.now().plusDays(2));
        List<CampaignMessage> campaignMessages = Arrays.asList(campaignMessage);
        when(allCampaignMessages.getAllUnsentNAMessages()).thenReturn(campaignMessages);
        String csvContent = "csvContent";
        when(campaignMessageCSVBuilder.getCSV(campaignMessages)).thenReturn(csvContent);
        when(obdProperties.getRetryIntervalForMessageUpdate()).thenReturn(5);
        when(obdProperties.getInitialWaitForMessageUpdate()).thenReturn(5);
        when(obdProperties.getRetryCountForMessageUpdate()).thenReturn(3);

        doThrow(new RuntimeException("myruntimeexception")).when(allCampaignMessages).update(any(CampaignMessage.class));
        campaignMessageService.sendRetrySlotMessages(RetrySubSlot.ONE);
        ArgumentCaptor<RetryTask> retryTaskArgumentCaptor = ArgumentCaptor.forClass(RetryTask.class);
        verify(retryTaskExecutor).run(eq(5), eq(5), eq(3), retryTaskArgumentCaptor.capture());
        RetryTask retryTask = retryTaskArgumentCaptor.getValue();
        try {
            retryTask.execute();
        } catch (Exception e) {

        }
        verify(allCampaignMessages, times(2)).update(campaignMessage);
    }

    @Test
    public void shouldCheckIfOBDMessageHasReachedMaximumRetryDays() {
        String subscriptionId = "subscriptionId";
        String campaignId = "WEEK13";
        DateTime now = DateTime.now();
        CampaignMessage campaignMessage = new CampaignMessage(subscriptionId, campaignId, now.minusDays(8), "1234567890", "airtel", now.plusDays(2));
        ValidFailedCallReport failedCallReport = new ValidFailedCallReport(subscriptionId, "msisdn", campaignId, CampaignMessageStatus.NA, DateTime.now());
        when(obdProperties.getMaximumOBDRetryDays()).thenReturn(MAX_OBD_RETRY_DAYS);
        when(allCampaignMessages.find(subscriptionId, campaignId)).thenReturn(campaignMessage);

        campaignMessageService.processValidCallDeliveryFailureRecords(failedCallReport);

        verify(allCampaignMessages).find(subscriptionId, campaignId);
        verify(reportingService).reportCampaignMessageDeliveryStatus(any(CallDetailsReportRequest.class));
        verify(allCampaignMessages).delete(campaignMessage);
        verify(allCampaignMessages, never()).update(campaignMessage);
    }

    @Test
    public void shouldCheckIfOBDMessageHasReachedExpiryDate() {
        String subscriptionId = "subscriptionId";
        String campaignId = "WEEK13";
        DateTime now = DateTime.now();
        CampaignMessage campaignMessage = new CampaignMessage(subscriptionId, campaignId, now.minusDays(4), "1234567890", "airtel", now.minusDays(1));
        ValidFailedCallReport failedCallReport = new ValidFailedCallReport(subscriptionId, "msisdn", campaignId, CampaignMessageStatus.NA, DateTime.now());
        when(obdProperties.getMaximumOBDRetryDays()).thenReturn(MAX_OBD_RETRY_DAYS);
        when(allCampaignMessages.find(subscriptionId, campaignId)).thenReturn(campaignMessage);

        campaignMessageService.processValidCallDeliveryFailureRecords(failedCallReport);

        verify(allCampaignMessages).find(subscriptionId, campaignId);
        verify(reportingService).reportCampaignMessageDeliveryStatus(any(CallDetailsReportRequest.class));
        verify(allCampaignMessages).delete(campaignMessage);
        verify(allCampaignMessages, never()).update(campaignMessage);
    }

    private void verifyCampaignMessageUpdate(List<CampaignMessage> expectedCampaignMessages) {
        ArgumentCaptor<CampaignMessage> captor = ArgumentCaptor.forClass(CampaignMessage.class);
        verify(allCampaignMessages, times(expectedCampaignMessages.size())).update(captor.capture());
        List<CampaignMessage> actualCampaignMessages = captor.getAllValues();
        assertEquals(expectedCampaignMessages, actualCampaignMessages);
    }
}
