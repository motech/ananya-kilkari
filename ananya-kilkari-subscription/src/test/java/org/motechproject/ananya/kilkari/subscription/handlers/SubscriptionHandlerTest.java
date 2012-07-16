package org.motechproject.ananya.kilkari.subscription.handlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.subscription.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.domain.ProcessSubscriptionRequest;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionEventKeys;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.gateway.OnMobileSubscriptionGateway;
import org.motechproject.ananya.kilkari.subscription.service.SubscriptionService;
import org.motechproject.scheduler.domain.MotechEvent;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class SubscriptionHandlerTest {
    @Mock
    private OnMobileSubscriptionGateway onMobileSubscriptionGateway;
    @Mock
    private SubscriptionService subscriptionService;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldInvokeReportingServiceToCreateASubscriptionRequest() {
        final String msisdn = "msisdn";
        final SubscriptionPack pack = SubscriptionPack.TWELVE_MONTHS;
        final Channel channel = Channel.IVR;
        final String subscriptionId = "abcd1234";
        HashMap<String, Object> parameters = new HashMap<String, Object>(){{put("0", new ProcessSubscriptionRequest(msisdn, pack, channel, subscriptionId));}};

        new SubscriptionHandler(onMobileSubscriptionGateway, subscriptionService).handleSubscriptionActivation(new MotechEvent(SubscriptionEventKeys.ACTIVATE_SUBSCRIPTION, parameters));

        ArgumentCaptor<ProcessSubscriptionRequest> subscriptionActivationRequestArgumentCaptor = ArgumentCaptor.forClass(ProcessSubscriptionRequest.class);
        verify(onMobileSubscriptionGateway).activateSubscription(subscriptionActivationRequestArgumentCaptor.capture());
        ProcessSubscriptionRequest processSubscriptionRequest = subscriptionActivationRequestArgumentCaptor.getValue();

        assertEquals(msisdn, processSubscriptionRequest.getMsisdn());
        assertEquals(channel, processSubscriptionRequest.getChannel());
        assertEquals(pack, processSubscriptionRequest.getPack());
        assertEquals(subscriptionId, processSubscriptionRequest.getSubscriptionId());

        verify(onMobileSubscriptionGateway).activateSubscription(processSubscriptionRequest);
        verify(subscriptionService).activationRequested(subscriptionId);
    }

    @Test
    public void shouldDeactivateSubscription() {
        final String msisdn = "msisdn";
        final SubscriptionPack pack = SubscriptionPack.TWELVE_MONTHS;
        final Channel channel = Channel.IVR;
        final String subscriptionId = "abcd1234";
        HashMap<String, Object> parameters = new HashMap<String, Object>(){{put("0", new ProcessSubscriptionRequest(msisdn, pack, channel, subscriptionId));}};

        new SubscriptionHandler(onMobileSubscriptionGateway, subscriptionService).handleSubscriptionDeactivation(new MotechEvent(SubscriptionEventKeys.DEACTIVATE_SUBSCRIPTION, parameters));

        ArgumentCaptor<ProcessSubscriptionRequest> processSubscriptionRequestArgumentCaptor = ArgumentCaptor.forClass(ProcessSubscriptionRequest.class);
        verify(onMobileSubscriptionGateway).deactivateSubscription(processSubscriptionRequestArgumentCaptor.capture());
        ProcessSubscriptionRequest processSubscriptionRequest = processSubscriptionRequestArgumentCaptor.getValue();

        assertEquals(msisdn, processSubscriptionRequest.getMsisdn());
        assertEquals(channel, processSubscriptionRequest.getChannel());
        assertEquals(pack, processSubscriptionRequest.getPack());
        assertEquals(subscriptionId, processSubscriptionRequest.getSubscriptionId());

        verify(onMobileSubscriptionGateway).deactivateSubscription(processSubscriptionRequest);
        verify(subscriptionService).deactivationRequested(subscriptionId);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionsRaisedByOnMobileSubscriptionServiceToCreateAnActivationRequest() {
        HashMap<String, Object> parameters = new HashMap<String, Object>(){{put("0", null);}};

        doThrow(new RuntimeException()).when(onMobileSubscriptionGateway).activateSubscription(any(ProcessSubscriptionRequest.class));

        new SubscriptionHandler(onMobileSubscriptionGateway, subscriptionService).handleSubscriptionActivation(new MotechEvent(SubscriptionEventKeys.ACTIVATE_SUBSCRIPTION, parameters));
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionsRaisedByOnMobileSubscriptionServiceToCreateAnDeactivationRequest() {
        HashMap<String, Object> parameters = new HashMap<String, Object>(){{put("0", null);}};

        doThrow(new RuntimeException()).when(onMobileSubscriptionGateway).deactivateSubscription(any(ProcessSubscriptionRequest.class));

        new SubscriptionHandler(onMobileSubscriptionGateway, subscriptionService).handleSubscriptionDeactivation(new MotechEvent(SubscriptionEventKeys.DEACTIVATE_SUBSCRIPTION, parameters));
    }
}
