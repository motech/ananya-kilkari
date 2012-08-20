package org.motechproject.ananya.kilkari.subscription.repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.subscription.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.request.OMSubscriptionRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class OnMobileSubscriptionGatewayImplTest {
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private OnMobileEndpoints onMobileEndpoints;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldInvokeSubscriptionManagerWithActivationSubscriptionDetails() {
        String msisdn = "msisdn";
        SubscriptionPack pack = SubscriptionPack.CHOTI_KILKARI;
        Channel channel = Channel.IVR;
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
    public void shouldThrowExceptionWithAppropriateErrorMessage() {
        String msisdn = "msisdn";
        SubscriptionPack pack = SubscriptionPack.CHOTI_KILKARI;
        Channel channel = Channel.IVR;
        String subscriptionId = "abcd1234";
        HashMap<String, String> urlVariables = new HashMap<>();
        urlVariables.put("msisdn", msisdn);
        urlVariables.put("srvkey", pack.name());
        urlVariables.put("mode", channel.name());
        urlVariables.put("refid", subscriptionId);

        when(onMobileEndpoints.activateSubscriptionURL()).thenReturn("url");
        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "bad", "response body".getBytes(), null)).when(restTemplate).getForEntity("url", String.class, urlVariables);
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("OnMobile subscription request failed with errorCode: 400, error: response body");
        OnMobileSubscriptionGatewayImpl onMobileSubscriptionService = new OnMobileSubscriptionGatewayImpl(restTemplate, onMobileEndpoints);

        onMobileSubscriptionService.activateSubscription(new OMSubscriptionRequest(msisdn, pack, channel, subscriptionId));
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
