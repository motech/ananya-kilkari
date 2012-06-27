package org.motechproject.ananya.kilkari.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.domain.Channel;
import org.motechproject.ananya.kilkari.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.domain.SubscriptionReportRequest;
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

        new ReportingService(restTemplate, kilkariProperties).createSubscription(new SubscriptionReportRequest(msisdn, pack, channel, subscriptionId));

        ArgumentCaptor<String> urlArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<SubscriptionReportRequest> subscriptionReportRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionReportRequest.class);
        verify(restTemplate).postForLocation(urlArgumentCaptor.capture(), subscriptionReportRequestArgumentCaptor.capture(), responseTypeArgumentCaptor.capture(), urlVariablesArgumentCaptor.capture());
        SubscriptionReportRequest subscriptionReportRequest = subscriptionReportRequestArgumentCaptor.getValue();

        verify(kilkariProperties).getProperty("reporting.service.base.url");
        assertEquals("url/subscription", urlArgumentCaptor.getValue());
        assertEquals(msisdn, subscriptionReportRequest.getMsisdn());
        assertEquals(pack, subscriptionReportRequest.getPack());
        assertEquals(channel, subscriptionReportRequest.getChannel());
        assertEquals(subscriptionId, subscriptionReportRequest.getSubscriptionId());
    }
}
