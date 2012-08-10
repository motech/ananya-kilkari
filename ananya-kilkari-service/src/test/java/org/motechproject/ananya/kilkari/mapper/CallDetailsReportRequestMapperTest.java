package org.motechproject.ananya.kilkari.mapper;

import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.Mockito;
import org.motechproject.ananya.kilkari.contract.request.CallDetailsReportRequest;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessageStatus;
import org.motechproject.ananya.kilkari.obd.domain.ServiceOption;
import org.motechproject.ananya.kilkari.request.CallDurationRequest;
import org.motechproject.ananya.kilkari.request.CallDurationWebRequest;
import org.motechproject.ananya.kilkari.request.InboxCallDetailsWebRequest;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallDetailsRequest;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.validators.DateUtils;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

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
        assertEquals(retryCount.toString(), actualDeliveryReportRequest.getRetryCount());
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
        assertEquals(retryCount.toString(), actualDeliveryReportRequest.getRetryCount());
        assertEquals(startTime, obdSuccessfulCallDetailsRequest.getCallDurationRequest().getStartTime());
        assertEquals(endTime, obdSuccessfulCallDetailsRequest.getCallDurationRequest().getEndTime());
        assertEquals("OBD", actualDeliveryReportRequest.getCallSource());
    }

    @Test
    public void shouldMapInboxCallDetailsWebRequest() {
        String startTime = DateTime.now().toString("dd-MM-yyyy HH-mm-ss");
        String endTime = DateTime.now().plusMinutes(2).toString("dd-MM-yyyy HH-mm-ss");
        String subscriptionId = "subscriptionId";
        CallDurationWebRequest callDurationWebRequest = new CallDurationWebRequest(startTime, endTime);
        String msisdn = "1234567890";
        String campaignId = "WEEK12";
        String pack = SubscriptionPack.CHOTI_KILKARI.name();
        Subscription subscription = Mockito.mock(Subscription.class);
        when(subscription.getSubscriptionId()).thenReturn(subscriptionId);
        InboxCallDetailsWebRequest webRequest = new InboxCallDetailsWebRequest(msisdn, campaignId, callDurationWebRequest, pack);

        CallDetailsReportRequest actualDeliveryReportRequest = CallDetailsReportRequestMapper.mapFrom(webRequest, subscription);

        assertEquals(subscriptionId, actualDeliveryReportRequest.getSubscriptionId());
        assertEquals(msisdn, actualDeliveryReportRequest.getMsisdn());
        assertEquals(campaignId, actualDeliveryReportRequest.getCampaignId());
        assertNull(actualDeliveryReportRequest.getServiceOption());
        assertEquals(CampaignMessageStatus.SUCCESS.name(), actualDeliveryReportRequest.getStatus());
        assertNull(actualDeliveryReportRequest.getRetryCount());
        assertEquals(DateUtils.parseDateTime(startTime), actualDeliveryReportRequest.getStartTime());
        assertEquals(DateUtils.parseDateTime(endTime), actualDeliveryReportRequest.getEndTime());
        assertEquals("INBOX", actualDeliveryReportRequest.getCallSource());
    }
}