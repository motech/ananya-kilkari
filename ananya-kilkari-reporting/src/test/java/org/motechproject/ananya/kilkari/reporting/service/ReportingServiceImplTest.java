package org.motechproject.ananya.kilkari.reporting.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.reporting.domain.*;
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
        reportingServiceImpl = new ReportingServiceImpl(reportGateway, httpClientService, kilkariProperties);
    }
   
    @Test
    public void shouldGetLocation() {
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        when(reportGateway.getLocation(district, block, panchayat)).thenReturn(new SubscriberLocation(district, block, panchayat));

        SubscriberLocation location = reportingServiceImpl.getLocation(district, block, panchayat);

        assertEquals(district, location.getDistrict());
        assertEquals(block, location.getBlock());
        assertEquals(panchayat, location.getPanchayat());
    }
    
    @Test
    public void shouldReportASubscriptionCreation() {
        SubscriptionCreationReportRequest subscriptionCreationReportRequest = mock(SubscriptionCreationReportRequest.class);
        when(kilkariProperties.getProperty("reporting.service.base.url")).thenReturn("url");

        reportingServiceImpl.reportSubscriptionCreation(subscriptionCreationReportRequest);

        verify(httpClientService).post("url/subscription", subscriptionCreationReportRequest);
    }

    @Test
    public void shouldReportASubscriptionStateChange() {
        String subscriptionId = "subscriptionId";
        SubscriptionStateChangeReportRequest subscriptionCreationReportRequest = mock(SubscriptionStateChangeReportRequest.class);
        when(subscriptionCreationReportRequest.getSubscriptionId()).thenReturn(subscriptionId);
        when(kilkariProperties.getProperty("reporting.service.base.url")).thenReturn("url");

        reportingServiceImpl.reportSubscriptionStateChange(subscriptionCreationReportRequest);

        verify(httpClientService).put("url/subscription/"+subscriptionId, subscriptionCreationReportRequest);
    }

    @Test
    public void shouldReportASuccessfulCampaignMessageDelivery() {
        CampaignMessageDeliveryReportRequest campaignMessageDeliveryReportRequest = mock(CampaignMessageDeliveryReportRequest.class);
        when(kilkariProperties.getProperty("reporting.service.base.url")).thenReturn("url");

        reportingServiceImpl.reportCampaignMessageDeliveryStatus(campaignMessageDeliveryReportRequest);

        verify(httpClientService).post("url/callDetails", campaignMessageDeliveryReportRequest);
    }

    @Test
    public void shouldReportASubscriberUpdate() {
        String subscriptionId = "subscriptionId";
        String beneficiaryName = "Name";
        SubscriberReportRequest subscriberReportRequest = new SubscriberReportRequest(subscriptionId, null, beneficiaryName, null, null, null, null);
        when(kilkariProperties.getProperty("reporting.service.base.url")).thenReturn("url");

        reportingServiceImpl.reportSubscriberDetailsChange(subscriberReportRequest);

        ArgumentCaptor<SubscriberRequest> requestCaptor = ArgumentCaptor.forClass(SubscriberRequest.class);
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(httpClientService).put(urlCaptor.capture(), requestCaptor.capture());
        SubscriberRequest requestCaptorValue = requestCaptor.getValue();
        String urlCaptorValue = urlCaptor.getValue();
        assertEquals(beneficiaryName, requestCaptorValue.getBeneficiaryName());
        assertEquals("url/subscriber/" + subscriptionId, urlCaptorValue);
    }
}