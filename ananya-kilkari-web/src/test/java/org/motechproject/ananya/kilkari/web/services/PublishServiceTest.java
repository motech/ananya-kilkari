package org.motechproject.ananya.kilkari.web.services;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.web.domain.SubscriptionEventKeys;
import org.motechproject.scheduler.context.EventContext;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class PublishServiceTest {
    @Mock
    private EventContext eventContext;

    private PublishService publishService;

    @Before
    public void setUp(){
        initMocks(this);
        publishService = new PublishService(eventContext);
    }

    @Test
    public void shouldPublishSubscriptionCreationDataIntoQueue() {
        String msisdn = "1234567890";
        String pack = "twelve-months";

        publishService.createSubscription(msisdn, pack);

        verify(eventContext).send(SubscriptionEventKeys.CREATE_SUBSCRIPTION, msisdn, pack);
    }
}

