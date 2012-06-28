package org.motechproject.ananya.kilkari.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.domain.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ReportingServiceTest {
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private Properties kilkariProperties;
    @Captor
    private ArgumentCaptor<Class<String>> responseTypeArgumentCaptor;
    @Captor
    private ArgumentCaptor<HashMap<String,String>> urlVariablesArgumentCaptor;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldInvokeReportingServiceWithSubscriptionDetails() {
        String msisdn = "msisdn";
        String pack = SubscriptionPack.TWELVE_MONTHS.name();
        String channel = Channel.IVR.name();
        String subscriptionId = "abcd1234";
        when(kilkariProperties.getProperty("reporting.service.base.url")).thenReturn("url");

        new ReportingService(restTemplate, kilkariProperties).createSubscription(new SubscriptionCreationReportRequest(msisdn, pack, channel, subscriptionId));

        ArgumentCaptor<String> urlArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<SubscriptionCreationReportRequest> subscriptionReportRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionCreationReportRequest.class);
        verify(restTemplate).postForLocation(urlArgumentCaptor.capture(), subscriptionReportRequestArgumentCaptor.capture(), responseTypeArgumentCaptor.capture(), urlVariablesArgumentCaptor.capture());
        SubscriptionCreationReportRequest subscriptionCreationReportRequest = subscriptionReportRequestArgumentCaptor.getValue();

        verify(kilkariProperties).getProperty("reporting.service.base.url");
        assertEquals("url/subscription", urlArgumentCaptor.getValue());
        assertEquals(msisdn, subscriptionCreationReportRequest.getMsisdn());
        assertEquals(pack, subscriptionCreationReportRequest.getPack());
        assertEquals(channel, subscriptionCreationReportRequest.getChannel());
        assertEquals(subscriptionId, subscriptionCreationReportRequest.getSubscriptionId());
    }

    @Test
    public void shouldInvokeUpdateOnReportingServiceWithSubscriptionStateChangeDetails() {
        String subscriptionId = "abcd1234";
        String subscriptionStatus = SubscriptionStatus.ACTIVE.name();
        when(kilkariProperties.getProperty("reporting.service.base.url")).thenReturn("url");

        new ReportingService(restTemplate, kilkariProperties).updateSubscriptionStateChange(new SubscriptionStateChangeReportRequest(subscriptionId, subscriptionStatus));

        ArgumentCaptor<String> urlArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<SubscriptionStateChangeReportRequest> subscriptionStateChangeReportRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionStateChangeReportRequest.class);
        verify(restTemplate).put(urlArgumentCaptor.capture(), subscriptionStateChangeReportRequestArgumentCaptor.capture(), urlVariablesArgumentCaptor.capture());
        SubscriptionStateChangeReportRequest subscriptionStateChangeReportRequest = subscriptionStateChangeReportRequestArgumentCaptor.getValue();

        verify(kilkariProperties).getProperty("reporting.service.base.url");
        assertEquals("url/updatesubscription/abcd1234", urlArgumentCaptor.getValue());
        assertEquals(subscriptionId, subscriptionStateChangeReportRequest.getSubscriptionId());
        assertEquals(subscriptionStatus, subscriptionStateChangeReportRequest.getStatus());
    }
}
