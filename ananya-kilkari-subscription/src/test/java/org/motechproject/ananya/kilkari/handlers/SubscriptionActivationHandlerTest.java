package org.motechproject.ananya.kilkari.handlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.domain.Channel;
import org.motechproject.ananya.kilkari.domain.SubscriptionActivationRequest;
import org.motechproject.ananya.kilkari.domain.SubscriptionEventKeys;
import org.motechproject.ananya.kilkari.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.service.IOnMobileSubscriptionService;
import org.motechproject.ananya.kilkari.service.SubscriptionService;
import org.motechproject.scheduler.domain.MotechEvent;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class SubscriptionActivationHandlerTest {
    @Mock
    private IOnMobileSubscriptionService onMobileSubscriptionService;
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

        assertEquals(msisdn, subscriptionActivationRequest.getMsisdn());
        assertEquals(channel, subscriptionActivationRequest.getChannel());
        assertEquals(pack, subscriptionActivationRequest.getPack());
        assertEquals(subscriptionId, subscriptionActivationRequest.getSubscriptionId());

        verify(subscriptionService).activationRequested(subscriptionId);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionsRaisedByOnMobileSubscriptionServiceToCreateAnActivationRequest() {
        HashMap<String, Object> parameters = new HashMap<String, Object>(){{put("0", null);}};

        doThrow(new RuntimeException()).when(onMobileSubscriptionService).activateSubscription(any(SubscriptionActivationRequest.class));

        new SubscriptionActivationHandler(onMobileSubscriptionService, subscriptionService).handleProcessSubscription(new MotechEvent(SubscriptionEventKeys.PROCESS_SUBSCRIPTION, parameters));
    }
}
