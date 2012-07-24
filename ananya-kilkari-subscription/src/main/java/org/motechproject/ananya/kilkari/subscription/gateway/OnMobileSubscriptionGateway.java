package org.motechproject.ananya.kilkari.subscription.gateway;

import org.motechproject.ananya.kilkari.subscription.contract.OMSubscriptionRequest;

public interface OnMobileSubscriptionGateway {

    void activateSubscription(OMSubscriptionRequest OMSubscriptionRequest);

    void deactivateSubscription(OMSubscriptionRequest OMSubscriptionRequest);
}
