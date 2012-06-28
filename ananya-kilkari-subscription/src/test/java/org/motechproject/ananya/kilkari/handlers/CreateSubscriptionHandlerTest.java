package org.motechproject.ananya.kilkari.handlers;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.domain.SubscriptionEventKeys;
import org.motechproject.ananya.kilkari.domain.SubscriptionRequest;
import org.motechproject.ananya.kilkari.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.service.SubscriptionService;
import org.motechproject.scheduler.domain.MotechEvent;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
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
        String channel = "ivr";
        parameters.put("0", new SubscriptionRequest(msisdn, pack, channel, DateTime.now()));

        new CreateSubscriptionHandler(subscriptionService).handleCreateSubscription(new MotechEvent(SubscriptionEventKeys.CREATE_SUBSCRIPTION, parameters));

        ArgumentCaptor<SubscriptionRequest> subscriptionRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionRequest.class);
        verify(subscriptionService).createSubscription(subscriptionRequestArgumentCaptor.capture());
        SubscriptionRequest subscriptionRequest = subscriptionRequestArgumentCaptor.getValue();

        assertEquals(msisdn, subscriptionRequest.getMsisdn());
        assertEquals(pack, subscriptionRequest.getPack());
        assertEquals(channel, subscriptionRequest.getChannel());

    }

    @Test(expected = ValidationException.class)
    public void shouldThrowExceptionIfDetailsAreInvalidWhileHandlingCreateSubscriptionEvent() throws ValidationException {
        doThrow(new ValidationException("Invalid")).when(subscriptionService).createSubscription(any(SubscriptionRequest.class));
        new CreateSubscriptionHandler(subscriptionService).handleCreateSubscription(new MotechEvent(SubscriptionEventKeys.CREATE_SUBSCRIPTION, new HashMap<String, Object>(){{put("0", new SubscriptionRequest("msisdn", "pack", "channel", DateTime.now()));}}));
    }
}
