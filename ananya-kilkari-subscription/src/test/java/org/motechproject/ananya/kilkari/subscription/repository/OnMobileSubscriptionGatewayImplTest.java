package org.motechproject.ananya.kilkari.subscription.repository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.subscription.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.request.OMSubscriptionRequest;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class OnMobileSubscriptionGatewayImplTest {
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private OnMobileEndpoints onMobileEndpoints;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldInvokeSubscriptionManagerWithActivationSubscriptionDetails() {
        String msisdn = "msisdn";
        SubscriptionPack pack = SubscriptionPack.CHOTI_KILKARI;
        Channel channel = Channel.IVR;
        String username = "thoughtworks";
        String password = "password123";
        String subscriptionId = "abcd1234";
        when(onMobileEndpoints.activateSubscriptionURL()).thenReturn("url");
        OnMobileSubscriptionGatewayImpl onMobileSubscriptionService = new OnMobileSubscriptionGatewayImpl(restTemplate, onMobileEndpoints);

        onMobileSubscriptionService.activateSubscription(new OMSubscriptionRequest(msisdn, pack, channel, subscriptionId));

        HashMap<String, String> urlVariables = new HashMap<>();
        urlVariables.put("msisdn", msisdn);
        urlVariables.put("srvkey", pack.name());
        urlVariables.put("mode", channel.name());
        urlVariables.put("refid", subscriptionId);

        verify(restTemplate).getForEntity("url", String.class, urlVariables);
    }

    @Test
    public void shouldInvokeSubscriptionManagerWithDeactivationSubscriptionDetails() {
        String msisdn = "msisdn";
        SubscriptionPack pack = SubscriptionPack.CHOTI_KILKARI;
        Channel channel = Channel.IVR;
        String subscriptionId = "abcd1234";
        when(onMobileEndpoints.deactivateSubscriptionURL()).thenReturn("url");
        OnMobileSubscriptionGatewayImpl onMobileSubscriptionService = new OnMobileSubscriptionGatewayImpl(restTemplate, onMobileEndpoints);

        onMobileSubscriptionService.deactivateSubscription(new OMSubscriptionRequest(msisdn, pack, channel, subscriptionId));

        HashMap<String, String> urlVariables = new HashMap<>();
        urlVariables.put("msisdn", msisdn);
        urlVariables.put("srvkey", pack.name());
        urlVariables.put("mode", channel.name());
        urlVariables.put("refid", subscriptionId);

        verify(restTemplate).getForEntity("url", String.class, urlVariables);
    }
}
