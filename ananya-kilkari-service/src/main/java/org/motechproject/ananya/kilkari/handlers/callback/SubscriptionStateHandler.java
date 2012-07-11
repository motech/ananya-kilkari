package org.motechproject.ananya.kilkari.handlers.callback;

import org.motechproject.ananya.kilkari.request.CallbackRequestWrapper;
import org.motechproject.ananya.kilkari.service.SubscriptionService;

public abstract class SubscriptionStateHandler {
    protected SubscriptionService subscriptionService;

    public abstract void perform(CallbackRequestWrapper callbackRequestWrapper);

    public SubscriptionService getSubscriptionService() {
        return subscriptionService;
    }

    public void setSubscriptionService(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }
}
