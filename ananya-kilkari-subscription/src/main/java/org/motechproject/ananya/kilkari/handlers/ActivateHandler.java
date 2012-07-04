package org.motechproject.ananya.kilkari.handlers;

import org.motechproject.ananya.kilkari.domain.CallbackRequestWrapper;

public class ActivateHandler extends SubscriptionStateHandler {
    @Override
    public void perform(CallbackRequestWrapper callbackRequestWrapper) {
        subscriptionService.activate(callbackRequestWrapper.getSubscriptionId(), callbackRequestWrapper.getCreatedAt(), callbackRequestWrapper.getOperator());
    }
}
