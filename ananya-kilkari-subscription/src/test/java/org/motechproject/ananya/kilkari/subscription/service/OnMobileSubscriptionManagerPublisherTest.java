package org.motechproject.ananya.kilkari.subscription.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.subscription.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.domain.ProcessSubscriptionRequest;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionEventKeys;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
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
        onMobileSubscriptionManagerPublisher.processActivation(new ProcessSubscriptionRequest("1234567890", SubscriptionPack.TWELVE_MONTHS, Channel.IVR, subscriptionId));

        ArgumentCaptor<ProcessSubscriptionRequest> subscriptionActivationRequestArgumentCaptor = ArgumentCaptor.forClass(ProcessSubscriptionRequest.class);
        ArgumentCaptor<String> eventArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(eventContext).send(eventArgumentCaptor.capture(), subscriptionActivationRequestArgumentCaptor.capture());
        ProcessSubscriptionRequest processSubscriptionRequest = subscriptionActivationRequestArgumentCaptor.getValue();
        String eventName = eventArgumentCaptor.getValue();

        assertEquals(SubscriptionEventKeys.ACTIVATE_SUBSCRIPTION, eventName);
        assertEquals("1234567890", processSubscriptionRequest.getMsisdn());
        assertEquals(SubscriptionPack.TWELVE_MONTHS, processSubscriptionRequest.getPack());
        assertEquals(Channel.IVR, processSubscriptionRequest.getChannel());
        assertEquals(subscriptionId, processSubscriptionRequest.getSubscriptionId());
    }

    @Test
    public void shouldPublishProcessSubscriptionDeactivationEventIntoQueue() {
        String subscriptionId = "ABCD1234";
        onMobileSubscriptionManagerPublisher.processDeactivation(new ProcessSubscriptionRequest("1234567890", SubscriptionPack.TWELVE_MONTHS, Channel.IVR, subscriptionId));

        ArgumentCaptor<ProcessSubscriptionRequest> processSubscriptionRequestArgumentCaptor = ArgumentCaptor.forClass(ProcessSubscriptionRequest.class);
        ArgumentCaptor<String> eventArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(eventContext).send(eventArgumentCaptor.capture(), processSubscriptionRequestArgumentCaptor.capture());
        ProcessSubscriptionRequest processSubscriptionRequest = processSubscriptionRequestArgumentCaptor.getValue();
        String eventName = eventArgumentCaptor.getValue();

        assertEquals(SubscriptionEventKeys.DEACTIVATE_SUBSCRIPTION, eventName);
        assertEquals("1234567890", processSubscriptionRequest.getMsisdn());
        assertEquals(SubscriptionPack.TWELVE_MONTHS, processSubscriptionRequest.getPack());
        assertEquals(Channel.IVR, processSubscriptionRequest.getChannel());
        assertEquals(subscriptionId, processSubscriptionRequest.getSubscriptionId());
    }
}

