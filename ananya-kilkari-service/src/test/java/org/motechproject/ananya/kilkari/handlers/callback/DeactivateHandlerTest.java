package org.motechproject.ananya.kilkari.handlers.callback;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.request.CallbackRequest;
import org.motechproject.ananya.kilkari.request.CallbackRequestWrapper;
import org.motechproject.ananya.kilkari.service.SubscriptionService;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class DeactivateHandlerTest {

    private DeactivateHandler deactivateHandler;
    @Mock
    private SubscriptionService subscriptionService;

    @Before
    public void setup() {
        initMocks(this);
        deactivateHandler = new DeactivateHandler(subscriptionService);
    }

    @Test
    public void shouldInvokeSubscriptionServiceForDeactivation(){
        final String subscriptionId = "sub123";
        final DateTime deactivationDate = DateTime.now();
        final String reason = "low balance";
        final String graceCount = "7";
        final CallbackRequest callbackRequest = new CallbackRequest();
        callbackRequest.setGraceCount(graceCount);
        callbackRequest.setReason(reason);

        deactivateHandler.perform(new CallbackRequestWrapper(callbackRequest, subscriptionId, deactivationDate));

        verify(subscriptionService).deactivateSubscription(subscriptionId, deactivationDate, reason, Integer.valueOf(graceCount));
    }
}
