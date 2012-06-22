package org.motechproject.ananya.kilkari.handlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.domain.SubscriptionEventKeys;
import org.motechproject.ananya.kilkari.domain.SubscriptionRequest;
import org.motechproject.ananya.kilkari.service.OnMobileSubscriptionService;
import org.motechproject.scheduler.domain.MotechEvent;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class ProcessSubscriptionHandlerTest {
    @Mock
    private OnMobileSubscriptionService onMobileSubscriptionService;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldInvokeOnMobileSubscriptionServiceToCreateAnActivationRequest() {
        final String msisdn = "msisdn";
        final String pack = "twelve_months";
        final String channel = "ivr";
        HashMap<String, Object> parameters = new HashMap<String, Object>(){{put("0", new SubscriptionRequest(msisdn, pack, channel));}};

        new ProcessSubscriptionHandler(onMobileSubscriptionService).handleProcessSubscription(new MotechEvent(SubscriptionEventKeys.PROCESS_SUBSCRIPTION, parameters));

        ArgumentCaptor<SubscriptionRequest> subscriptionRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionRequest.class);
        verify(onMobileSubscriptionService).activateSubscription(subscriptionRequestArgumentCaptor.capture());
        SubscriptionRequest subscriptionRequest = subscriptionRequestArgumentCaptor.getValue();

        assertEquals(msisdn, subscriptionRequest.getMsisdn());
        assertEquals(channel, subscriptionRequest.getChannel());
        assertEquals(pack, subscriptionRequest.getPack());
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionsRaisedByOnMobileSubscriptionServiceToCreateAnActivationRequest() {
        HashMap<String, Object> parameters = new HashMap<String, Object>(){{put("0", null);}};

        doThrow(new RuntimeException()).when(onMobileSubscriptionService).activateSubscription(any(SubscriptionRequest.class));

        new ProcessSubscriptionHandler(onMobileSubscriptionService).handleProcessSubscription(new MotechEvent(SubscriptionEventKeys.PROCESS_SUBSCRIPTION, parameters));
    }
}
