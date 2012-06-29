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
        SubscriptionStateHandlerFactory subscriptionStateHandlerFactory = new SubscriptionStateHandlerFactory(subscriptionService);
        HashMap<String,Class> handlerMappings = subscriptionStateHandlerFactory.getHandlerMappings();

        assertEquals(ActivateHandler.class, handlerMappings.get("ACT|SUCCESS"));
        assertEquals(ActivationFailedHandler.class, handlerMappings.get("ACT|FAILURE"));
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
    public void shouldReturnTheActivationFailedHandlerGivenAnActivationFailedCallbackRequestWrapper() {
        CallbackRequest callbackRequest = new CallbackRequest();
        callbackRequest.setAction("ACT");
        callbackRequest.setStatus("FAILURE");
        CallbackRequestWrapper callbackRequestWrapper = new CallbackRequestWrapper(callbackRequest, "abcd1234", DateTime.now());

        SubscriptionStateHandler subscriptionStateHandler = new SubscriptionStateHandlerFactory(subscriptionService).getHandler(callbackRequestWrapper);

        assertEquals(ActivationFailedHandler.class, subscriptionStateHandler.getClass());
        assertEquals(subscriptionService, subscriptionStateHandler.getSubscriptionService());
    }
}
