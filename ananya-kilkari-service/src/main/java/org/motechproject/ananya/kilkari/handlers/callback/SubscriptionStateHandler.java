package org.motechproject.ananya.kilkari.handlers.callback;

import org.motechproject.ananya.kilkari.request.CallbackRequestWrapper;

public interface SubscriptionStateHandler {
    public abstract void perform(CallbackRequestWrapper callbackRequestWrapper);
}
