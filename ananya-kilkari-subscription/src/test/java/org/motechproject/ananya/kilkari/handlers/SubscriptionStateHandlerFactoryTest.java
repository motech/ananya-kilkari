package org.motechproject.ananya.kilkari.handlers;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.domain.CallbackRequest;
import org.motechproject.ananya.kilkari.domain.CallbackRequestWrapper;
import org.motechproject.ananya.kilkari.service.SubscriptionService;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;

public class SubscriptionStateHandlerFactoryTest {
    @Mock
    private SubscriptionService subscriptionService;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldPopulateHandlerMappings() {
        HashMap<ActionStatus, Class> handlerMappings = SubscriptionStateHandlerFactory.handlerMappings;

        assertEquals(ActivateHandler.class, handlerMappings.get(new ActionStatus("ACT", "SUCCESS")));
        assertEquals(ActivationFailedHandler.class, handlerMappings.get(new ActionStatus("ACT", "FAILURE")));
        assertEquals(RenewalSuccessHandler.class, handlerMappings.get(new ActionStatus("REN", "SUCCESS")));
        assertEquals(RenewalSuspensionHandler.class, handlerMappings.get(new ActionStatus("REN", "BAL_LOW")));
        assertEquals(DeactivateHandler.class, handlerMappings.get(new ActionStatus("DCT", "BAL_LOW")));
    }

    @Test
    public void shouldReturnTheActivationHandlerGivenAnActivationSuccessCallbackRequestWrapper() {
        CallbackRequest callbackRequest = new CallbackRequest();
        callbackRequest.setAction("ACT");
        callbackRequest.setStatus("SUCCESS");
        CallbackRequestWrapper callbackRequestWrapper = new CallbackRequestWrapper(callbackRequest, "abcd1234", DateTime.now());

        SubscriptionStateHandler subscriptionStateHandler = new SubscriptionStateHandlerFactory(subscriptionService).getHandler(callbackRequestWrapper);

        assertEquals(ActivateHandler.class, subscriptionStateHandler.getClass());
        assertEquals(subscriptionService, subscriptionStateHandler.getSubscriptionService());
    }

    @Test
    public void shouldReturnTheActivationFailedHandlerGivenACallbackRequestWrapperWithStatusAnythingOtherThanSuccessForACTAction() {
        CallbackRequest callbackRequest = new CallbackRequest();
        callbackRequest.setAction("ACT");
        callbackRequest.setStatus("anything_other_than_success");
        CallbackRequestWrapper callbackRequestWrapper = new CallbackRequestWrapper(callbackRequest, "abcd1234", DateTime.now());

        SubscriptionStateHandler subscriptionStateHandler = new SubscriptionStateHandlerFactory(subscriptionService).getHandler(callbackRequestWrapper);

        assertEquals(ActivationFailedHandler.class, subscriptionStateHandler.getClass());
        assertEquals(subscriptionService, subscriptionStateHandler.getSubscriptionService());
    }

    @Test
    public void shouldReturnTheRenewalSuccessHandlerGivenARenewalStatusSuccess() {
        CallbackRequest callbackRequest = new CallbackRequest();
        callbackRequest.setAction("REN");
        callbackRequest.setStatus("SUCCESS");
        CallbackRequestWrapper callbackRequestWrapper = new CallbackRequestWrapper(callbackRequest, "abcd1234", DateTime.now());

        SubscriptionStateHandler subscriptionStateHandler = new SubscriptionStateHandlerFactory(subscriptionService).getHandler(callbackRequestWrapper);

        assertEquals(RenewalSuccessHandler.class, subscriptionStateHandler.getClass());
        assertEquals(subscriptionService, subscriptionStateHandler.getSubscriptionService());
    }

    @Test
    public void shouldReturnTheRenewalSuspensionHandlerGivenARenewalStatusBalanceLow() {
        CallbackRequest callbackRequest = new CallbackRequest();
        callbackRequest.setAction("REN");
        callbackRequest.setStatus("BAL_LOW");
        CallbackRequestWrapper callbackRequestWrapper = new CallbackRequestWrapper(callbackRequest, "abcd1234", DateTime.now());

        SubscriptionStateHandler subscriptionStateHandler = new SubscriptionStateHandlerFactory(subscriptionService).getHandler(callbackRequestWrapper);

        assertEquals(RenewalSuspensionHandler.class, subscriptionStateHandler.getClass());
        assertEquals(subscriptionService, subscriptionStateHandler.getSubscriptionService());
    }

    @Test
    public void shouldReturnTheRenewalSuspensionHandlerWithRenewalStatusOtherThanSuccessOrBalanceLow() {
        CallbackRequest callbackRequest = new CallbackRequest();
        callbackRequest.setAction("REN");
        callbackRequest.setStatus("GRACE");
        CallbackRequestWrapper callbackRequestWrapper = new CallbackRequestWrapper(callbackRequest, "abcd1234", DateTime.now());

        SubscriptionStateHandler subscriptionStateHandler = new SubscriptionStateHandlerFactory(subscriptionService).getHandler(callbackRequestWrapper);

        assertEquals(RenewalFailedHandler.class, subscriptionStateHandler.getClass());
        assertEquals(subscriptionService, subscriptionStateHandler.getSubscriptionService());
    }
}
