package org.motechproject.ananya.kilkari.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.builder.SubscriptionWebRequestBuilder;
import org.motechproject.ananya.kilkari.domain.SubscriberCareReasons;
import org.motechproject.ananya.kilkari.domain.SubscriberCareRequest;
import org.motechproject.ananya.kilkari.request.CallbackRequest;
import org.motechproject.ananya.kilkari.request.CallbackRequestWrapper;
import org.motechproject.ananya.kilkari.request.SubscriptionWebRequest;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionEventKeys;
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
        SubscriptionWebRequest subscriptionWebRequest = createSubscriptionRequest("1234567890", "twelve-months", "ivr");

        subscriptionPublisher.createSubscription(subscriptionWebRequest);

        verify(eventContext).send(SubscriptionEventKeys.CREATE_SUBSCRIPTION, subscriptionWebRequest);
    }

    @Test
    public void shouldPublishCallbackRequestIntoQueue() {
        CallbackRequestWrapper callbackRequestWrapper = new CallbackRequestWrapper(new CallbackRequest(), "abcd1234", DateTime.now());

        subscriptionPublisher.processCallbackRequest(callbackRequestWrapper);

        verify(eventContext).send(SubscriptionEventKeys.PROCESS_CALLBACK_REQUEST, callbackRequestWrapper);
    }

    @Test
    public void shouldPublishSubscriberCareRequestIntoQueue() {
        SubscriberCareRequest subscriberCareRequest = new SubscriberCareRequest("1234567890", SubscriberCareReasons.HELP.name(), channel, DateTime.now());

        subscriptionPublisher.processSubscriberCareRequest(subscriberCareRequest);

        verify(eventContext).send(SubscriptionEventKeys.PROCESS_SUBSCRIBER_CARE_REQUEST, subscriberCareRequest);
    }

    private SubscriptionWebRequest createSubscriptionRequest(String msisdn, String pack, String channel) {
        return new SubscriptionWebRequestBuilder().withDefaults().withMsisdn(msisdn).withPack(pack).withChannel(channel).build();
    }
}

