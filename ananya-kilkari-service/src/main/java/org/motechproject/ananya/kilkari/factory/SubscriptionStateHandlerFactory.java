package org.motechproject.ananya.kilkari.factory;

import org.motechproject.ananya.kilkari.domain.ActionStatus;
import org.motechproject.ananya.kilkari.domain.CallbackAction;
import org.motechproject.ananya.kilkari.domain.CallbackStatus;
import org.motechproject.ananya.kilkari.handlers.callback.subscription.*;
import org.motechproject.ananya.kilkari.request.CallbackRequestWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SubscriptionStateHandlerFactory {
    private ActivateHandler activateHandler;
    private ActivationFailedHandler activationFailedHandler;
    private RenewalSuccessHandler renewalSuccessHandler;
    private RenewalSuspensionHandler renewalSuspensionHandler;
    private DeactivateHandler deactivateHandler;
    private Map<ActionStatus, SubscriptionStateHandler> handlerMappings;

    @Autowired
    public SubscriptionStateHandlerFactory(ActivateHandler activateHandler, ActivationFailedHandler activationFailedHandler, RenewalSuccessHandler renewalSuccessHandler, RenewalSuspensionHandler renewalSuspensionHandler, DeactivateHandler deactivateHandler) {
        this.activateHandler = activateHandler;
        this.activationFailedHandler = activationFailedHandler;
        this.renewalSuccessHandler = renewalSuccessHandler;
        this.renewalSuspensionHandler = renewalSuspensionHandler;
        this.deactivateHandler = deactivateHandler;
        initializeHandlerMap();
    }

    private void initializeHandlerMap() {
        handlerMappings = new HashMap<>();
        handlerMappings.put(new ActionStatus(CallbackAction.ACT, CallbackStatus.SUCCESS), activateHandler);
        handlerMappings.put(new ActionStatus(CallbackAction.ACT, CallbackStatus.BAL_LOW), activationFailedHandler);
        handlerMappings.put(new ActionStatus(CallbackAction.REN, CallbackStatus.SUCCESS), renewalSuccessHandler);
        handlerMappings.put(new ActionStatus(CallbackAction.REN, CallbackStatus.BAL_LOW), renewalSuspensionHandler);
        handlerMappings.put(new ActionStatus(CallbackAction.DCT, CallbackStatus.BAL_LOW), deactivateHandler);
    }

    public SubscriptionStateHandler getHandler(CallbackRequestWrapper callbackRequestWrapper) {
        ActionStatus actionStatus = ActionStatus.createFor(callbackRequestWrapper.getAction(), callbackRequestWrapper.getStatus());
        return handlerMappings.get(actionStatus);
    }
}