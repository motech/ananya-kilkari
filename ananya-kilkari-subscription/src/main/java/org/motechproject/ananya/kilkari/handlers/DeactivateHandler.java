package org.motechproject.ananya.kilkari.handlers;

import org.motechproject.ananya.kilkari.domain.CallbackRequestWrapper;

public class DeactivateHandler extends SubscriptionStateHandler {
    @Override
    public void perform(CallbackRequestWrapper callbackRequestWrapper) {
        subscriptionService.deactivateSubscription(callbackRequestWrapper.getSubscriptionId(), callbackRequestWrapper.getCreatedAt(),
                callbackRequestWrapper.getReason(), callbackRequestWrapper.getGraceCount());
    }
}
