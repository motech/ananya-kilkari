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
    private SubscriptionService subscriptionService;
    private static final String STATUS_FAILURE = "FAILURE";

    private final Logger LOGGER = LoggerFactory.getLogger(SubscriptionStateHandlerFactory.class);

    static HashMap<ActionStatus, Class> handlerMappings
            = new HashMap<ActionStatus, Class>() {{
        put(new ActionStatus("ACT", "SUCCESS"), ActivateHandler.class);
        put(new ActionStatus("ACT", "FAILURE"), ActivationFailedHandler.class);

        put(new ActionStatus("REN", "SUCCESS"), RenewalSuccessHandler.class);
        put(new ActionStatus("REN", "BAL_LOW"), RenewalSuspensionHandler.class);
        put(new ActionStatus("REN", "FAILURE"), RenewalFailedHandler.class);
        
        put(new ActionStatus("DCT", "BAL_LOW"), DeactivateHandler.class);
    }};

    @Autowired
    public SubscriptionStateHandlerFactory(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    public SubscriptionStateHandler getHandler(CallbackRequestWrapper callbackRequestWrapper) {
        SubscriptionStateHandler subscriptionStateHandler = null;
        Class handlerClass = getHandlerClass(callbackRequestWrapper);
        
        try {
            subscriptionStateHandler = (SubscriptionStateHandler) handlerClass.newInstance();
            subscriptionStateHandler.setSubscriptionService(subscriptionService);

        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.error(String.format("Unable to find the subscription state handler for action [%s] and status [%s]",
                    callbackRequestWrapper.getAction(), callbackRequestWrapper.getStatus()), e);
        }
        return subscriptionStateHandler;
    }

    private Class getHandlerClass(CallbackRequestWrapper callbackRequestWrapper) {
        ActionStatus actionStatus = ActionStatus.createFor(callbackRequestWrapper.getAction(), callbackRequestWrapper.getStatus());
        actionStatus = handlerMappings.keySet().contains(actionStatus) ? actionStatus : ActionStatus.createFor(callbackRequestWrapper.getAction(), STATUS_FAILURE);
        return handlerMappings.get(actionStatus);
    }
}