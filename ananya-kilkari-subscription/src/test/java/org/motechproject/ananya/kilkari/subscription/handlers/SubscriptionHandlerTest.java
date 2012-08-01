package org.motechproject.ananya.kilkari.subscription.handlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.subscription.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionEventKeys;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.repository.OnMobileSubscriptionGateway;
import org.motechproject.ananya.kilkari.subscription.request.OMSubscriptionRequest;
import org.motechproject.ananya.kilkari.subscription.service.SubscriptionService;
import org.motechproject.scheduler.domain.MotechEvent;

import java.util.HashMap;

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
        subscriptionHandler = new SubscriptionHandler(subscriptionService);
    }

    @Test
    public void shouldInvokeReportingServiceToCreateASubscriptionRequest() {
        final String msisdn = "1234567890";
        final SubscriptionPack pack = SubscriptionPack.TWELVE_MONTHS;
        final Channel channel = Channel.IVR;
        final String subscriptionId = "abcd1234";
        final OMSubscriptionRequest omSubscriptionRequest = new OMSubscriptionRequest(msisdn, pack, channel, subscriptionId);
        HashMap<String, Object> parameters = new HashMap<String, Object>() {{
            put("0", omSubscriptionRequest);
        }};

        subscriptionHandler.handleSubscriptionActivation(new MotechEvent(SubscriptionEventKeys.ACTIVATE_SUBSCRIPTION, parameters));

        verify(subscriptionService).activationRequested(omSubscriptionRequest);
    }

    @Test
    public void shouldDeactivateSubscription() {
        final String msisdn = "msisdn";
        final SubscriptionPack pack = SubscriptionPack.TWELVE_MONTHS;
        final Channel channel = Channel.IVR;
        final String subscriptionId = "abcd1234";
        final OMSubscriptionRequest omSubscriptionRequest = new OMSubscriptionRequest(msisdn, pack, channel, subscriptionId);
        HashMap<String, Object> parameters = new HashMap<String, Object>() {{
            put("0", omSubscriptionRequest);
        }};

        subscriptionHandler.handleSubscriptionDeactivation(new MotechEvent(SubscriptionEventKeys.DEACTIVATE_SUBSCRIPTION, parameters));

        verify(subscriptionService).deactivationRequested(omSubscriptionRequest);
    }

    @Test
    public void shouldHandleSubscriptionComplete() {
        final String msisdn = "9988776655";
        final SubscriptionPack pack = SubscriptionPack.TWELVE_MONTHS;
        final String subscriptionId = "abcd1234";
        final OMSubscriptionRequest omSubscriptionRequest = new OMSubscriptionRequest(msisdn, pack, null, subscriptionId);
        HashMap<String, Object> parameters = new HashMap<String, Object>() {{
            put("0", omSubscriptionRequest);
        }};

        subscriptionHandler.handleSubscriptionComplete(new MotechEvent(SubscriptionEventKeys.SUBSCRIPTION_COMPLETE, parameters));

        verify(subscriptionService).subscriptionComplete(omSubscriptionRequest);
    }

    @Test
    public void shouldHandleEarlySubscription() {
        final String msisdn = "9988776655";
        final SubscriptionPack pack = SubscriptionPack.TWELVE_MONTHS;
        final String subscriptionId = "abcd1234";
        final OMSubscriptionRequest omSubscriptionRequest = new OMSubscriptionRequest(msisdn, pack, null, subscriptionId);
        HashMap<String, Object> parameters = new HashMap<String, Object>() {{
            put("0", omSubscriptionRequest);
        }};

        subscriptionHandler.handleEarlySubscription(new MotechEvent(SubscriptionEventKeys.EARLY_SUBSCRIPTION, parameters));

        verify(subscriptionService).initiateActivationRequest(omSubscriptionRequest);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionsRaisedByOnMobileSubscriptionServiceToCreateAnActivationRequest() {
        HashMap<String, Object> parameters = new HashMap<String, Object>() {{
            put("0", null);
        }};

        doThrow(new RuntimeException()).when(onMobileSubscriptionGateway).activateSubscription(any(OMSubscriptionRequest.class));

        subscriptionHandler.handleSubscriptionActivation(new MotechEvent(SubscriptionEventKeys.ACTIVATE_SUBSCRIPTION, parameters));
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionsRaisedByOnMobileSubscriptionServiceToCreateAnDeactivationRequest() {
        HashMap<String, Object> parameters = new HashMap<String, Object>() {{
            put("0", null);
        }};

        doThrow(new RuntimeException()).when(onMobileSubscriptionGateway).deactivateSubscription(any(OMSubscriptionRequest.class));

        subscriptionHandler.handleSubscriptionDeactivation(new MotechEvent(SubscriptionEventKeys.DEACTIVATE_SUBSCRIPTION, parameters));
    }
}
