package org.motechproject.ananya.kilkari.subscription.gateway;

import org.motechproject.ananya.kilkari.subscription.request.OMSubscriptionRequest;

public interface OnMobileSubscriptionGateway {

    void activateSubscription(OMSubscriptionRequest OMSubscriptionRequest);

    void deactivateSubscription(OMSubscriptionRequest OMSubscriptionRequest);
}
