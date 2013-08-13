package org.motechproject.ananya.kilkari.service;

import org.motechproject.ananya.kilkari.subscription.service.request.SubscriberCareRequest;
import org.motechproject.ananya.kilkari.request.CallbackRequestWrapper;
import org.motechproject.ananya.kilkari.request.ReferredByFlwMsisdnRequest;
import org.motechproject.ananya.kilkari.request.SubscriptionWebRequest;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionEventKeys;
import org.motechproject.scheduler.context.EventContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionPublisher {
    private EventContext eventContext;

    @Autowired
    public SubscriptionPublisher(@Qualifier("eventContext") EventContext eventContext) {
        this.eventContext = eventContext;
    }

    public void createSubscription(SubscriptionWebRequest subscriptionWebRequest) {
        eventContext.send(SubscriptionEventKeys.CREATE_SUBSCRIPTION, subscriptionWebRequest);
    }

    public void processReferredByFLWRequest(ReferredByFlwMsisdnRequest referredByFlwMsisdnRequest) {
        eventContext.send(SubscriptionEventKeys.PROCESS_REFFERED_BY_SUBSCRIPTION, referredByFlwMsisdnRequest);
    }
    public void processReferredByRequest(SubscriptionWebRequest subscriptionWebRequest) {
        eventContext.send(SubscriptionEventKeys.CREATE_SUBSCRIPTION, subscriptionWebRequest);
    }

    public void processCallbackRequest(CallbackRequestWrapper callbackRequestWrapper) {
        eventContext.send(SubscriptionEventKeys.PROCESS_CALLBACK_REQUEST, callbackRequestWrapper);
    }

    public void processSubscriberCareRequest(SubscriberCareRequest subscriberCareRequest) {
        eventContext.send(SubscriptionEventKeys.PROCESS_SUBSCRIBER_CARE_REQUEST, subscriberCareRequest);
    }
}
