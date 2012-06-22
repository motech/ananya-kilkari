package org.motechproject.ananya.kilkari.handlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.domain.SubscriptionEventKeys;
import org.motechproject.ananya.kilkari.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.service.SubscriptionService;
import org.motechproject.scheduler.domain.MotechEvent;

import java.util.HashMap;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class CreateSubscriptionHandlerTest {
    @Mock
    private SubscriptionService subscriptionService;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldHandleCreateSubscriptionEvent() throws ValidationException {
        HashMap<String, Object> parameters = new HashMap<>();
        String msisdn = "1234567890";
        String pack = "twelve-months";

        parameters.put("msisdn", msisdn);
        parameters.put("pack", pack);

        new CreateSubscriptionHandler(subscriptionService).handleCreateSubscription(new MotechEvent(SubscriptionEventKeys.CREATE_SUBSCRIPTION, parameters));

        verify(subscriptionService).createSubscription(msisdn, "twelve-months");
    }

    @Test(expected = ValidationException.class)
    public void shouldThrowExceptionIfDetailsAreInvalidWhileHandlingCreateSubscriptionEvent() throws ValidationException {
        doThrow(new ValidationException("Invalid")).when(subscriptionService).createSubscription(any(String.class), any(String.class));
        new CreateSubscriptionHandler(subscriptionService).handleCreateSubscription(new MotechEvent(SubscriptionEventKeys.CREATE_SUBSCRIPTION, new HashMap<String, Object>()));
    }
}
