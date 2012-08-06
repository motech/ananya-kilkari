package org.motechproject.ananya.kilkari.reporting.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.contract.request.CallDetailsReportRequest;
import org.motechproject.ananya.kilkari.contract.request.SubscriberReportRequest;
import org.motechproject.ananya.kilkari.contract.request.SubscriptionReportRequest;
import org.motechproject.ananya.kilkari.contract.request.SubscriptionStateChangeRequest;
import org.motechproject.ananya.kilkari.contract.response.LocationResponse;
import org.motechproject.ananya.kilkari.reporting.repository.ReportingGateway;
import org.motechproject.http.client.service.HttpClientService;

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
        when(reportGateway.getLocation(district, block, panchayat)).thenReturn(new LocationResponse(district, block, panchayat));

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
        SubscriberReportRequest subscriberReportRequest = new SubscriberReportRequest(null, "Name", null, null, null, null);

        reportingServiceImpl.reportSubscriberDetailsChange(subscriptionId, subscriberReportRequest);

        verify(reportGateway).reportSubscriberDetailsChange(subscriptionId, subscriberReportRequest);
    }
}