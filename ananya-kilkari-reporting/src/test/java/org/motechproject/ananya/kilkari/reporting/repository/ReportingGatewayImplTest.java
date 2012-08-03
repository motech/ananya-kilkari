package org.motechproject.ananya.kilkari.reporting.repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.reporting.domain.SubscriberLocation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Properties;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
}
