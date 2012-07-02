package org.motechproject.ananya.kilkari.handlers;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.domain.CallbackRequest;
import org.motechproject.ananya.kilkari.domain.CallbackRequestWrapper;
import org.motechproject.ananya.kilkari.domain.SubscriptionEventKeys;
import org.motechproject.ananya.kilkari.exceptions.ValidationException;
import org.motechproject.scheduler.domain.MotechEvent;

import java.util.HashMap;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class CallbackRequestHandlerTest {
    @Mock
    private SubscriptionStateHandlerFactory subscriptionStateHandlerFactory;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldHandleProcessCallbackRequestEvent() throws ValidationException {
        HashMap<String, Object> parameters = new HashMap<>();
        String msisdn = "1234567890";
        String subscriptionId = "abcd1234";
        CallbackRequest callbackRequest = new CallbackRequest();
        callbackRequest.setMsisdn(msisdn);
        callbackRequest.setAction("ACT");
        callbackRequest.setStatus("SUCCESS");
        CallbackRequestWrapper callbackRequestWrapper = new CallbackRequestWrapper(callbackRequest, subscriptionId, DateTime.now());
        parameters.put("0", callbackRequestWrapper);

        SubscriptionStateHandler subscriptionStateHandler = mock(SubscriptionStateHandler.class);
        when(subscriptionStateHandlerFactory.getHandler(any(CallbackRequestWrapper.class))).thenReturn(subscriptionStateHandler);

        new CallbackRequestHandler(subscriptionStateHandlerFactory).handleCallbackRequest(new MotechEvent(SubscriptionEventKeys.PROCESS_CALLBACK_REQUEST, parameters));

        verify(subscriptionStateHandlerFactory).getHandler(callbackRequestWrapper);
        verify(subscriptionStateHandler).perform(callbackRequestWrapper);
    }
}
