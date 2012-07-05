package org.motechproject.ananya.kilkari.handlers;

import org.motechproject.ananya.kilkari.domain.CallbackRequestWrapper;
import org.motechproject.ananya.kilkari.service.SubscriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class SubscriptionStateHandlerFactory {
    public static final String SUCCESS = "SUCCESS";
    public static final String FAILURE = "FAILURE";
    private SubscriptionService subscriptionService;

    private final Logger LOGGER = LoggerFactory.getLogger(SubscriptionStateHandlerFactory.class);

    static HashMap<ActionStatus, Class> handlerMappings
            = new HashMap<ActionStatus, Class>() {{
        put(new ActionStatus("ACT", "SUCCESS"), ActivateHandler.class);
        put(new ActionStatus("ACT", "FAILURE"), ActivationFailedHandler.class);
    }};

    @Autowired
    public SubscriptionStateHandlerFactory(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    public SubscriptionStateHandler getHandler(CallbackRequestWrapper callbackRequestWrapper) {
        String status = callbackRequestWrapper.getStatus().equals(SUCCESS) ? callbackRequestWrapper.getStatus() : FAILURE;
        SubscriptionStateHandler subscriptionStateHandler = null;
        try {
            Class handlerClass = handlerMappings.get(new ActionStatus(callbackRequestWrapper.getAction(), status));
            subscriptionStateHandler = (SubscriptionStateHandler) handlerClass.newInstance();
            subscriptionStateHandler.setSubscriptionService(subscriptionService);

        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.error(String.format("Unable to find the subscription state handler for action [%s] and status [%s]",
                    callbackRequestWrapper.getAction(), callbackRequestWrapper.getStatus()), e);
        }
        return subscriptionStateHandler;
    }
}