package org.motechproject.ananya.kilkari.handlers.callback;

import org.motechproject.ananya.kilkari.request.CallbackRequestWrapper;
import org.motechproject.ananya.kilkari.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ActivateHandler implements SubscriptionStateHandler {
    private SubscriptionService subscriptionService;

    @Autowired
    public ActivateHandler(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    public void perform(CallbackRequestWrapper callbackRequestWrapper) {
        subscriptionService.activate(callbackRequestWrapper.getSubscriptionId(), callbackRequestWrapper.getCreatedAt(), callbackRequestWrapper.getOperator());
    }
}
