package org.motechproject.ananya.kilkari.subscription.service;

import org.motechproject.ananya.kilkari.subscription.contract.OMSubscriptionRequest;
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

    public void sendActivationRequest(OMSubscriptionRequest OMSubscriptionRequest) {
        eventContext.send(SubscriptionEventKeys.ACTIVATE_SUBSCRIPTION, OMSubscriptionRequest);
    }

    public void processDeactivation(OMSubscriptionRequest OMSubscriptionRequest) {
        eventContext.send(SubscriptionEventKeys.DEACTIVATE_SUBSCRIPTION, OMSubscriptionRequest);
    }
}
