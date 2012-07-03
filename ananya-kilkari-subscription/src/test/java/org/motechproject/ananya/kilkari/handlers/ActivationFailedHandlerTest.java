package org.motechproject.ananya.kilkari.handlers;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.domain.CallbackRequest;
import org.motechproject.ananya.kilkari.domain.CallbackRequestWrapper;
import org.motechproject.ananya.kilkari.service.SubscriptionService;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class ActivationFailedHandlerTest {
    @Mock
    private SubscriptionService subscriptionService;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldUpdateSubscriptionStatusToActivationFailed() {
        ActivationFailedHandler activationFailedHandler = new ActivationFailedHandler();
        activationFailedHandler.setSubscriptionService(subscriptionService);
        String subscriptionId = "abcd1234";
        DateTime now = DateTime.now();

        CallbackRequest callbackRequest = new CallbackRequest();
        callbackRequest.setReason("my own reason");
        activationFailedHandler.perform(new CallbackRequestWrapper(callbackRequest, subscriptionId, now));

        verify(subscriptionService).activationFailed(subscriptionId, now, "my own reason");
    }
}
