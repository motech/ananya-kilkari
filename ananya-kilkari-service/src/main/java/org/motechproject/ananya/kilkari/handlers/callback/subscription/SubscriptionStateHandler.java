package org.motechproject.ananya.kilkari.handlers.callback.subscription;

import org.motechproject.ananya.kilkari.request.CallbackRequestWrapper;

public interface SubscriptionStateHandler {
    public void perform(CallbackRequestWrapper callbackRequestWrapper);
}
