package org.motechproject.ananya.kilkari.subscription.service;

import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionActivationRequest;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionEventKeys;
import org.motechproject.scheduler.context.EventContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class OnMobileSubscriptionManagerPublisher {
    @Autowired
    private EventContext eventContext;

    @Autowired
    public OnMobileSubscriptionManagerPublisher(@Qualifier("eventContext") EventContext eventContext) {
        this.eventContext = eventContext;
    }

    public void processSubscription(SubscriptionActivationRequest subscriptionActivationRequest) {
        eventContext.send(SubscriptionEventKeys.PROCESS_SUBSCRIPTION, subscriptionActivationRequest);
    }
}
