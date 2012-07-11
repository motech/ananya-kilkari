package org.motechproject.ananya.kilkari.gateway;

import org.motechproject.ananya.kilkari.domain.SubscriptionActivationRequest;

public interface OnMobileSubscriptionGateway {

    public static final String ACTIVATE_SUBSCRIPTION_PATH = "ActivateSubscription";

    void activateSubscription(SubscriptionActivationRequest subscriptionActivationRequest);
}
