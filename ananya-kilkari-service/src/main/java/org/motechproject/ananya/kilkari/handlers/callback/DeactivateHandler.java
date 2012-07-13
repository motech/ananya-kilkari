package org.motechproject.ananya.kilkari.handlers.callback;

import org.motechproject.ananya.kilkari.request.CallbackRequestWrapper;
import org.motechproject.ananya.kilkari.subscription.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeactivateHandler implements SubscriptionStateHandler{
    private SubscriptionService subscriptionService;

    @Autowired
    public DeactivateHandler(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    public void perform(CallbackRequestWrapper callbackRequestWrapper) {
        subscriptionService.deactivateSubscription(callbackRequestWrapper.getSubscriptionId(), callbackRequestWrapper.getCreatedAt(),
                callbackRequestWrapper.getReason(), callbackRequestWrapper.getGraceCount());
    }
}
