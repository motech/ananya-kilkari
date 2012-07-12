package org.motechproject.ananya.kilkari.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.domain.Channel;
import org.motechproject.ananya.kilkari.domain.SubscriptionActivationRequest;
import org.motechproject.ananya.kilkari.domain.SubscriptionEventKeys;
import org.motechproject.ananya.kilkari.domain.SubscriptionPack;
import org.motechproject.scheduler.context.EventContext;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class PublisherTest {
    @Mock
    private EventContext eventContext;

    private Publisher publisher;

    @Before
    public void setUp() {
        initMocks(this);
        publisher = new Publisher(eventContext);
    }

    @Test
    public void shouldPublishProcessSubscriptionEventIntoQueue() {
        String subscriptionId = "ABCD1234";
        publisher.processSubscription(new SubscriptionActivationRequest("1234567890", SubscriptionPack.TWELVE_MONTHS, Channel.IVR, subscriptionId));

        ArgumentCaptor<SubscriptionActivationRequest> subscriptionActivationRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionActivationRequest.class);
        ArgumentCaptor<String> eventArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(eventContext).send(eventArgumentCaptor.capture(), subscriptionActivationRequestArgumentCaptor.capture());
        SubscriptionActivationRequest subscriptionActivationRequest = subscriptionActivationRequestArgumentCaptor.getValue();
        String eventName = eventArgumentCaptor.getValue();

        assertEquals(SubscriptionEventKeys.PROCESS_SUBSCRIPTION, eventName);
        assertEquals("1234567890", subscriptionActivationRequest.getMsisdn());
        assertEquals(SubscriptionPack.TWELVE_MONTHS, subscriptionActivationRequest.getPack());
        assertEquals(Channel.IVR, subscriptionActivationRequest.getChannel());
        assertEquals(subscriptionId, subscriptionActivationRequest.getSubscriptionId());
    }
}

