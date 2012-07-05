package org.motechproject.ananya.kilkari.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.domain.Channel;
import org.motechproject.ananya.kilkari.domain.SubscriptionActivationRequest;
import org.motechproject.ananya.kilkari.domain.SubscriptionPack;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Properties;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class OnMobileSubscriptionServiceTest {
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private Properties kilkariProperties;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldInvokeSubscriptionManagerWithSubscriptionDetails() {
        String msisdn = "msisdn";
        SubscriptionPack pack = SubscriptionPack.TWELVE_MONTHS;
        Channel channel = Channel.IVR;
        String username = "thoughtworks";
        String password = "password123";
        String subscriptionId = "abcd1234";
        when(kilkariProperties.getProperty("omsm.base.url")).thenReturn("url");
        when(kilkariProperties.getProperty("omsm.username")).thenReturn("thoughtworks");
        when(kilkariProperties.getProperty("omsm.password")).thenReturn("password123");
        new OnMobileSubscriptionService(restTemplate, kilkariProperties).activateSubscription(new SubscriptionActivationRequest(msisdn, pack, channel, subscriptionId));

        HashMap<String, String> urlVariables = new HashMap<>();
        urlVariables.put("msisdn", msisdn);
        urlVariables.put("srvkey", pack.name());
        urlVariables.put("mode", channel.name());
        urlVariables.put("refid", subscriptionId);
        urlVariables.put("user", username);
        urlVariables.put("pass", password);
        verify(restTemplate).getForEntity("url/ActivateSubscription?msisdn={msisdn}&srvkey={srvkey}&mode={mode}&refid={refid}&user={user}&pass={pass}", String.class, urlVariables);
        verify(kilkariProperties).getProperty("omsm.base.url");
        verify(kilkariProperties).getProperty("omsm.username");
        verify(kilkariProperties).getProperty("omsm.password");
    }
}
