package org.motechproject.ananya.kilkari.service;

import org.motechproject.ananya.kilkari.subscription.service.request.SubscriberCareRequest;
import org.motechproject.ananya.kilkari.handlers.CallbackRequestHandler;
import org.motechproject.ananya.kilkari.request.CallbackRequestWrapper;
import org.motechproject.ananya.kilkari.request.ReferredByFlwRequest;
import org.motechproject.ananya.kilkari.request.SubscriptionWebRequest;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionEventKeys;
import org.motechproject.scheduler.context.EventContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionPublisher {
    private EventContext eventContext;
    private final static Logger logger = LoggerFactory.getLogger(SubscriptionPublisher.class);
    
    @Autowired
    public SubscriptionPublisher(@Qualifier("eventContext") EventContext eventContext) {
        this.eventContext = eventContext;
    }

    public void createSubscription(SubscriptionWebRequest subscriptionWebRequest) {
    	 eventContext.send(SubscriptionEventKeys.CREATE_SUBSCRIPTION, subscriptionWebRequest);
    }

    public void processReferredByFLWRequest(ReferredByFlwRequest referredByFlwMsisdnRequest) {
    	 eventContext.send(SubscriptionEventKeys.PROCESS_REFFERED_BY_SUBSCRIPTION, referredByFlwMsisdnRequest);
    }
    public void processReferredByRequest(SubscriptionWebRequest subscriptionWebRequest) {
    	  eventContext.send(SubscriptionEventKeys.CREATE_SUBSCRIPTION, subscriptionWebRequest);
    }

    public void processCallbackRequest(CallbackRequestWrapper callbackRequestWrapper) {
    	try{
    		eventContext.send(SubscriptionEventKeys.PROCESS_CALLBACK_REQUEST, callbackRequestWrapper);
    	}
    	catch(Throwable t){
    		logger.info("@@@ Exception/Error .... ");
    		logger.error("@@@ Exeption/Error", t);
    	}
    }

    public void processSubscriberCareRequest(SubscriberCareRequest subscriberCareRequest) {
    	 eventContext.send(SubscriptionEventKeys.PROCESS_SUBSCRIBER_CARE_REQUEST, subscriberCareRequest);
    }
}
