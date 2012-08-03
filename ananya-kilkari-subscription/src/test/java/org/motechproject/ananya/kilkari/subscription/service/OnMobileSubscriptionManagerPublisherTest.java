package org.motechproject.ananya.kilkari.subscription.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.subscription.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionEventKeys;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.request.OMSubscriptionRequest;
import org.motechproject.scheduler.context.EventContext;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class OnMobileSubscriptionManagerPublisherTest {
    @Mock
    private EventContext eventContext;

    private OnMobileSubscriptionManagerPublisher onMobileSubscriptionManagerPublisher;

    @Before
    public void setUp() {
        initMocks(this);
        onMobileSubscriptionManagerPublisher = new OnMobileSubscriptionManagerPublisher(eventContext);
    }

    @Test
    public void shouldPublishProcessSubscriptionActivationEventIntoQueue() {
        String subscriptionId = "ABCD1234";
        onMobileSubscriptionManagerPublisher.sendActivationRequest(new OMSubscriptionRequest("1234567890", SubscriptionPack.TWELVE_MONTHS, Channel.IVR, subscriptionId));

        ArgumentCaptor<OMSubscriptionRequest> subscriptionActivationRequestArgumentCaptor = ArgumentCaptor.forClass(OMSubscriptionRequest.class);
        ArgumentCaptor<String> eventArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(eventContext).send(eventArgumentCaptor.capture(), subscriptionActivationRequestArgumentCaptor.capture());
        OMSubscriptionRequest OMSubscriptionRequest = subscriptionActivationRequestArgumentCaptor.getValue();
        String eventName = eventArgumentCaptor.getValue();

        assertEquals(SubscriptionEventKeys.ACTIVATE_SUBSCRIPTION, eventName);
        assertEquals("1234567890", OMSubscriptionRequest.getMsisdn());
        assertEquals(SubscriptionPack.TWELVE_MONTHS, OMSubscriptionRequest.getPack());
        assertEquals(Channel.IVR, OMSubscriptionRequest.getChannel());
        assertEquals(subscriptionId, OMSubscriptionRequest.getSubscriptionId());
    }

    @Test
    public void shouldPublishProcessSubscriptionDeactivationEventIntoQueue() {
        String subscriptionId = "ABCD1234";
        onMobileSubscriptionManagerPublisher.processDeactivation(new OMSubscriptionRequest("1234567890", SubscriptionPack.TWELVE_MONTHS, Channel.IVR, subscriptionId));

        ArgumentCaptor<OMSubscriptionRequest> processSubscriptionRequestArgumentCaptor = ArgumentCaptor.forClass(OMSubscriptionRequest.class);
        ArgumentCaptor<String> eventArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(eventContext).send(eventArgumentCaptor.capture(), processSubscriptionRequestArgumentCaptor.capture());
        OMSubscriptionRequest OMSubscriptionRequest = processSubscriptionRequestArgumentCaptor.getValue();
        String eventName = eventArgumentCaptor.getValue();

        assertEquals(SubscriptionEventKeys.DEACTIVATE_SUBSCRIPTION, eventName);
        assertEquals("1234567890", OMSubscriptionRequest.getMsisdn());
        assertEquals(SubscriptionPack.TWELVE_MONTHS, OMSubscriptionRequest.getPack());
        assertEquals(Channel.IVR, OMSubscriptionRequest.getChannel());
        assertEquals(subscriptionId, OMSubscriptionRequest.getSubscriptionId());
    }
}

