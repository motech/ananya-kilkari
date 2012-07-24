package org.motechproject.ananya.kilkari.subscription.handlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.subscription.builder.SubscriptionBuilder;
import org.motechproject.ananya.kilkari.subscription.contract.OMSubscriptionRequest;
import org.motechproject.ananya.kilkari.subscription.domain.*;
import org.motechproject.ananya.kilkari.subscription.gateway.OnMobileSubscriptionGateway;
import org.motechproject.ananya.kilkari.subscription.service.SubscriptionService;
import org.motechproject.scheduler.domain.MotechEvent;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
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
        HashMap<String, Object> parameters = new HashMap<String, Object>(){{put("0", new OMSubscriptionRequest(msisdn, pack, null, subscriptionId));}};
        Subscription subscription = new SubscriptionBuilder().withMsisdn(msisdn).withStatus(SubscriptionStatus.ACTIVE).build();
        when(subscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(subscription);

        subscriptionHandler.handleSubscriptionComplete(new MotechEvent(SubscriptionEventKeys.SUBSCRIPTION_COMPLETE, parameters));

        ArgumentCaptor<OMSubscriptionRequest> processSubscriptionRequestArgumentCaptor = ArgumentCaptor.forClass(OMSubscriptionRequest.class);
        verify(onMobileSubscriptionGateway).deactivateSubscription(processSubscriptionRequestArgumentCaptor.capture());
        OMSubscriptionRequest OMSubscriptionRequest = processSubscriptionRequestArgumentCaptor.getValue();

        assertEquals(msisdn, OMSubscriptionRequest.getMsisdn());
        assertEquals(null, OMSubscriptionRequest.getChannel());
        assertEquals(pack, OMSubscriptionRequest.getPack());
        assertEquals(subscriptionId, OMSubscriptionRequest.getSubscriptionId());

        verify(subscriptionService).subscriptionComplete(subscriptionId);
    }

    @Test
    public void shouldNotSendDeactivationRequestAgainIfTheExistingSubscriptionIsAlreadyInDeactivatedState() {
        final String msisdn = "9988776655";
        final String subscriptionId = "abcd1234";
        final SubscriptionPack pack = SubscriptionPack.TWELVE_MONTHS;
        Subscription subscription = new SubscriptionBuilder().withMsisdn(msisdn).withStatus(SubscriptionStatus.DEACTIVATED).build();
        when(subscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(subscription);
        HashMap<String, Object> parameters = new HashMap<String, Object>(){{put("0", new OMSubscriptionRequest(msisdn, pack, null, subscriptionId));}};

        new SubscriptionHandler(onMobileSubscriptionGateway, subscriptionService).handleSubscriptionComplete(new MotechEvent(SubscriptionEventKeys.SUBSCRIPTION_COMPLETE, parameters));

        verify(onMobileSubscriptionGateway, never()).deactivateSubscription(any(OMSubscriptionRequest.class));
        verify(subscriptionService, never()).subscriptionComplete(subscriptionId);
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
