package org.motechproject.ananya.kilkari.handlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.service.KilkariSubscriptionService;
import org.motechproject.ananya.kilkari.utils.SubscriptionRequestBuilder;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionEventKeys;
import org.motechproject.ananya.kilkari.request.SubscriptionRequest;
import org.motechproject.scheduler.domain.MotechEvent;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class CreateSubscriptionHandlerTest {
    @Mock
    private KilkariSubscriptionService kilkariSubscriptionService;

    private CreateSubscriptionHandler createSubscriptionHandler;

    @Before
    public void setUp() {
        initMocks(this);
        createSubscriptionHandler = new CreateSubscriptionHandler(kilkariSubscriptionService);
    }

    @Test
    public void shouldHandleCreateSubscriptionEvent() {
        HashMap<String, Object> parameters = new HashMap<>();
        String msisdn = "1234567890";
        String pack = "twelve-months";
        String channel = "ivr";
        parameters.put("0", createSubscriptionRequest(msisdn, pack, channel));

        createSubscriptionHandler.handleCreateSubscription(new MotechEvent(SubscriptionEventKeys.CREATE_SUBSCRIPTION, parameters));

        ArgumentCaptor<SubscriptionRequest> subscriptionRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionRequest.class);
        verify(kilkariSubscriptionService).createSubscription(subscriptionRequestArgumentCaptor.capture());
        SubscriptionRequest subscriptionRequest = subscriptionRequestArgumentCaptor.getValue();

        assertEquals(msisdn, subscriptionRequest.getMsisdn());
        assertEquals(pack, subscriptionRequest.getPack());
        assertEquals(channel, subscriptionRequest.getChannel());

    }

    private SubscriptionRequest createSubscriptionRequest(String msisdn, String pack, String channel) {
        return new SubscriptionRequestBuilder().withDefaults().withMsisdn(msisdn).withPack(pack).withChannel(channel).build();
    }
}
