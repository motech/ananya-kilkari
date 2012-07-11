package org.motechproject.ananya.kilkari.factory;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.domain.ActionStatus;
import org.motechproject.ananya.kilkari.handlers.callback.*;
import org.motechproject.ananya.kilkari.request.CallbackRequest;
import org.motechproject.ananya.kilkari.request.CallbackRequestWrapper;
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

        assertEquals(ActivateHandler.class, handlerMappings.get(ActionStatus.createFor("ACT", "SUCCESS")));
        assertEquals(ActivationFailedHandler.class, handlerMappings.get(ActionStatus.createFor("ACT", "BAL_LOW")));
        assertEquals(RenewalSuccessHandler.class, handlerMappings.get(ActionStatus.createFor("REN", "SUCCESS")));
        assertEquals(RenewalSuspensionHandler.class, handlerMappings.get(ActionStatus.createFor("REN", "BAL_LOW")));
        assertEquals(DeactivateHandler.class, handlerMappings.get(ActionStatus.createFor("DCT", "BAL_LOW")));
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
}
