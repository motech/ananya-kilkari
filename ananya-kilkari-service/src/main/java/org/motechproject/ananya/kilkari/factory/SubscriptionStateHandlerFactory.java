package org.motechproject.ananya.kilkari.factory;

import org.motechproject.ananya.kilkari.domain.ActionStatus;
import org.motechproject.ananya.kilkari.domain.CallbackAction;
import org.motechproject.ananya.kilkari.domain.CallbackStatus;
import org.motechproject.ananya.kilkari.handlers.callback.*;
import org.motechproject.ananya.kilkari.request.CallbackRequestWrapper;
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

    static HashMap<ActionStatus, Class> handlerMappings = new HashMap<ActionStatus, Class>() {{
        put(new ActionStatus(CallbackAction.ACT, CallbackStatus.SUCCESS), ActivateHandler.class);
        put(new ActionStatus(CallbackAction.ACT, CallbackStatus.BAL_LOW), ActivationFailedHandler.class);

        put(new ActionStatus(CallbackAction.REN, CallbackStatus.SUCCESS), RenewalSuccessHandler.class);
        put(new ActionStatus(CallbackAction.REN, CallbackStatus.BAL_LOW), RenewalSuspensionHandler.class);

        put(new ActionStatus(CallbackAction.DCT, CallbackStatus.BAL_LOW), DeactivateHandler.class);
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

    public static Class getHandlerClass(CallbackRequestWrapper callbackRequestWrapper) {
        ActionStatus actionStatus = ActionStatus.createFor(callbackRequestWrapper.getAction(), callbackRequestWrapper.getStatus());
        return handlerMappings.get(actionStatus);
    }
}