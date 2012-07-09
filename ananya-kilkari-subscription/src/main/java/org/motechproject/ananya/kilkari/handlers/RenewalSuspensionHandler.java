package org.motechproject.ananya.kilkari.handlers;

import org.motechproject.ananya.kilkari.domain.CallbackRequestWrapper;

public class RenewalSuspensionHandler extends SubscriptionStateHandler {
    @Override
    public void perform(CallbackRequestWrapper callbackRequestWrapper) {
        subscriptionService.suspendSubscription(callbackRequestWrapper.getSubscriptionId(), callbackRequestWrapper.getCreatedAt(), callbackRequestWrapper.getReason(), callbackRequestWrapper.getGraceCount());
    }
}
