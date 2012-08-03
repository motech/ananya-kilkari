package org.motechproject.ananya.kilkari.mapper;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessageStatus;
import org.motechproject.ananya.kilkari.obd.domain.ServiceOption;
import org.motechproject.ananya.kilkari.reporting.domain.CampaignMessageDeliveryReportRequest;
import org.motechproject.ananya.kilkari.request.CallDurationRequest;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallDetailsRequest;

import static junit.framework.Assert.assertEquals;

public class CampaignMessageDeliveryReportRequestMapperTest {

    @Test
    public void shouldMapCampaignMessageDeliveryReportRequest() {
        DateTime startTime = DateTime.now();
        DateTime endTime = DateTime.now().plusMinutes(2);
        String subscriptionId = "subscriptionId";
        CallDurationRequest callDurationRequest = new CallDurationRequest(startTime, endTime);
        DateTime createdAt = DateTime.now().minusSeconds(56);

        OBDSuccessfulCallDetailsRequest obdSuccessfulCallDetailsRequest = new OBDSuccessfulCallDetailsRequest(subscriptionId, ServiceOption.HELP, "1234567890", "CampaignId", callDurationRequest, createdAt);

        Integer retryCount = 3;
        CampaignMessageDeliveryReportRequestMapper campaignMessageDeliveryReportRequestMapper = new CampaignMessageDeliveryReportRequestMapper();

        CampaignMessageDeliveryReportRequest actualDeliveryReportRequest = campaignMessageDeliveryReportRequestMapper.mapFrom(obdSuccessfulCallDetailsRequest, retryCount);

        assertEquals(subscriptionId, actualDeliveryReportRequest.getSubscriptionId());
        assertEquals(obdSuccessfulCallDetailsRequest.getMsisdn(), actualDeliveryReportRequest.getMsisdn());
        assertEquals(obdSuccessfulCallDetailsRequest.getCampaignId(), actualDeliveryReportRequest.getCampaignId());
        assertEquals(obdSuccessfulCallDetailsRequest.getServiceOption().name(), actualDeliveryReportRequest.getServiceOption());
        assertEquals(CampaignMessageStatus.SUCCESS.name(), actualDeliveryReportRequest.getStatus());
        assertEquals(retryCount.toString(), actualDeliveryReportRequest.getRetryCount());
        assertEquals(startTime, obdSuccessfulCallDetailsRequest.getCallDurationRequest().getStartTime());
        assertEquals(endTime, obdSuccessfulCallDetailsRequest.getCallDurationRequest().getEndTime());
        assertEquals("OBD", actualDeliveryReportRequest.getCallSource());
    }
}