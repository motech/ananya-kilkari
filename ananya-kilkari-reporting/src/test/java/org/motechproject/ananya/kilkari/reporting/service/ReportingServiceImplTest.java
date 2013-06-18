package org.motechproject.ananya.kilkari.reporting.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.reporting.repository.ReportingGateway;
import org.motechproject.ananya.reports.kilkari.contract.request.*;
import org.motechproject.ananya.reports.kilkari.contract.response.LocationResponse;
import org.motechproject.ananya.reports.kilkari.contract.response.SubscriberResponse;
import org.motechproject.http.client.service.HttpClientService;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ReportingServiceImplTest {

    @Mock
    private ReportingGateway reportGateway;
    @Mock
    private HttpClientService httpClientService;
    @Mock
    private Properties kilkariProperties;
    private ReportingServiceImpl reportingServiceImpl;

    @Before
    public void setUp() {
        initMocks(this);
        reportingServiceImpl = new ReportingServiceImpl(reportGateway);
    }

    @Test
    public void shouldGetLocation() {
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        String state = "state";
        when(reportGateway.getLocation(district, block, panchayat)).thenReturn(new LocationResponse(state, district, block, panchayat));

        LocationResponse location = reportingServiceImpl.getLocation(district, block, panchayat);

        assertEquals(district, location.getDistrict());
        assertEquals(block, location.getBlock());
        assertEquals(panchayat, location.getPanchayat());
    }

    @Test
    public void shouldReportASubscriptionCreation() {
        SubscriptionReportRequest subscriptionCreationReportRequest = mock(SubscriptionReportRequest.class);

        reportingServiceImpl.reportSubscriptionCreation(subscriptionCreationReportRequest);

        verify(reportGateway).reportSubscriptionCreation(subscriptionCreationReportRequest);
    }

    @Test
    public void shouldReportASubscriptionStateChange() {
        SubscriptionStateChangeRequest subscriptionStateChangeRequest = mock(SubscriptionStateChangeRequest.class);

        reportingServiceImpl.reportSubscriptionStateChange(subscriptionStateChangeRequest);

        verify(reportGateway).reportSubscriptionStateChange(subscriptionStateChangeRequest);
    }

    @Test
    public void shouldReportASuccessfulCampaignMessageDelivery() {
        CallDetailsReportRequest campaignMessageDeliveryReportRequest = mock(CallDetailsReportRequest.class);

        reportingServiceImpl.reportCampaignMessageDeliveryStatus(campaignMessageDeliveryReportRequest);

        verify(reportGateway).reportCampaignMessageDeliveryStatus(campaignMessageDeliveryReportRequest);
    }

    @Test
    public void shouldReportASubscriberUpdate() {
        String subscriptionId = "subscriptionId";
        SubscriberReportRequest subscriberReportRequest = new SubscriberReportRequest(null, "Name", null, null);

        reportingServiceImpl.reportSubscriberDetailsChange(subscriptionId, subscriberReportRequest);

        verify(reportGateway).reportSubscriberDetailsChange(subscriptionId, subscriberReportRequest);
    }

    @Test
    public void shouldReportChangeMsisdn() {
        Long msisdn = 9876543210L;
        String subscriptionId = "subscriptionId";
        String reason = "reason";
        SubscriberChangeMsisdnReportRequest reportRequest = new SubscriberChangeMsisdnReportRequest(subscriptionId, msisdn, reason, DateTime.now());

        reportingServiceImpl.reportChangeMsisdnForEarlySubscription(reportRequest);

        verify(reportGateway).reportChangeMsisdnForSubscriber(reportRequest);
    }

    @Test
    public void shouldGetSubscribersByMsisdn() {
        final String msisdn = "1234567890";
        final ArrayList<SubscriberResponse> expectedSubscriber = new ArrayList<SubscriberResponse>() {{
            add(new SubscriberResponse("subscriptionId", "bName", 25, DateTime.now(), DateTime.now(), DateTime.now(), new LocationResponse("s","d", "b", "p"), DateTime.now(), DateTime.now()));
        }};
        when(reportGateway.getSubscribersByMsisdn(msisdn)).thenReturn(expectedSubscriber);

        List<SubscriberResponse> actualSubscriber = reportingServiceImpl.getSubscribersByMsisdn(msisdn);

        verify(reportGateway).getSubscribersByMsisdn(msisdn);
        assertEquals(expectedSubscriber, actualSubscriber);
    }

    @Test
    public void shouldReportOnReceivingACampaignScheduleAlert() {
        CampaignScheduleAlertRequest campaignScheduleAlertRequest = new CampaignScheduleAlertRequest("subscriptionId", "campaignName", DateTime.now());
        reportingServiceImpl.reportCampaignScheduleAlertReceived(campaignScheduleAlertRequest);

        verify(reportGateway).reportCampaignScheduleAlertReceived(campaignScheduleAlertRequest);
    }

    @Test
    public void shouldReportCampaignMessagePackChange() {
        CampaignChangeReportRequest campaignChangeReportRequest = new CampaignChangeReportRequest("INFANT_DEATH", DateTime.now());
        String subscriptionId = "subscriptionId";

        reportingServiceImpl.reportCampaignChange(campaignChangeReportRequest, subscriptionId);

        verify(reportGateway).reportCampaignChange(campaignChangeReportRequest, subscriptionId);
    }

    @Test
    public void shouldReportSubscriberCareReportRequest() {

        SubscriberCareReportRequest subscriberCareReportRequest = new SubscriberCareReportRequest("msisdn", "HELP", "ivr", DateTime.now());

        reportingServiceImpl.reportCareRequest(subscriberCareReportRequest);

        verify(reportGateway).reportCareRequest(subscriberCareReportRequest);

    }
}