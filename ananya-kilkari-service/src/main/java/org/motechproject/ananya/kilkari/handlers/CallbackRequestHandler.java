package org.motechproject.ananya.kilkari.handlers;

import org.apache.log4j.Logger;
import org.motechproject.ananya.kilkari.domain.CallbackRequestWrapper;
import org.motechproject.ananya.kilkari.domain.SubscriptionEventKeys;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CallbackRequestHandler {

    private SubscriptionStateHandlerFactory subscriptionStateHandlerFactory;

    @Autowired
    public CallbackRequestHandler(SubscriptionStateHandlerFactory subscriptionStateHandlerFactory) {
        this.subscriptionStateHandlerFactory = subscriptionStateHandlerFactory;
    }

    @MotechListener(subjects = {SubscriptionEventKeys.PROCESS_CALLBACK_REQUEST})
    public void handleCallbackRequest(MotechEvent motechEvent) {
        CallbackRequestWrapper callbackRequestWrapper = (CallbackRequestWrapper) motechEvent.getParameters().get("0");
        subscriptionStateHandlerFactory.getHandler(callbackRequestWrapper).perform(callbackRequestWrapper);
    }
}
