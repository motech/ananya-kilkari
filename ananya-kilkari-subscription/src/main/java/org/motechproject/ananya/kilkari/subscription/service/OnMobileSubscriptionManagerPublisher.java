package org.motechproject.ananya.kilkari.subscription.service;

import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionEventKeys;
import org.motechproject.ananya.kilkari.subscription.handlers.SubscriptionHandler;
import org.motechproject.ananya.kilkari.subscription.request.OMSubscriptionRequest;
import org.motechproject.scheduler.context.EventContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class OnMobileSubscriptionManagerPublisher {
    @Autowired
    private EventContext eventContext;

    private final static Logger logger = LoggerFactory.getLogger(OnMobileSubscriptionManagerPublisher.class);
    
    @Autowired
    public OnMobileSubscriptionManagerPublisher(@Qualifier("eventContext") EventContext eventContext) {
        this.eventContext = eventContext;
    }

    public void sendActivationRequest(OMSubscriptionRequest OMSubscriptionRequest) {
    	logger.info("sending motech event with key ACTIVATE_SUBSCRIPTION");
        eventContext.send(SubscriptionEventKeys.ACTIVATE_SUBSCRIPTION, OMSubscriptionRequest);
    }

    public void processDeactivation(OMSubscriptionRequest OMSubscriptionRequest) {
    	logger.info("sending motech event with key DEACTIVATION_REQUESTED_SUBSCRIPTION");
        eventContext.send(SubscriptionEventKeys.DEACTIVATION_REQUESTED_SUBSCRIPTION, OMSubscriptionRequest);
    }
}
