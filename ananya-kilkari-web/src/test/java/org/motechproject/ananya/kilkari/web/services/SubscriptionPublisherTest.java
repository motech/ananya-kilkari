package org.motechproject.ananya.kilkari.web.services;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.domain.SubscriptionEventKeys;
import org.motechproject.ananya.kilkari.domain.SubscriptionRequest;
import org.motechproject.scheduler.context.EventContext;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class SubscriptionPublisherTest {
    @Mock
    private EventContext eventContext;

    private SubscriptionPublisher subscriptionPublisher;

    @Before
    public void setUp(){
        initMocks(this);
        subscriptionPublisher = new SubscriptionPublisher(eventContext);
    }

    @Test
    public void shouldPublishSubscriptionCreationDataIntoQueue() {
        String msisdn = "1234567890";
        String pack = "twelve-months";
        String channel = "ivr";
        SubscriptionRequest subscriptionRequest = new SubscriptionRequest(msisdn, pack, channel);

        subscriptionPublisher.createSubscription(subscriptionRequest);

        ArgumentCaptor<SubscriptionRequest> subscriptionRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionRequest.class);
        ArgumentCaptor<String> eventArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(eventContext).send(eventArgumentCaptor.capture(), subscriptionRequestArgumentCaptor.capture());
        SubscriptionRequest actualSubscriptionRequest = subscriptionRequestArgumentCaptor.getValue();
        String event = eventArgumentCaptor.getValue();

        assertEquals(SubscriptionEventKeys.CREATE_SUBSCRIPTION, event);
        assertEquals(msisdn, actualSubscriptionRequest.getMsisdn());
        assertEquals(pack, actualSubscriptionRequest.getPack());
        assertEquals(channel, actualSubscriptionRequest.getChannel());
    }
}

