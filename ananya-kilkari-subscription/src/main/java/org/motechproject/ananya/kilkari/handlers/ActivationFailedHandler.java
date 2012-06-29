package org.motechproject.ananya.kilkari.handlers;

import org.motechproject.ananya.kilkari.domain.CallbackRequestWrapper;
import org.motechproject.ananya.kilkari.domain.SubscriptionStatus;

public class ActivationFailedHandler extends SubscriptionStateHandler {
    @Override
    public void perform(CallbackRequestWrapper callbackRequestWrapper) {
        subscriptionService.updateSubscriptionStatus(callbackRequestWrapper.getSubscriptionId(), SubscriptionStatus.ACTIVATION_FAILED, callbackRequestWrapper.getCreatedAt());
    }
}
