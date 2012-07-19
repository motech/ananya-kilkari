package org.motechproject.ananya.kilkari.obd.handlers;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.obd.contract.ValidCallDeliveryFailureRecordObject;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessage;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessageStatus;
import org.motechproject.ananya.kilkari.obd.domain.OBDEventKeys;
import org.motechproject.ananya.kilkari.obd.gateway.OBDEndPoints;
import org.motechproject.ananya.kilkari.obd.service.CampaignMessageService;
import org.motechproject.ananya.kilkari.reporting.domain.CampaignMessageDeliveryReportRequest;
import org.motechproject.ananya.kilkari.reporting.service.ReportingService;
import org.motechproject.scheduler.domain.MotechEvent;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ValidCallDeliveryFailureRecordHandlerTest {
    public static final int MAX_RETRY_COUNT = 3;
    @Mock
    private CampaignMessageService campaignMessageService;
    @Mock
    private OBDEndPoints obdEndPoints;
    @Mock
    private ReportingService reportingService;
    private ValidCallDeliveryFailureRecordHandler validCallDeliveryFailureRecordHandler;

    @Before
    public void setUp() {
        initMocks(this);
        validCallDeliveryFailureRecordHandler = new ValidCallDeliveryFailureRecordHandler(campaignMessageService, reportingService, obdEndPoints);
    }

    @Test
    public void shouldUpdateCampaignMessageStatus() {
        HashMap<String, Object> parameters = new HashMap<>();
        String subscriptionId = "subscriptionId";
        String campaignId = "WEEK13";
        ValidCallDeliveryFailureRecordObject recordObject = new ValidCallDeliveryFailureRecordObject(subscriptionId, "msisdn", campaignId, CampaignMessageStatus.DNP, DateTime.now());
        parameters.put("0", recordObject);

        CampaignMessage campaignMessage = new CampaignMessage();
        when(obdEndPoints.getMaximumRetryCount()).thenReturn(MAX_RETRY_COUNT);
        when(campaignMessageService.find(subscriptionId, campaignId)).thenReturn(campaignMessage);

        validCallDeliveryFailureRecordHandler.handleValidCallDeliveryFailureRecord(new MotechEvent(OBDEventKeys.PROCESS_VALID_CALL_DELIVERY_FAILURE_RECORD, parameters));

        verify(campaignMessageService).find(subscriptionId, campaignId);
        verify(reportingService).reportCampaignMessageDeliveryStatus(any(CampaignMessageDeliveryReportRequest.class));

        ArgumentCaptor<CampaignMessage> campaignMessageArgumentCaptor = ArgumentCaptor.forClass(CampaignMessage.class);
        verify(campaignMessageService).update(campaignMessageArgumentCaptor.capture());
        CampaignMessage actualCampaignMessage = campaignMessageArgumentCaptor.getValue();

        assertEquals(CampaignMessageStatus.DNP, actualCampaignMessage.getStatus());
    }

    @Test
    public void shouldDeleteCampaignMessageIfRetryCountHAsReachedItsMaximumValue() {
        HashMap<String, Object> parameters = new HashMap<>();
        String subscriptionId = "subscriptionId";
        String campaignId = "WEEK13";
        ValidCallDeliveryFailureRecordObject recordObject = new ValidCallDeliveryFailureRecordObject(subscriptionId, "msisdn", campaignId, CampaignMessageStatus.DNP, DateTime.now());
        parameters.put("0", recordObject);

        CampaignMessage campaignMessage = mock(CampaignMessage.class);
        when(obdEndPoints.getMaximumRetryCount()).thenReturn(MAX_RETRY_COUNT);
        when(campaignMessage.getRetryCount()).thenReturn(MAX_RETRY_COUNT);
        when(campaignMessageService.find(subscriptionId, campaignId)).thenReturn(campaignMessage);

        validCallDeliveryFailureRecordHandler.handleValidCallDeliveryFailureRecord(new MotechEvent(OBDEventKeys.PROCESS_VALID_CALL_DELIVERY_FAILURE_RECORD, parameters));

        verify(campaignMessageService).find(subscriptionId, campaignId);
        verify(reportingService).reportCampaignMessageDeliveryStatus(any(CampaignMessageDeliveryReportRequest.class));
        verify(campaignMessageService).deleteCampaignMessage(campaignMessage);
    }

    @Test
    public void shouldNotUpdateCampaignMessageStatusIfCampaignMessageIsNull() {
        HashMap<String, Object> parameters = new HashMap<>();
        String subscriptionId = "subscriptionId";
        String campaignId = "WEEK13";
        ValidCallDeliveryFailureRecordObject recordObject = new ValidCallDeliveryFailureRecordObject(subscriptionId, "msisdn", campaignId, CampaignMessageStatus.DNP, DateTime.now());
        parameters.put("0", recordObject);

        when(campaignMessageService.find(subscriptionId, campaignId)).thenReturn(null);

        validCallDeliveryFailureRecordHandler.handleValidCallDeliveryFailureRecord(new MotechEvent(OBDEventKeys.PROCESS_VALID_CALL_DELIVERY_FAILURE_RECORD, parameters));

        verify(campaignMessageService).find(subscriptionId, campaignId);
        verify(reportingService, never()).reportCampaignMessageDeliveryStatus(any(CampaignMessageDeliveryReportRequest.class));
        verify(campaignMessageService, never()).update(any(CampaignMessage.class));
    }

    @Test
    public void shouldReportCampaignMessageStatus() {
        HashMap<String, Object> parameters = new HashMap<>();
        String subscriptionId = "subscriptionId";
        String campaignId = "WEEK13";
        String msisdn = "msisdn";
        DateTime createdAt = new DateTime(2012, 12, 25, 23, 23, 23);
        ValidCallDeliveryFailureRecordObject recordObject = new ValidCallDeliveryFailureRecordObject(subscriptionId, msisdn, campaignId, CampaignMessageStatus.DNP, createdAt);
        parameters.put("0", recordObject);

        CampaignMessage campaignMessage = new CampaignMessage();
        when(obdEndPoints.getMaximumRetryCount()).thenReturn(MAX_RETRY_COUNT);
        when(campaignMessageService.find(subscriptionId, campaignId)).thenReturn(campaignMessage);

        validCallDeliveryFailureRecordHandler.handleValidCallDeliveryFailureRecord(new MotechEvent(OBDEventKeys.PROCESS_VALID_CALL_DELIVERY_FAILURE_RECORD, parameters));

        verify(campaignMessageService).find(subscriptionId, campaignId);

        ArgumentCaptor<CampaignMessageDeliveryReportRequest> campaignMessageDeliveryReportRequestArgumentCaptor = ArgumentCaptor.forClass(CampaignMessageDeliveryReportRequest.class);
        verify(reportingService).reportCampaignMessageDeliveryStatus(campaignMessageDeliveryReportRequestArgumentCaptor.capture());
        CampaignMessageDeliveryReportRequest reportRequest = campaignMessageDeliveryReportRequestArgumentCaptor.getValue();

        ArgumentCaptor<CampaignMessage> campaignMessageArgumentCaptor = ArgumentCaptor.forClass(CampaignMessage.class);
        verify(campaignMessageService).update(campaignMessageArgumentCaptor.capture());
        CampaignMessage actualCampaignMessage = campaignMessageArgumentCaptor.getValue();

        assertEquals(CampaignMessageStatus.DNP, actualCampaignMessage.getStatus());
        assertEquals(msisdn, reportRequest.getMsisdn());
        assertEquals(subscriptionId, reportRequest.getSubscriptionId());
        assertEquals(campaignId, reportRequest.getCampaignId());
        assertEquals("0", reportRequest.getRetryCount());
        assertEquals("25-12-2012 23:23:23", reportRequest.getCallDetailRecord().getStartTime());
        assertEquals("25-12-2012 23:23:23", reportRequest.getCallDetailRecord().getEndTime());
        assertNull(reportRequest.getServiceOption());
    }

}
