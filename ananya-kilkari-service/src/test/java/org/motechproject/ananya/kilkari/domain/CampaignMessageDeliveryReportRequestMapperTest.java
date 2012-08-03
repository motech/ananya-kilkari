package org.motechproject.ananya.kilkari.domain;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ananya.kilkari.obd.domain.CallDetailRecord;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessageStatus;
import org.motechproject.ananya.kilkari.reporting.domain.CampaignMessageDeliveryReportRequest;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallDetailsRequest;

import static junit.framework.Assert.assertEquals;

public class CampaignMessageDeliveryReportRequestMapperTest {

    @Test
    public void shouldMapCampaignMessageDeliveryReportRequest() {
        DateTime startTime = DateTime.now();
        DateTime endTime = DateTime.now().plusMinutes(2);
        String subscriptionId = "subscriptionId";
        CallDetailRecord callDetailRecord = new CallDetailRecord();
        callDetailRecord.setStartTime(startTime.toString());
        callDetailRecord.setEndTime(endTime.toString());

        OBDSuccessfulCallDetailsRequest obdSuccessfulCallDetailsRequest = new OBDSuccessfulCallDetailsRequest();
        obdSuccessfulCallDetailsRequest.setMsisdn("1234567890");
        obdSuccessfulCallDetailsRequest.setServiceOption("HELP");
        obdSuccessfulCallDetailsRequest.setCampaignId("CampaignId");
        obdSuccessfulCallDetailsRequest.setCallDetailRecord(callDetailRecord);
        obdSuccessfulCallDetailsRequest.setSubscriptionId(subscriptionId);

        Integer retryCount = 3;
        CampaignMessageDeliveryReportRequestMapper campaignMessageDeliveryReportRequestMapper = new CampaignMessageDeliveryReportRequestMapper();

        CampaignMessageDeliveryReportRequest actualDeliveryReportRequest = campaignMessageDeliveryReportRequestMapper.mapFrom(obdSuccessfulCallDetailsRequest, retryCount);

        assertEquals(subscriptionId, actualDeliveryReportRequest.getSubscriptionId());
        assertEquals(obdSuccessfulCallDetailsRequest.getMsisdn(), actualDeliveryReportRequest.getMsisdn());
        assertEquals(obdSuccessfulCallDetailsRequest.getCampaignId(), actualDeliveryReportRequest.getCampaignId());
        assertEquals(obdSuccessfulCallDetailsRequest.getServiceOption(), actualDeliveryReportRequest.getServiceOption());
        assertEquals(CampaignMessageStatus.SUCCESS.name(), actualDeliveryReportRequest.getStatus());
        assertEquals(retryCount.toString(), actualDeliveryReportRequest.getRetryCount());
        assertEquals(startTime.toString(), obdSuccessfulCallDetailsRequest.getCallDetailRecord().getStartTime());
        assertEquals(endTime.toString(), obdSuccessfulCallDetailsRequest.getCallDetailRecord().getEndTime());
        assertEquals("OBD", actualDeliveryReportRequest.getCallSource());
    }
}