package org.motechproject.ananya.kilkari.reporting.repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.reporting.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Properties;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ReportingGatewayImplTest {
    private ReportingGatewayImpl reportingGateway;
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private Properties kilkariProperties;
    @Captor
    private ArgumentCaptor<Class<String>> responseTypeArgumentCaptor;
    @Captor
    private ArgumentCaptor<HashMap<String, String>> urlVariablesArgumentCaptor;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        initMocks(this);
        reportingGateway = new ReportingGatewayImpl(restTemplate, kilkariProperties);
    }

    @Test
    public void shouldInvokeReportingServiceWithSubscriptionDetails() {
        when(kilkariProperties.getProperty("reporting.service.base.url")).thenReturn("url");
        SubscriptionCreationReportRequest creationReportRequest = mock(SubscriptionCreationReportRequest.class);

        reportingGateway.createSubscription(creationReportRequest);

        ArgumentCaptor<String> urlArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<SubscriptionCreationReportRequest> subscriptionReportRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionCreationReportRequest.class);
        verify(restTemplate).postForLocation(urlArgumentCaptor.capture(), subscriptionReportRequestArgumentCaptor.capture(), responseTypeArgumentCaptor.capture(), urlVariablesArgumentCaptor.capture());
        SubscriptionCreationReportRequest actualCreationReportRequest = subscriptionReportRequestArgumentCaptor.getValue();

        verify(kilkariProperties).getProperty("reporting.service.base.url");
        assertEquals("url/subscription", urlArgumentCaptor.getValue());
        assertEquals(creationReportRequest, actualCreationReportRequest);
    }

    @Test
    public void shouldInvokeReportingServiceWithGetLocations() {
        when(kilkariProperties.getProperty("reporting.service.base.url")).thenReturn("url");
        SubscriberLocation expectedLocation = new SubscriberLocation("mydistrict", "myblock", "mypanchayat");
        when(restTemplate.getForEntity(any(String.class), any(Class.class))).thenReturn(new ResponseEntity(expectedLocation, HttpStatus.OK));

        SubscriberLocation actualLocation = reportingGateway.getLocation("mydistrict", "myblock", "mypanchayat");

        assertEquals(expectedLocation, actualLocation);

        ArgumentCaptor<String> urlArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Class> subscriberLocationCaptor = ArgumentCaptor.forClass(Class.class);
        verify(restTemplate).getForEntity(urlArgumentCaptor.capture(), subscriberLocationCaptor.capture());

        assertEquals(SubscriberLocation.class, subscriberLocationCaptor.getValue());

        verify(kilkariProperties).getProperty("reporting.service.base.url");
        String url = urlArgumentCaptor.getValue();
        assertTrue(url.startsWith("url/location?"));
        assertTrue(url.contains("district=mydistrict"));
        assertTrue(url.contains("block=myblock"));
        assertTrue(url.contains("panchayat=mypanchayat"));
    }

    @Test
    public void shouldReturnNullIfLocationNotPresent() {
        when(kilkariProperties.getProperty("reporting.service.base.url")).thenReturn("url");
        when(restTemplate.getForEntity(any(String.class), any(Class.class))).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        SubscriberLocation actualLocation = reportingGateway.getLocation("mydistrict", "myblock", "mypanchayat");

        assertNull(actualLocation);

        ArgumentCaptor<String> urlArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Class> subscriberLocationCaptor = ArgumentCaptor.forClass(Class.class);
        verify(restTemplate).getForEntity(urlArgumentCaptor.capture(), subscriberLocationCaptor.capture());

        assertEquals(SubscriberLocation.class, subscriberLocationCaptor.getValue());

        verify(kilkariProperties).getProperty("reporting.service.base.url");
        String url = urlArgumentCaptor.getValue();
        assertTrue(url.startsWith("url/location?"));
        assertTrue(url.contains("district=mydistrict"));
        assertTrue(url.contains("block=myblock"));
        assertTrue(url.contains("panchayat=mypanchayat"));
    }

    @Test
    public void shouldRethrowAnyOtherExceptionOnGetLocation() {
        when(kilkariProperties.getProperty("reporting.service.base.url")).thenReturn("url");
        when(restTemplate.getForEntity(any(String.class), any(Class.class))).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));
        expectedException.expect(HttpClientErrorException.class);
        expectedException.expectMessage("400 BAD_REQUEST");

        reportingGateway.getLocation("mydistrict", "myblock", "mypanchayat");

        ArgumentCaptor<String> urlArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Class> subscriberLocationCaptor = ArgumentCaptor.forClass(Class.class);
        verify(restTemplate).getForEntity(urlArgumentCaptor.capture(), subscriberLocationCaptor.capture());

        assertEquals(SubscriberLocation.class, subscriberLocationCaptor.getValue());

        verify(kilkariProperties).getProperty("reporting.service.base.url");
        String url = urlArgumentCaptor.getValue();
        assertTrue(url.startsWith("url/location?"));
        assertTrue(url.contains("district=mydistrict"));
        assertTrue(url.contains("block=myblock"));
        assertTrue(url.contains("panchayat=mypanchayat"));
    }

    @Test
    public void shouldInvokeReportingServiceWithGetLocationsIfDistrctNotPresent() {
        when(kilkariProperties.getProperty("reporting.service.base.url")).thenReturn("url");
        SubscriberLocation expectedLocation = new SubscriberLocation(null, "myblock", "mypanchayat");
        when(restTemplate.getForEntity(any(String.class), any(Class.class))).thenReturn(new ResponseEntity(expectedLocation, HttpStatus.OK));

        SubscriberLocation actualLocation = reportingGateway.getLocation(null, "myblock", "mypanchayat");

        assertEquals(expectedLocation, actualLocation);

        ArgumentCaptor<String> urlArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Class> subscriberLocationCaptor = ArgumentCaptor.forClass(Class.class);
        verify(restTemplate).getForEntity(urlArgumentCaptor.capture(), subscriberLocationCaptor.capture());

        assertEquals(SubscriberLocation.class, subscriberLocationCaptor.getValue());

        verify(kilkariProperties).getProperty("reporting.service.base.url");
        String url = urlArgumentCaptor.getValue();
        assertTrue(url.startsWith("url/location?"));
        assertTrue(url.contains("block=myblock"));
        assertTrue(url.contains("panchayat=mypanchayat"));
    }

    @Test
    public void shouldInvokeReportingServiceToGetLocationIfDistrictBlockAndPanchayatAreNotPresent() {
        when(kilkariProperties.getProperty("reporting.service.base.url")).thenReturn("url");
        SubscriberLocation expectedLocation = new SubscriberLocation(null, "myblock", "mypanchayat");
        when(restTemplate.getForEntity(any(String.class), any(Class.class))).thenReturn(new ResponseEntity(expectedLocation, HttpStatus.OK));

        SubscriberLocation actualLocation = reportingGateway.getLocation(null, null, null);

        assertEquals(expectedLocation, actualLocation);

        ArgumentCaptor<String> urlArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Class> subscriberLocationCaptor = ArgumentCaptor.forClass(Class.class);
        verify(restTemplate).getForEntity(urlArgumentCaptor.capture(), subscriberLocationCaptor.capture());

        assertEquals(SubscriberLocation.class, subscriberLocationCaptor.getValue());

        verify(kilkariProperties).getProperty("reporting.service.base.url");
        String url = urlArgumentCaptor.getValue();
        assertEquals("url/location", url);
    }

    @Test
    public void shouldInvokeUpdateOnReportingServiceWithSubscriptionStateChangeDetails() {
        when(kilkariProperties.getProperty("reporting.service.base.url")).thenReturn("url");
        SubscriptionStateChangeReportRequest stateChangeReportRequest = mock(SubscriptionStateChangeReportRequest.class);
        when(stateChangeReportRequest.getSubscriptionId()).thenReturn("abcd1234");

        reportingGateway.updateSubscriptionStateChange(stateChangeReportRequest);

        ArgumentCaptor<String> urlArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<SubscriptionStateChangeReportRequest> subscriptionStateChangeReportRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionStateChangeReportRequest.class);
        verify(restTemplate).put(urlArgumentCaptor.capture(), subscriptionStateChangeReportRequestArgumentCaptor.capture(), urlVariablesArgumentCaptor.capture());
        SubscriptionStateChangeReportRequest actualStateChangeReportRequest = subscriptionStateChangeReportRequestArgumentCaptor.getValue();

        verify(kilkariProperties).getProperty("reporting.service.base.url");
        assertEquals("url/updatesubscription/abcd1234", urlArgumentCaptor.getValue());
        assertEquals(stateChangeReportRequest, actualStateChangeReportRequest);
    }

    @Test
    public void shouldInvokeReportingServiceWithCampaignMessageDeliveryDetails() {
        when(kilkariProperties.getProperty("reporting.service.base.url")).thenReturn("url");
        CampaignMessageDeliveryReportRequest deliveryReportRequest = mock(CampaignMessageDeliveryReportRequest.class);

        reportingGateway.reportCampaignMessageDelivery(deliveryReportRequest);

        ArgumentCaptor<String> urlArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<CampaignMessageDeliveryReportRequest> campaignMessageDeliveryReportRequestArgumentCaptor = ArgumentCaptor.forClass(CampaignMessageDeliveryReportRequest.class);
        verify(restTemplate).postForLocation(urlArgumentCaptor.capture(), campaignMessageDeliveryReportRequestArgumentCaptor.capture(), urlVariablesArgumentCaptor.capture());
        CampaignMessageDeliveryReportRequest actualDeliveryReportRequest = campaignMessageDeliveryReportRequestArgumentCaptor.getValue();

        verify(kilkariProperties).getProperty("reporting.service.base.url");
        assertEquals("url/obd/callDetails", urlArgumentCaptor.getValue());
        assertEquals(deliveryReportRequest, actualDeliveryReportRequest);
    }

    @Test
    public void shouldInvokeReportingServiceWithSubscriberDetailsToUpdate() {
        when(kilkariProperties.getProperty("reporting.service.base.url")).thenReturn("url");
        SubscriberReportRequest reportRequest = mock(SubscriberReportRequest.class);
        String subscriptionId = "abcd1234";
        when(reportRequest.getSubscriptionId()).thenReturn(subscriptionId);

        reportingGateway.updateSubscriberDetails(reportRequest);

        verify(kilkariProperties).getProperty("reporting.service.base.url");
        ArgumentCaptor<String> urlArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<SubscriberRequest> requestArgumentCaptor = ArgumentCaptor.forClass(SubscriberRequest.class);
        verify(restTemplate).put(urlArgumentCaptor.capture(), requestArgumentCaptor.capture(), urlVariablesArgumentCaptor.capture());
        String url = urlArgumentCaptor.getValue();
        SubscriberRequest actualReportRequest = requestArgumentCaptor.getValue();

        assertEquals("url/subscriber/" + subscriptionId, url);
        assertEquals(reportRequest.getBeneficiaryAge(), actualReportRequest.getBeneficiaryAge());
        assertEquals(reportRequest.getExpectedDateOfDelivery(), actualReportRequest.getExpectedDateOfDelivery());
        assertEquals(reportRequest.getBeneficiaryName(), actualReportRequest.getBeneficiaryName());
        assertEquals(reportRequest.getDateOfBirth(), actualReportRequest.getDateOfBirth());
        assertEquals(reportRequest.getLocation(), actualReportRequest.getLocation());
        assertEquals(reportRequest.getCreatedAt(), actualReportRequest.getCreatedAt());
    }
}
