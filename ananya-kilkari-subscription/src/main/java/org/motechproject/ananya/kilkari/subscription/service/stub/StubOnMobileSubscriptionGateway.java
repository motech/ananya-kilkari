package org.motechproject.ananya.kilkari.subscription.service.stub;

import org.motechproject.ananya.kilkari.reporting.profile.TestProfile;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionActivationRequest;
import org.motechproject.ananya.kilkari.subscription.gateway.OnMobileSubscriptionGateway;
import org.springframework.stereotype.Service;

@Service
@TestProfile
public class StubOnMobileSubscriptionGateway implements OnMobileSubscriptionGateway {

    private OnMobileSubscriptionGateway behavior;

    @Override
    public void activateSubscription(SubscriptionActivationRequest subscriptionActivationRequest) {
        if (verify()) {
            behavior.activateSubscription(subscriptionActivationRequest);
        }
    }

    public void setBehavior(OnMobileSubscriptionGateway behavior) {
        this.behavior = behavior;
    }

    private boolean verify() {
        if (behavior == null) {
            System.err.println("WARNING: You need to set behavior before calling this method. Use setBehavior method.");
            return false;
        }
        return true;
    }
}
