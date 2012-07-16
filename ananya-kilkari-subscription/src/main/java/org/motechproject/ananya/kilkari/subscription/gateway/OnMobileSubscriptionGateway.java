package org.motechproject.ananya.kilkari.subscription.gateway;

import org.motechproject.ananya.kilkari.subscription.domain.ProcessSubscriptionRequest;

public interface OnMobileSubscriptionGateway {

    public static final String ACTIVATE_SUBSCRIPTION_PATH = "ActivateSubscription";

    void activateSubscription(ProcessSubscriptionRequest processSubscriptionRequest);

    void deactivateSubscription(ProcessSubscriptionRequest processSubscriptionRequest);
}
