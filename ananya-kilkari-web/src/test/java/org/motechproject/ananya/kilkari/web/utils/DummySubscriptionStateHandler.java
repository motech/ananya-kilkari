package org.motechproject.ananya.kilkari.web.utils;

import org.motechproject.ananya.kilkari.handlers.callback.subscription.SubscriptionStateHandler;
import org.motechproject.ananya.kilkari.request.CallbackRequestWrapper;

public class DummySubscriptionStateHandler implements SubscriptionStateHandler {
    @Override
    public void perform(CallbackRequestWrapper callbackRequestWrapper) {
    }
}
