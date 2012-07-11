package org.motechproject.ananya.kilkari.handlers.callback;

import org.motechproject.ananya.kilkari.request.CallbackRequestWrapper;
import org.motechproject.ananya.kilkari.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RenewalSuccessHandler implements SubscriptionStateHandler {
    private SubscriptionService subscriptionService;

    @Autowired
    public RenewalSuccessHandler(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @Override
    public void perform(CallbackRequestWrapper callbackRequestWrapper) {
        subscriptionService.renewSubscription(callbackRequestWrapper.getSubscriptionId(), callbackRequestWrapper.getCreatedAt(), callbackRequestWrapper.getGraceCount());
    }
}
