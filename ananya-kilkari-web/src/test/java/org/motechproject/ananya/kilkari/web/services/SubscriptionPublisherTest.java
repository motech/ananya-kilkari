package org.motechproject.ananya.kilkari.web.services;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.domain.SubscriptionEventKeys;
import org.motechproject.scheduler.context.EventContext;

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

        subscriptionPublisher.createSubscription(msisdn, pack);

        verify(eventContext).send(SubscriptionEventKeys.CREATE_SUBSCRIPTION, msisdn, pack);
    }
}

