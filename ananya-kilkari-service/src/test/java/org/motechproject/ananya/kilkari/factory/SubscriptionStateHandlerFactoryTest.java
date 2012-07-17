package org.motechproject.ananya.kilkari.factory;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.handlers.callback.subscription.*;
import org.motechproject.ananya.kilkari.request.CallbackRequest;
import org.motechproject.ananya.kilkari.request.CallbackRequestWrapper;
import org.motechproject.ananya.kilkari.subscription.service.SubscriptionService;

import static junit.framework.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

public class SubscriptionStateHandlerFactoryTest {
    @Mock
    private SubscriptionService subscriptionService;
    @Mock
    private ActivateHandler activateHandler;
    @Mock
    private ActivationFailedHandler activationFailedHandler;
    @Mock
    private RenewalSuccessHandler renewalSuccessHandler;
    @Mock
    private RenewalSuspensionHandler renewalSuspensionHandler;
    @Mock
    private DeactivateHandler deactivateHandler;
    private SubscriptionStateHandlerFactory subscriptionStateHandlerFactory;

    @Before
    public void setUp() {
        initMocks(this);
        subscriptionStateHandlerFactory = new SubscriptionStateHandlerFactory(activateHandler, activationFailedHandler,
                renewalSuccessHandler, renewalSuspensionHandler, deactivateHandler);
    }

    @Test
    public void shouldReturnTheActivationHandlerGivenAnActivationSuccessCallbackRequestWrapper() {
        CallbackRequest callbackRequest = new CallbackRequest();
        callbackRequest.setAction("ACT");
        callbackRequest.setStatus("SUCCESS");
        CallbackRequestWrapper callbackRequestWrapper = new CallbackRequestWrapper(callbackRequest, "abcd1234", DateTime.now());

        SubscriptionStateHandler subscriptionStateHandler = subscriptionStateHandlerFactory.getHandler(callbackRequestWrapper);

        assertTrue(subscriptionStateHandler instanceof ActivateHandler);
    }

   @Test
    public void shouldReturnTheActivationFailedHandlerGivenAnActivationCallbackWithBalanceLow() {
        CallbackRequest callbackRequest = new CallbackRequest();
        callbackRequest.setAction("ACT");
        callbackRequest.setStatus("BAL_LOW");
        CallbackRequestWrapper callbackRequestWrapper = new CallbackRequestWrapper(callbackRequest, "abcd1234", DateTime.now());

        SubscriptionStateHandler subscriptionStateHandler = subscriptionStateHandlerFactory.getHandler(callbackRequestWrapper);

        assertTrue(subscriptionStateHandler instanceof ActivationFailedHandler);
    }

    @Test
    public void shouldReturnTheRenewalSuccessHandlerGivenARenewalStatusSuccess() {
        CallbackRequest callbackRequest = new CallbackRequest();
        callbackRequest.setAction("REN");
        callbackRequest.setStatus("SUCCESS");
        CallbackRequestWrapper callbackRequestWrapper = new CallbackRequestWrapper(callbackRequest, "abcd1234", DateTime.now());

        SubscriptionStateHandler subscriptionStateHandler = subscriptionStateHandlerFactory.getHandler(callbackRequestWrapper);

        assertTrue(subscriptionStateHandler instanceof RenewalSuccessHandler);
    }

    @Test
    public void shouldReturnTheRenewalSuspensionHandlerGivenARenewalStatusBalanceLow() {
        CallbackRequest callbackRequest = new CallbackRequest();
        callbackRequest.setAction("REN");
        callbackRequest.setStatus("BAL_LOW");
        CallbackRequestWrapper callbackRequestWrapper = new CallbackRequestWrapper(callbackRequest, "abcd1234", DateTime.now());

        SubscriptionStateHandler subscriptionStateHandler = subscriptionStateHandlerFactory.getHandler(callbackRequestWrapper);

        assertTrue(subscriptionStateHandler instanceof RenewalSuspensionHandler);
    }

    @Test
    public void shouldReturnTheDeactivationHandlerGivenDeactivationRequestWithLowBalance() {
        CallbackRequest callbackRequest = new CallbackRequest();
        callbackRequest.setAction("DCT");
        callbackRequest.setStatus("BAL_LOW");
        CallbackRequestWrapper callbackRequestWrapper = new CallbackRequestWrapper(callbackRequest, "abcd1234", DateTime.now());

        SubscriptionStateHandler subscriptionStateHandler = subscriptionStateHandlerFactory.getHandler(callbackRequestWrapper);

        assertTrue(subscriptionStateHandler instanceof DeactivateHandler);
    }

    @Test
    public void shouldReturnTheDeactivationHandlerGivenDeactivationRequestWithSuccess() {
        CallbackRequest callbackRequest = new CallbackRequest();
        callbackRequest.setAction("DCT");
        callbackRequest.setStatus("SUCCESS");
        CallbackRequestWrapper callbackRequestWrapper = new CallbackRequestWrapper(callbackRequest, "abcd1234", DateTime.now());

        SubscriptionStateHandler subscriptionStateHandler = subscriptionStateHandlerFactory.getHandler(callbackRequestWrapper);

        assertTrue(subscriptionStateHandler instanceof DeactivateHandler);
    }
}
