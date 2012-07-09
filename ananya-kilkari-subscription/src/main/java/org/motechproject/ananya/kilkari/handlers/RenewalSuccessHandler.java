package org.motechproject.ananya.kilkari.handlers;

import org.motechproject.ananya.kilkari.domain.CallbackRequestWrapper;

public class RenewalSuccessHandler extends SubscriptionStateHandler {
    @Override
    public void perform(CallbackRequestWrapper callbackRequestWrapper) {
        subscriptionService.renewSubscription(callbackRequestWrapper.getSubscriptionId(), callbackRequestWrapper.getCreatedAt(), callbackRequestWrapper.getGraceCount());
    }
}
