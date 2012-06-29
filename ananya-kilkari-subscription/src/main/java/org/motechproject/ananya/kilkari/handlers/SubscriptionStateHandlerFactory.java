package org.motechproject.ananya.kilkari.handlers;

import org.motechproject.ananya.kilkari.domain.CallbackRequestWrapper;
import org.motechproject.ananya.kilkari.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class SubscriptionStateHandlerFactory {
    public static final String MAPPING_SEPARATOR = "|";
    private SubscriptionService subscriptionService;

    private HashMap<String, Class> handlerMappings;

    @Autowired
    public SubscriptionStateHandlerFactory(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
        populateHandlerMapping();
    }

    public SubscriptionStateHandler getHandler(CallbackRequestWrapper callbackRequestWrapper) {
        String actionAndStatus = callbackRequestWrapper.getAction() + MAPPING_SEPARATOR + callbackRequestWrapper.getStatus();
        SubscriptionStateHandler subscriptionStateHandler = null;
        try {
           subscriptionStateHandler = (SubscriptionStateHandler) handlerMappings.get(actionAndStatus).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        subscriptionStateHandler.setSubscriptionService(subscriptionService);
        return subscriptionStateHandler;
    }

    public HashMap<String, Class> getHandlerMappings() {
        return handlerMappings;
    }

    private void populateHandlerMapping() {
        handlerMappings = new HashMap<>();
        handlerMappings.put("ACT|SUCCESS", ActivateHandler.class);
        handlerMappings.put("ACT|FAILURE", ActivationFailedHandler.class);
    }
}
