package org.motechproject.ananya.kilkari.handlers.callback;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.domain.Operator;
import org.motechproject.ananya.kilkari.request.CallbackRequest;
import org.motechproject.ananya.kilkari.request.CallbackRequestWrapper;
import org.motechproject.ananya.kilkari.service.SubscriptionService;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class ActivateHandlerTest {
    @Mock
    private SubscriptionService subscriptionService;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldInvokeSubscriptionServiceToActivateASubscription() {
        ActivateHandler activateHandler = new ActivateHandler(subscriptionService);
        String subscriptionId = "abcd1234";
        String operator = Operator.AIRTEL.name();
        DateTime now = DateTime.now();

        CallbackRequest callbackRequest = new CallbackRequest();
        callbackRequest.setOperator(operator);
        activateHandler.perform(new CallbackRequestWrapper(callbackRequest, subscriptionId, now));

        verify(subscriptionService).activate(subscriptionId, now, operator);
    }
}
