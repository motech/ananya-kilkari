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

public class ActivateHandlerTest {
    @Mock
    private SubscriptionService subscriptionService;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldInvokeSubscriptionServiceToActivateASubscription() {
        ActivateHandler activateHandler = new ActivateHandler();
        activateHandler.setSubscriptionService(subscriptionService);
        String subscriptionId = "abcd1234";
        DateTime now = DateTime.now();

        activateHandler.perform(new CallbackRequestWrapper(new CallbackRequest(), subscriptionId, now));

        verify(subscriptionService).activate(subscriptionId, now);
    }
}
