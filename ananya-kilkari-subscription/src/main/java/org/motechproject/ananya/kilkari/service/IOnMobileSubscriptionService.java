package org.motechproject.ananya.kilkari.service;

import org.motechproject.ananya.kilkari.domain.SubscriptionActivationRequest;

public interface IOnMobileSubscriptionService {

    public static final String ACTIVATE_SUBSCRIPTION_PATH = "ActivateSubscription";

    void activateSubscription(SubscriptionActivationRequest subscriptionActivationRequest);
}
