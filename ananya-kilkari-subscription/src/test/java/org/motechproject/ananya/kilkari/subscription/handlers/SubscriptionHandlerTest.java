package org.motechproject.ananya.kilkari.subscription.handlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.subscription.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionEventKeys;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.repository.OnMobileSubscriptionGateway;
import org.motechproject.ananya.kilkari.subscription.request.OMSubscriptionRequest;
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
    private SubscriptionHandler subscriptionHandler;

    @Before
    public void setUp() {
        initMocks(this);
        subscriptionHandler = new SubscriptionHandler(onMobileSubscriptionGateway, subscriptionService);
    }

    @Test
    public void shouldInvokeReportingServiceToCreateASubscriptionRequest() {
        final String msisdn = "msisdn";
        final SubscriptionPack pack = SubscriptionPack.TWELVE_MONTHS;
        final Channel channel = Channel.IVR;
        final String subscriptionId = "abcd1234";
        HashMap<String, Object> parameters = new HashMap<String, Object>(){{put("0", new OMSubscriptionRequest(msisdn, pack, channel, subscriptionId));}};


        subscriptionHandler.handleSubscriptionActivation(new MotechEvent(SubscriptionEventKeys.ACTIVATE_SUBSCRIPTION, parameters));

        ArgumentCaptor<OMSubscriptionRequest> subscriptionActivationRequestArgumentCaptor = ArgumentCaptor.forClass(OMSubscriptionRequest.class);
        verify(onMobileSubscriptionGateway).activateSubscription(subscriptionActivationRequestArgumentCaptor.capture());
        OMSubscriptionRequest OMSubscriptionRequest = subscriptionActivationRequestArgumentCaptor.getValue();

        assertEquals(msisdn, OMSubscriptionRequest.getMsisdn());
        assertEquals(channel, OMSubscriptionRequest.getChannel());
        assertEquals(pack, OMSubscriptionRequest.getPack());
        assertEquals(subscriptionId, OMSubscriptionRequest.getSubscriptionId());

        verify(onMobileSubscriptionGateway).activateSubscription(OMSubscriptionRequest);
        verify(subscriptionService).activationRequested(subscriptionId);
    }

    @Test
    public void shouldDeactivateSubscription() {
        final String msisdn = "msisdn";
        final SubscriptionPack pack = SubscriptionPack.TWELVE_MONTHS;
        final Channel channel = Channel.IVR;
        final String subscriptionId = "abcd1234";
        HashMap<String, Object> parameters = new HashMap<String, Object>(){{put("0", new OMSubscriptionRequest(msisdn, pack, channel, subscriptionId));}};

        subscriptionHandler.handleSubscriptionDeactivation(new MotechEvent(SubscriptionEventKeys.DEACTIVATE_SUBSCRIPTION, parameters));

        ArgumentCaptor<OMSubscriptionRequest> processSubscriptionRequestArgumentCaptor = ArgumentCaptor.forClass(OMSubscriptionRequest.class);
        verify(onMobileSubscriptionGateway).deactivateSubscription(processSubscriptionRequestArgumentCaptor.capture());
        OMSubscriptionRequest OMSubscriptionRequest = processSubscriptionRequestArgumentCaptor.getValue();

        assertEquals(msisdn, OMSubscriptionRequest.getMsisdn());
        assertEquals(channel, OMSubscriptionRequest.getChannel());
        assertEquals(pack, OMSubscriptionRequest.getPack());
        assertEquals(subscriptionId, OMSubscriptionRequest.getSubscriptionId());

        verify(onMobileSubscriptionGateway).deactivateSubscription(OMSubscriptionRequest);
        verify(subscriptionService).deactivationRequested(subscriptionId);
    }

    @Test
    public void shouldHandleSubscriptionComplete() {
        final String msisdn = "9988776655";
        final SubscriptionPack pack = SubscriptionPack.TWELVE_MONTHS;
        final String subscriptionId = "abcd1234";
        final OMSubscriptionRequest omSubscriptionRequest = new OMSubscriptionRequest(msisdn, pack, null, subscriptionId);
        HashMap<String, Object> parameters = new HashMap<String, Object>(){{
            put("0", omSubscriptionRequest);}};

        subscriptionHandler.handleSubscriptionComplete(new MotechEvent(SubscriptionEventKeys.SUBSCRIPTION_COMPLETE, parameters));

        verify(subscriptionService).subscriptionComplete(omSubscriptionRequest);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionsRaisedByOnMobileSubscriptionServiceToCreateAnActivationRequest() {
        HashMap<String, Object> parameters = new HashMap<String, Object>(){{put("0", null);}};

        doThrow(new RuntimeException()).when(onMobileSubscriptionGateway).activateSubscription(any(OMSubscriptionRequest.class));

        subscriptionHandler.handleSubscriptionActivation(new MotechEvent(SubscriptionEventKeys.ACTIVATE_SUBSCRIPTION, parameters));
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionsRaisedByOnMobileSubscriptionServiceToCreateAnDeactivationRequest() {
        HashMap<String, Object> parameters = new HashMap<String, Object>(){{put("0", null);}};

        doThrow(new RuntimeException()).when(onMobileSubscriptionGateway).deactivateSubscription(any(OMSubscriptionRequest.class));

        subscriptionHandler.handleSubscriptionDeactivation(new MotechEvent(SubscriptionEventKeys.DEACTIVATE_SUBSCRIPTION, parameters));
    }
}
