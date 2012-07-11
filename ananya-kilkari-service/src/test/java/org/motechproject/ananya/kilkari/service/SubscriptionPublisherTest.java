package org.motechproject.ananya.kilkari.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.builder.SubscriptionRequestBuilder;
import org.motechproject.ananya.kilkari.domain.SubscriberCareReasons;
import org.motechproject.ananya.kilkari.domain.SubscriberCareRequest;
import org.motechproject.ananya.kilkari.domain.SubscriptionEventKeys;
import org.motechproject.ananya.kilkari.domain.SubscriptionRequest;
import org.motechproject.ananya.kilkari.request.CallbackRequest;
import org.motechproject.ananya.kilkari.request.CallbackRequestWrapper;
import org.motechproject.scheduler.context.EventContext;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class SubscriptionPublisherTest {
    @Mock
    private EventContext eventContext;

    private SubscriptionPublisher subscriptionPublisher;
    private String channel;

    @Before
    public void setUp(){
        initMocks(this);
        channel = "ivr";
        subscriptionPublisher = new SubscriptionPublisher(eventContext);
    }

    @Test
    public void shouldPublishSubscriptionCreationDataIntoQueue() {
        SubscriptionRequest subscriptionRequest = createSubscriptionRequest("1234567890", "twelve-months", "ivr");

        subscriptionPublisher.createSubscription(subscriptionRequest);

        verify(eventContext).send(SubscriptionEventKeys.CREATE_SUBSCRIPTION, subscriptionRequest);
    }

    @Test
    public void shouldPublishCallbackRequestIntoQueue() {
        CallbackRequestWrapper callbackRequestWrapper = new CallbackRequestWrapper(new CallbackRequest(), "abcd1234", DateTime.now());

        subscriptionPublisher.processCallbackRequest(callbackRequestWrapper);

        verify(eventContext).send(SubscriptionEventKeys.PROCESS_CALLBACK_REQUEST, callbackRequestWrapper);
    }

    @Test
    public void shouldPublishSubscriberCareRequestIntoQueue() {
        SubscriberCareRequest subscriberCareRequest = new SubscriberCareRequest("1234567890", SubscriberCareReasons.HELP.name(), channel);

        subscriptionPublisher.processSubscriberCareRequest(subscriberCareRequest);

        verify(eventContext).send(SubscriptionEventKeys.PROCESS_SUBSCRIBER_CARE_REQUEST, subscriberCareRequest);
    }

    private SubscriptionRequest createSubscriptionRequest(String msisdn, String pack, String channel) {
        return new SubscriptionRequestBuilder().withDefaults().withMsisdn(msisdn).withPack(pack).withChannel(channel).build();
    }
}

