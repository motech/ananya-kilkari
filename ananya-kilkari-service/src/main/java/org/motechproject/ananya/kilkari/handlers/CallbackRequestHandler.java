package org.motechproject.ananya.kilkari.handlers;

import org.motechproject.ananya.kilkari.factory.SubscriptionStateHandlerFactory;
import org.motechproject.ananya.kilkari.request.CallbackRequestWrapper;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionEventKeys;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.annotations.MotechListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CallbackRequestHandler {

    private SubscriptionStateHandlerFactory subscriptionStateHandlerFactory;
    private final static Logger logger = LoggerFactory.getLogger(CallbackRequestHandler.class);

    @Autowired
    public CallbackRequestHandler(SubscriptionStateHandlerFactory subscriptionStateHandlerFactory) {
        this.subscriptionStateHandlerFactory = subscriptionStateHandlerFactory;
    }

    @MotechListener(subjects = {SubscriptionEventKeys.PROCESS_CALLBACK_REQUEST})
    public void handleCallbackRequest(MotechEvent motechEvent) {
    	logger.info("@@1 PROCESS_CALLBACK_REQUEST: processing call back request.. in CallbackRequestHandler class");
        CallbackRequestWrapper callbackRequestWrapper = (CallbackRequestWrapper) motechEvent.getParameters().get("0");
        if(callbackRequestWrapper.isRequestedByMotech())
        	subscriptionStateHandlerFactory.getHandler(callbackRequestWrapper).perform(callbackRequestWrapper);
        else
        	subscriptionStateHandlerFactory.getHandler(callbackRequestWrapper).performForSMReq(callbackRequestWrapper);
    }
}
