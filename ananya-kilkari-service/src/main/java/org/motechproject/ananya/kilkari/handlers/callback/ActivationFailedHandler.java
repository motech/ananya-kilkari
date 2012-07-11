package org.motechproject.ananya.kilkari.handlers.callback;

import org.motechproject.ananya.kilkari.request.CallbackRequestWrapper;

public class ActivationFailedHandler extends SubscriptionStateHandler {
    @Override
    public void perform(CallbackRequestWrapper callbackRequestWrapper) {
        subscriptionService.activationFailed(callbackRequestWrapper.getSubscriptionId(), callbackRequestWrapper.getCreatedAt(), callbackRequestWrapper.getReason(), callbackRequestWrapper.getOperator());
    }
}
