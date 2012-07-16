package org.motechproject.ananya.kilkari.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.motechproject.ananya.kilkari.domain.CampaignMessageAlert;
import org.motechproject.ananya.kilkari.messagecampaign.service.KilkariMessageCampaignService;
import org.motechproject.ananya.kilkari.obd.contract.InvalidCallRecordRequestObject;
import org.motechproject.ananya.kilkari.obd.contract.InvalidCallRecordsRequest;
import org.motechproject.ananya.kilkari.obd.contract.OBDRequest;
import org.motechproject.ananya.kilkari.obd.contract.OBDRequestWrapper;
import org.motechproject.ananya.kilkari.obd.domain.InvalidCallRecord;
import org.motechproject.ananya.kilkari.obd.domain.*;
import org.motechproject.ananya.kilkari.obd.service.CampaignMessageService;
import org.motechproject.ananya.kilkari.reporting.domain.CampaignMessageDeliveryReportRequest;
import org.motechproject.ananya.kilkari.reporting.service.ReportingService;
import org.motechproject.ananya.kilkari.repository.AllCampaignMessageAlerts;
import org.motechproject.ananya.kilkari.subscription.domain.Operator;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.utils.CampaignMessageIdStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class KilkariCampaignServiceTest {

    private KilkariCampaignService kilkariCampaignService;

    @Mock
    private KilkariMessageCampaignService kilkariMessageCampaignService;
    @Mock
    private KilkariSubscriptionService kilkariSubscriptionService;
    @Mock
    private AllCampaignMessageAlerts allCampaignMessageAlerts;
    @Mock
    private CampaignMessageIdStrategy campaignMessageIdStrategy;
    @Mock
    private CampaignMessageService campaignMessageService;
    @Mock
    private ReportingService reportingService;
    @Mock
    private OBDRequestCallbackPublisher obdRequestCallbackPublisher;

    @Before
    public void setUp() {
        initMocks(this);
        kilkariCampaignService = new KilkariCampaignService(kilkariMessageCampaignService, kilkariSubscriptionService, campaignMessageIdStrategy, allCampaignMessageAlerts, campaignMessageService, reportingService, obdRequestCallbackPublisher);
    }

    @Test
    public void shouldGetMessageTimings() {
        String msisdn = "1234567890";
        List<Subscription> subscriptions = new ArrayList<>();
        Subscription subscription1 = new Subscription(msisdn, SubscriptionPack.FIFTEEN_MONTHS, DateTime.now());
        Subscription subscription2 = new Subscription(msisdn, SubscriptionPack.SEVEN_MONTHS, DateTime.now());
        subscriptions.add(subscription1);
        subscriptions.add(subscription2);

        List<DateTime> dateTimes = new ArrayList<>();
        dateTimes.add(DateTime.now());

        when(kilkariSubscriptionService.findByMsisdn(msisdn)).thenReturn(subscriptions);

        when(kilkariMessageCampaignService.getMessageTimings(
                subscription1.getSubscriptionId(),
                subscription1.getPack().name(),
                subscription1.getCreationDate(), subscription1.endDate())).thenReturn(dateTimes);
        when(kilkariMessageCampaignService.getMessageTimings(
                subscription2.getSubscriptionId(),
                subscription2.getPack().name(),
                subscription2.getCreationDate(), subscription2.endDate())).thenReturn(dateTimes);


        Map<String, List<DateTime>> messageTimings = kilkariCampaignService.getMessageTimings(msisdn);

        verify(kilkariMessageCampaignService).getMessageTimings(
                eq(subscription1.getSubscriptionId()),
                eq(subscription1.getPack().name()),
                eq(subscription1.getCreationDate()),
                eq(subscription1.endDate()));

        verify(kilkariMessageCampaignService).getMessageTimings(
                eq(subscription2.getSubscriptionId()),
                eq(subscription2.getPack().name()),
                eq(subscription2.getCreationDate()),
                eq(subscription2.endDate()));

        assertThat(messageTimings.size(), is(2));
        assertThat(messageTimings, hasEntry(subscription1.getSubscriptionId(), dateTimes));
        assertThat(messageTimings, hasEntry(subscription2.getSubscriptionId(), dateTimes));
    }

    @Test
    public void shouldSaveCampaignMessageAlertIfDoesNotExist() {

        String subscriptionId = "mysubscriptionid";
        String messageId = "mymessageid";
        Subscription subscription = new Subscription();

        when(kilkariSubscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(subscription);
        when(allCampaignMessageAlerts.findBySubscriptionId(subscriptionId)).thenReturn(null);
        when(campaignMessageIdStrategy.createMessageId(subscription)).thenReturn(messageId);

        kilkariCampaignService.scheduleWeeklyMessage(subscriptionId);


        verify(allCampaignMessageAlerts).findBySubscriptionId(subscriptionId);
        ArgumentCaptor<CampaignMessageAlert> campaignMessageAlertArgumentCaptor = ArgumentCaptor.forClass(CampaignMessageAlert.class);
        verify(allCampaignMessageAlerts).add(campaignMessageAlertArgumentCaptor.capture());
        CampaignMessageAlert campaignMessageAlert = campaignMessageAlertArgumentCaptor.getValue();
        assertEquals(subscriptionId, campaignMessageAlert.getSubscriptionId());
        assertEquals(messageId, campaignMessageAlert.getMessageId());
        assertFalse(campaignMessageAlert.isRenewed());

        verifyZeroInteractions(campaignMessageService);
        verify(allCampaignMessageAlerts, never()).remove(any(CampaignMessageAlert.class));
    }

    @Test
    public void shouldAddCampaignMessageAlertOnRenewIfItDoesNotExist() {
        String subscriptionId = "mysubscriptionid";
        when(allCampaignMessageAlerts.findBySubscriptionId(subscriptionId)).thenReturn(null);

        kilkariCampaignService.renewSchedule(subscriptionId);

        ArgumentCaptor<CampaignMessageAlert> campaignMessageAlertArgumentCaptor = ArgumentCaptor.forClass(CampaignMessageAlert.class);
        verify(allCampaignMessageAlerts).add(campaignMessageAlertArgumentCaptor.capture());
        CampaignMessageAlert value = campaignMessageAlertArgumentCaptor.getValue();

        assertEquals(subscriptionId, value.getSubscriptionId());
        assertNull(value.getMessageId());
        assertTrue(value.isRenewed());
    }

    @Test
    public void shouldRenewCampaignMessageAlertAndScheduleCampaignMessageIfMessageIdExists() {
        String messageId = "mymessageid";
        String msisdn = "1234567890";
        Operator operator = Operator.AIRTEL;
        Subscription subscription = new Subscription(msisdn, SubscriptionPack.FIFTEEN_MONTHS, DateTime.now());
        subscription.setOperator(operator);
        String subscriptionId = subscription.getSubscriptionId();

        CampaignMessageAlert campaignMessageAlert = new CampaignMessageAlert(subscriptionId, messageId, true);
        when(allCampaignMessageAlerts.findBySubscriptionId(subscriptionId)).thenReturn(campaignMessageAlert);

        when(kilkariSubscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(subscription);

        kilkariCampaignService.renewSchedule(subscriptionId);

        verify(allCampaignMessageAlerts).findBySubscriptionId(subscriptionId);
        verify(campaignMessageService).scheduleCampaignMessage(subscriptionId, messageId, msisdn, operator.name());
        ArgumentCaptor<CampaignMessageAlert> captor = ArgumentCaptor.forClass(CampaignMessageAlert.class);
        verify(allCampaignMessageAlerts).update(captor.capture());
        CampaignMessageAlert actualCampaignMessageAlert = captor.getValue();
        assertNull(actualCampaignMessageAlert.getMessageId());
        assertFalse(actualCampaignMessageAlert.isRenewed());
    }

    @Test
    public void shouldUpdateCampaignMessageAlertIfAlreadyExistsAndScheduleCampaignMessageIfRenewed() {
        String messageId = "mymessageid";
        String msisdn = "1234567890";
        Operator operator = Operator.AIRTEL;
        Subscription subscription = new Subscription(msisdn, SubscriptionPack.FIFTEEN_MONTHS, DateTime.now());
        subscription.setOperator(operator);
        String subscriptionId = subscription.getSubscriptionId();

        CampaignMessageAlert campaignMessageAlert = new CampaignMessageAlert(subscriptionId, "previousMessageId", true);

        when(kilkariSubscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(subscription);
        when(allCampaignMessageAlerts.findBySubscriptionId(subscriptionId)).thenReturn(campaignMessageAlert);
        when(campaignMessageIdStrategy.createMessageId(subscription)).thenReturn(messageId);

        kilkariCampaignService.scheduleWeeklyMessage(subscriptionId);

        verify(allCampaignMessageAlerts).findBySubscriptionId(subscriptionId);
        verify(campaignMessageService).scheduleCampaignMessage(subscriptionId, messageId, msisdn, operator.name());
        ArgumentCaptor<CampaignMessageAlert> captor = ArgumentCaptor.forClass(CampaignMessageAlert.class);
        verify(allCampaignMessageAlerts).update(captor.capture());
        CampaignMessageAlert actualCampaignMessageAlert = captor.getValue();
        assertNull(actualCampaignMessageAlert.getMessageId());
        assertFalse(actualCampaignMessageAlert.isRenewed());
    }

    @Test
    public void shouldUpdateCampaignMessageAlertIfAlreadyExistsButShouldNotScheduleCampaignMessageIfNotRenewed() {

        String subscriptionId = "mysubscriptionid";
        String messageId = "mymessageid";
        Subscription subscription = new Subscription();
        CampaignMessageAlert campaignMessageAlert = new CampaignMessageAlert(subscriptionId, "previousMessageId");

        when(kilkariSubscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(subscription);
        when(allCampaignMessageAlerts.findBySubscriptionId(subscriptionId)).thenReturn(campaignMessageAlert);
        when(campaignMessageIdStrategy.createMessageId(subscription)).thenReturn(messageId);

        kilkariCampaignService.scheduleWeeklyMessage(subscriptionId);

        verify(allCampaignMessageAlerts).findBySubscriptionId(subscriptionId);

        ArgumentCaptor<CampaignMessageAlert> campaignMessageAlertArgumentCaptor = ArgumentCaptor.forClass(CampaignMessageAlert.class);
        verify(allCampaignMessageAlerts).update(campaignMessageAlertArgumentCaptor.capture());
        CampaignMessageAlert actualCampaignMessageAlert = campaignMessageAlertArgumentCaptor.getValue();
        assertEquals(subscriptionId, actualCampaignMessageAlert.getSubscriptionId());
        assertEquals(messageId, actualCampaignMessageAlert.getMessageId());
        assertFalse(actualCampaignMessageAlert.isRenewed());

        verifyZeroInteractions(campaignMessageService);
        verify(allCampaignMessageAlerts, never()).remove(any(CampaignMessageAlert.class));
    }

    @Test
    public void shouldPublishObdCallbackRequest() {
        OBDRequestWrapper obdRequestWrapper = new OBDRequestWrapper(new OBDRequest(), "subscriptionId", DateTime.now());

        kilkariCampaignService.processOBDCallbackRequest(obdRequestWrapper);

        verify(obdRequestCallbackPublisher).publishObdCallbackRequest(obdRequestWrapper);
    }

    @Test
    public void shouldProcessSuccessfulCampaignMessageDelivery() {
        OBDRequest obdRequest = new OBDRequest();
        String subscriptionId = "subscriptionId";
        String campaignId = "WEEK1";
        String msisdn = "1234567890";
        Integer retryCount = 3;
        String serviceOption = ServiceOption.HELP.name();
        String startTime = "25-12-2012";
        String endTime = "27-12-2012";
        CallDetailRecord callDetailRecord = new CallDetailRecord();
        callDetailRecord.setStartTime(startTime);
        callDetailRecord.setEndTime(endTime);

        obdRequest.setMsisdn(msisdn);
        obdRequest.setCampaignId(campaignId);
        obdRequest.setServiceOption(serviceOption);
        obdRequest.setCallDetailRecord(callDetailRecord);
        OBDRequestWrapper obdRequestWrapper = new OBDRequestWrapper(obdRequest, subscriptionId, DateTime.now());
        CampaignMessage campaignMessage = mock(CampaignMessage.class);
        when(campaignMessage.getRetryCount()).thenReturn(retryCount);
        when(campaignMessageService.find(subscriptionId, campaignId)).thenReturn(campaignMessage);

        kilkariCampaignService.processSuccessfulMessageDelivery(obdRequestWrapper);

        InOrder inOrder = Mockito.inOrder(campaignMessageService, reportingService);
        inOrder.verify(campaignMessageService).find(subscriptionId, campaignId);
        inOrder.verify(campaignMessageService).deleteCampaignMessage(campaignMessage);

        ArgumentCaptor<CampaignMessageDeliveryReportRequest> campaignMessageDeliveryReportRequestArgumentCaptor = ArgumentCaptor.forClass(CampaignMessageDeliveryReportRequest.class);
        inOrder.verify(reportingService).reportCampaignMessageDelivered(campaignMessageDeliveryReportRequestArgumentCaptor.capture());
        CampaignMessageDeliveryReportRequest campaignMessageDeliveryReportRequest = campaignMessageDeliveryReportRequestArgumentCaptor.getValue();

        assertEquals(subscriptionId, campaignMessageDeliveryReportRequest.getSubscriptionId());
        assertEquals(msisdn, campaignMessageDeliveryReportRequest.getMsisdn());
        assertEquals(campaignId, campaignMessageDeliveryReportRequest.getCampaignId());
        assertEquals(retryCount.toString(), campaignMessageDeliveryReportRequest.getRetryCount());
        assertEquals(serviceOption, campaignMessageDeliveryReportRequest.getServiceOption());
        assertEquals(startTime, campaignMessageDeliveryReportRequest.getCallDetailRecord().getStartTime());
        assertEquals(endTime, campaignMessageDeliveryReportRequest.getCallDetailRecord().getEndTime());
    }

    @Test
    public void shouldNotProcessSuccessfulCampaignMessageDeliveryIfThereIsNoSubscriptionAvailable() {
        OBDRequest obdRequest = new OBDRequest();
        String subscriptionId = "subscriptionId";
        String campaignId = "WEEK1";
        String msisdn = "1234567890";
        String serviceOption = ServiceOption.HELP.name();
        String startTime = "25-12-2012";
        String endTime = "27-12-2012";
        CallDetailRecord callDetailRecord = new CallDetailRecord();
        callDetailRecord.setStartTime(startTime);
        callDetailRecord.setEndTime(endTime);

        obdRequest.setMsisdn(msisdn);
        obdRequest.setCampaignId(campaignId);
        obdRequest.setServiceOption(serviceOption);
        obdRequest.setCallDetailRecord(callDetailRecord);
        OBDRequestWrapper obdRequestWrapper = new OBDRequestWrapper(obdRequest, subscriptionId, DateTime.now());
        when(campaignMessageService.find(subscriptionId, campaignId)).thenReturn(null);

        kilkariCampaignService.processSuccessfulMessageDelivery(obdRequestWrapper);

        verify(campaignMessageService).find(subscriptionId, campaignId);
        verify(campaignMessageService, never()).deleteCampaignMessage(any(CampaignMessage.class));
        verifyZeroInteractions(reportingService);
    }

    @Test
    public void shouldProcessInvalidCallRecords() {
        String msisdn1 = "msisdn1";
        String campaign1 = "campaign1";
        String desc1 = "desc1";
        String operator1 = "operator1";
        String sub1 = "sub1";

        InvalidCallRecordsRequest invalidCallRecordsRequest = Mockito.mock(InvalidCallRecordsRequest.class);
        ArrayList<InvalidCallRecordRequestObject> invalidCallRecordRequestObjects = new ArrayList<InvalidCallRecordRequestObject>();
        InvalidCallRecordRequestObject invalidCallRecordRequestObject1 = Mockito.mock(InvalidCallRecordRequestObject.class);
        when(invalidCallRecordRequestObject1.getMsisdn()).thenReturn(msisdn1);
        when(invalidCallRecordRequestObject1.getCampaignId()).thenReturn(campaign1);
        when(invalidCallRecordRequestObject1.getDescription()).thenReturn(desc1);
        when(invalidCallRecordRequestObject1.getOperator()).thenReturn(operator1);
        when(invalidCallRecordRequestObject1.getSubscriptionId()).thenReturn(sub1);

        InvalidCallRecordRequestObject invalidCallRecordRequestObject2 = Mockito.mock(InvalidCallRecordRequestObject.class);
        invalidCallRecordRequestObjects.add(invalidCallRecordRequestObject1);
        invalidCallRecordRequestObjects.add(invalidCallRecordRequestObject2);


        when(invalidCallRecordsRequest.getCallrecords()).thenReturn(invalidCallRecordRequestObjects);

        kilkariCampaignService.processInvalidCallRecords(invalidCallRecordsRequest);

        ArgumentCaptor<ArrayList> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(campaignMessageService).processInvalidCallRecords(captor.capture());

        ArrayList actualInvalidCallRecords = captor.getValue();
        assertEquals(2, actualInvalidCallRecords.size());
        InvalidCallRecord actualInvalidCallRecord1 = (InvalidCallRecord) actualInvalidCallRecords.get(0);
        assertEquals(campaign1,actualInvalidCallRecord1.getCampaignId());
        assertEquals(sub1,actualInvalidCallRecord1.getSubscriptionId());
        assertEquals(msisdn1,actualInvalidCallRecord1.getMsisdn());
        assertEquals(operator1,actualInvalidCallRecord1.getOperator());
        assertEquals(desc1,actualInvalidCallRecord1.getDescription());
    }
}