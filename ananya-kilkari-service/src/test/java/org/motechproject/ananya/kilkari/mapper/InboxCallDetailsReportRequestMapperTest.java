package org.motechproject.ananya.kilkari.mapper;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessageStatus;
import org.motechproject.ananya.kilkari.request.CallDurationWebRequest;
import org.motechproject.ananya.kilkari.request.InboxCallDetailsWebRequest;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.validators.DateUtils;
import org.motechproject.ananya.reports.kilkari.contract.request.CallDetailsReportRequest;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class InboxCallDetailsReportRequestMapperTest {

    @Test
    public void shouldMapInboxCallDetailsWebRequest() {
        String startTime = DateTime.now().toString("dd-MM-yyyy HH-mm-ss");
        String endTime = DateTime.now().plusMinutes(2).toString("dd-MM-yyyy HH-mm-ss");
        String subscriptionId = "subscriptionId";
        CallDurationWebRequest callDurationWebRequest = new CallDurationWebRequest(startTime, endTime);
        String msisdn = "1234567890";
        String campaignId = "WEEK12";
        String pack = SubscriptionPack.NAVJAAT_KILKARI.name();
        InboxCallDetailsWebRequest webRequest = new InboxCallDetailsWebRequest(msisdn, campaignId, callDurationWebRequest, pack, subscriptionId);

        CallDetailsReportRequest actualDeliveryReportRequest = InboxCallDetailsReportRequestMapper.mapFrom(webRequest);

        assertEquals(subscriptionId, actualDeliveryReportRequest.getSubscriptionId());
        assertEquals(msisdn, actualDeliveryReportRequest.getMsisdn());
        assertEquals(campaignId, actualDeliveryReportRequest.getCampaignId());
        assertNull(actualDeliveryReportRequest.getServiceOption());
        assertEquals(CampaignMessageStatus.SUCCESS.name(), actualDeliveryReportRequest.getStatus());
        assertEquals(DateUtils.parseDateTime(startTime), actualDeliveryReportRequest.getStartTime());
        assertEquals(DateUtils.parseDateTime(endTime), actualDeliveryReportRequest.getEndTime());
        assertEquals("INBOX", actualDeliveryReportRequest.getCallSource());
    }
}