package org.motechproject.ananya.kilkari.handlers;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriberCareReasons;
import org.motechproject.ananya.kilkari.subscription.service.request.SubscriberCareRequest;
import org.motechproject.ananya.kilkari.service.KilkariSubscriberCareService;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionEventKeys;
import org.motechproject.event.MotechEvent;

import java.util.HashMap;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class SubscriberCareRequestHandlerTest {
    @Mock
    private KilkariSubscriberCareService kilkariSubscriberCareService;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldInvokeSubscriberCareServiceWithTheRequest() {
        String channel = "ivr";
        SubscriberCareRequest subscriberCareRequest = new SubscriberCareRequest("1234567890", SubscriberCareReasons.HELP.name(), channel, DateTime.now());
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("0", subscriberCareRequest);
        MotechEvent motechEvent = new MotechEvent(SubscriptionEventKeys.PROCESS_SUBSCRIBER_CARE_REQUEST, parameters);

        new SubscriberCareRequestHandler(kilkariSubscriberCareService).handleSubscriberCareRequest(motechEvent);

        verify(kilkariSubscriberCareService).createSubscriberCareRequest(subscriberCareRequest);
    }
}
