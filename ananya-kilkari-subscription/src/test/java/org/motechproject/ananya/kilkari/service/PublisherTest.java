package org.motechproject.ananya.kilkari.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.domain.SubscriptionEventKeys;
import org.motechproject.ananya.kilkari.handlers.ProcessSubscriptionHandler;
import org.motechproject.scheduler.context.EventContext;

import java.util.HashMap;

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
        HashMap<String, String> subscriptionDetails = new HashMap<>();
        subscriptionDetails.put(ProcessSubscriptionHandler.MSISDN, "1234567890");
        subscriptionDetails.put(ProcessSubscriptionHandler.PACK, "twelve-months");
        publisher.processSubscription(subscriptionDetails);

        verify(eventContext).send(SubscriptionEventKeys.PROCESS_SUBSCRIPTION, subscriptionDetails);
    }
}

