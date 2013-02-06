package org.motechproject.ananya.kilkari.obd.service;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessageStatus;
import org.motechproject.ananya.kilkari.obd.domain.ServiceOption;
import org.motechproject.ananya.kilkari.obd.service.request.CallDurationRequest;
import org.motechproject.ananya.kilkari.obd.service.request.OBDSuccessfulCallDetailsRequest;
import org.motechproject.ananya.reports.kilkari.contract.request.CallDetailsReportRequest;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class CallDetailsReportRequestMapperTest {

    @Test
    public void shouldMapCampaignMessageDeliveryReportRequest() {
        DateTime startTime = DateTime.now();
        DateTime endTime = DateTime.now().plusMinutes(2);
        String subscriptionId = "subscriptionId";
        CallDurationRequest callDurationRequest = new CallDurationRequest(startTime, endTime);
        DateTime createdAt = DateTime.now().minusSeconds(56);

        OBDSuccessfulCallDetailsRequest obdSuccessfulCallDetailsRequest = new OBDSuccessfulCallDetailsRequest(subscriptionId, ServiceOption.HELP, "1234567890", "CampaignId", callDurationRequest, createdAt);

        Integer retryCount = 3;

        CallDetailsReportRequest actualDeliveryReportRequest = CallDetailsReportRequestMapper.mapFrom(obdSuccessfulCallDetailsRequest, retryCount);

        assertEquals(subscriptionId, actualDeliveryReportRequest.getSubscriptionId());
        assertEquals(obdSuccessfulCallDetailsRequest.getMsisdn(), actualDeliveryReportRequest.getMsisdn());
        assertEquals(obdSuccessfulCallDetailsRequest.getCampaignId(), actualDeliveryReportRequest.getCampaignId());
        assertEquals(obdSuccessfulCallDetailsRequest.getServiceOption().name(), actualDeliveryReportRequest.getServiceOption());
        assertEquals(CampaignMessageStatus.SUCCESS.name(), actualDeliveryReportRequest.getStatus());
        assertEquals(startTime, obdSuccessfulCallDetailsRequest.getCallDurationRequest().getStartTime());
        assertEquals(endTime, obdSuccessfulCallDetailsRequest.getCallDurationRequest().getEndTime());
        assertEquals("OBD", actualDeliveryReportRequest.getCallSource());
    }

    @Test
    public void serviceOptionIsOptional() {
        DateTime startTime = DateTime.now();
        DateTime endTime = DateTime.now().plusMinutes(2);
        String subscriptionId = "subscriptionId";
        CallDurationRequest callDurationRequest = new CallDurationRequest(startTime, endTime);
        DateTime createdAt = DateTime.now().minusSeconds(56);
        OBDSuccessfulCallDetailsRequest obdSuccessfulCallDetailsRequest = new OBDSuccessfulCallDetailsRequest(subscriptionId, null, "1234567890", "CampaignId", callDurationRequest, createdAt);

        Integer retryCount = 3;

        CallDetailsReportRequest actualDeliveryReportRequest = CallDetailsReportRequestMapper.mapFrom(obdSuccessfulCallDetailsRequest, retryCount);

        assertEquals(subscriptionId, actualDeliveryReportRequest.getSubscriptionId());
        assertEquals(obdSuccessfulCallDetailsRequest.getMsisdn(), actualDeliveryReportRequest.getMsisdn());
        assertEquals(obdSuccessfulCallDetailsRequest.getCampaignId(), actualDeliveryReportRequest.getCampaignId());
        assertNull(actualDeliveryReportRequest.getServiceOption());
        assertEquals(CampaignMessageStatus.SUCCESS.name(), actualDeliveryReportRequest.getStatus());
        assertEquals(startTime, obdSuccessfulCallDetailsRequest.getCallDurationRequest().getStartTime());
        assertEquals(endTime, obdSuccessfulCallDetailsRequest.getCallDurationRequest().getEndTime());
        assertEquals("OBD", actualDeliveryReportRequest.getCallSource());
    }
}
