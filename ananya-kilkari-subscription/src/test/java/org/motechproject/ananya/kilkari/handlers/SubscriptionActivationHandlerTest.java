package org.motechproject.ananya.kilkari.handlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.domain.*;
import org.motechproject.ananya.kilkari.service.OnMobileSubscriptionService;
import org.motechproject.ananya.kilkari.service.SubscriptionService;
import org.motechproject.scheduler.domain.MotechEvent;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class SubscriptionActivationHandlerTest {
    @Mock
    private OnMobileSubscriptionService onMobileSubscriptionService;
    @Mock
    private SubscriptionService subscriptionService;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldInvokeReportingServiceToCreateASubscriptionRequest() {
        final String msisdn = "msisdn";
        final SubscriptionPack pack = SubscriptionPack.TWELVE_MONTHS;
        final Channel channel = Channel.IVR;
        final String subscriptionId = "abcd1234";
        HashMap<String, Object> parameters = new HashMap<String, Object>(){{put("0", new SubscriptionActivationRequest(msisdn, pack, channel, subscriptionId));}};

        new SubscriptionActivationHandler(onMobileSubscriptionService, subscriptionService).handleProcessSubscription(new MotechEvent(SubscriptionEventKeys.PROCESS_SUBSCRIPTION, parameters));

        ArgumentCaptor<SubscriptionActivationRequest> subscriptionActivationRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionActivationRequest.class);
        verify(onMobileSubscriptionService).activateSubscription(subscriptionActivationRequestArgumentCaptor.capture());
        SubscriptionActivationRequest subscriptionActivationRequest = subscriptionActivationRequestArgumentCaptor.getValue();

        verify(subscriptionService).updateSubscriptionStatus(msisdn, pack.name(), SubscriptionStatus.PENDING_ACTIVATION);

        assertEquals(msisdn, subscriptionActivationRequest.getMsisdn());
        assertEquals(channel, subscriptionActivationRequest.getChannel());
        assertEquals(pack, subscriptionActivationRequest.getPack());
        assertEquals(subscriptionId, subscriptionActivationRequest.getSubscriptionId());
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionsRaisedByOnMobileSubscriptionServiceToCreateAnActivationRequest() {
        HashMap<String, Object> parameters = new HashMap<String, Object>(){{put("0", null);}};

        doThrow(new RuntimeException()).when(onMobileSubscriptionService).activateSubscription(any(SubscriptionActivationRequest.class));

        new SubscriptionActivationHandler(onMobileSubscriptionService, subscriptionService).handleProcessSubscription(new MotechEvent(SubscriptionEventKeys.PROCESS_SUBSCRIPTION, parameters));
    }
}
