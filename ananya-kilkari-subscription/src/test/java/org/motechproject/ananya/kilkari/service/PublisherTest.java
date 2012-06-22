package org.motechproject.ananya.kilkari.service;

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
    public void shouldPublishSubscriptionCreationDataIntoQueue() {
        publisher.processSubscription(new SubscriptionRequest("1234567890", "twelve-months", "ivr"));

        ArgumentCaptor<SubscriptionRequest> subscriptionRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionRequest.class);
        ArgumentCaptor<String> eventArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(eventContext).send(eventArgumentCaptor.capture(), subscriptionRequestArgumentCaptor.capture());
        SubscriptionRequest subscriptionRequest = subscriptionRequestArgumentCaptor.getValue();
        String eventName = eventArgumentCaptor.getValue();

        assertEquals(SubscriptionEventKeys.PROCESS_SUBSCRIPTION, eventName);
        assertEquals("1234567890", subscriptionRequest.getMsisdn());
        assertEquals("twelve-months", subscriptionRequest.getPack());
        assertEquals("ivr", subscriptionRequest.getChannel());
    }
}

