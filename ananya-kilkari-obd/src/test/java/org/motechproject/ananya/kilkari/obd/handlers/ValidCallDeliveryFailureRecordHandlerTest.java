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
import org.motechproject.scheduler.domain.MotechEvent;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ValidCallDeliveryFailureRecordHandlerTest {
    public static final int MAX_RETRY_COUNT = 3;
    @Mock
    private CampaignMessageService campaignMessageService;
    @Mock
    private OBDEndPoints obdEndPoints;
    private ValidCallDeliveryFailureRecordHandler validCallDeliveryFailureRecordHandler;

    @Before
    public void setUp() {
        initMocks(this);
        validCallDeliveryFailureRecordHandler = new ValidCallDeliveryFailureRecordHandler(campaignMessageService, obdEndPoints);
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

        verify(campaignMessageService, never()).update(any(CampaignMessage.class));
    }

}
