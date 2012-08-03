package org.motechproject.ananya.kilkari.handlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.domain.SubscriberCareReasons;
import org.motechproject.ananya.kilkari.domain.SubscriberCareRequest;
import org.motechproject.ananya.kilkari.service.SubscriberCareService;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionEventKeys;
import org.motechproject.scheduler.domain.MotechEvent;

import java.util.HashMap;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class SubscriberCareRequestHandlerTest {
    @Mock
    private SubscriberCareService subscriberCareService;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldInvokeSubscriberCareServiceWithTheRequest() {
        String channel = "ivr";
        SubscriberCareRequest subscriberCareRequest = new SubscriberCareRequest("1234567890", SubscriberCareReasons.HELP.name(), channel);
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("0", subscriberCareRequest);
        MotechEvent motechEvent = new MotechEvent(SubscriptionEventKeys.PROCESS_SUBSCRIBER_CARE_REQUEST, parameters);

        new SubscriberCareRequestHandler(subscriberCareService).handleSubscriberCareRequest(motechEvent);

        verify(subscriberCareService).createSubscriberCareRequest(subscriberCareRequest);
    }
}
