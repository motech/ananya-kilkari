package org.motechproject.ananya.kilkari.domain;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ananya.kilkari.obd.contract.OBDRequest;
import org.motechproject.ananya.kilkari.obd.contract.OBDRequestWrapper;
import org.motechproject.ananya.kilkari.obd.domain.CallDetailRecord;
import org.motechproject.ananya.kilkari.reporting.domain.CampaignMessageDeliveryReportRequest;

import static junit.framework.Assert.assertEquals;

public class CampaignMessageDeliveryReportRequestMapperTest {

    @Test
    public void shouldMapCampaignMessageDeliveryReportRequest() {
        DateTime startTime = DateTime.now();
        DateTime endTime = DateTime.now().plusMinutes(2);
        CallDetailRecord callDetailRecord = new CallDetailRecord();
        callDetailRecord.setStartTime(startTime.toString());
        callDetailRecord.setEndTime(endTime.toString());

        OBDRequest obdRequest = new OBDRequest();
        obdRequest.setMsisdn("1234567890");
        obdRequest.setServiceOption("HELP");
        obdRequest.setCampaignId("CampaignId");
        obdRequest.setCallDetailRecord(callDetailRecord);

        Integer retryCount = 3;
        String subscriptionId = "subscriptionId";
        CampaignMessageDeliveryReportRequestMapper campaignMessageDeliveryReportRequestMapper = new CampaignMessageDeliveryReportRequestMapper();
        OBDRequestWrapper obdRequestWrapper = new OBDRequestWrapper(obdRequest, subscriptionId, DateTime.now());

        CampaignMessageDeliveryReportRequest actualDeliveryReportRequest = campaignMessageDeliveryReportRequestMapper.mapFrom(obdRequestWrapper, retryCount);

        assertEquals(subscriptionId, actualDeliveryReportRequest.getSubscriptionId());
        assertEquals(obdRequest.getMsisdn(), actualDeliveryReportRequest.getMsisdn());
        assertEquals(obdRequest.getCampaignId(), actualDeliveryReportRequest.getCampaignId());
        assertEquals(obdRequest.getServiceOption(), actualDeliveryReportRequest.getServiceOption());
        assertEquals(retryCount.toString(), actualDeliveryReportRequest.getRetryCount());
        assertEquals(startTime.toString(), obdRequest.getCallDetailRecord().getStartTime());
        assertEquals(endTime.toString(), obdRequest.getCallDetailRecord().getEndTime());
    }
}