package org.motechproject.ananya.kilkari.service.stub;

import org.motechproject.ananya.kilkari.domain.SubscriptionActivationRequest;
import org.motechproject.ananya.kilkari.profile.TestProfile;
import org.motechproject.ananya.kilkari.service.OnMobileSubscriptionService;
import org.springframework.stereotype.Service;

@Service
@TestProfile
public class StubOnMobileSubscriptionService implements OnMobileSubscriptionService {

    private OnMobileSubscriptionService behavior;

    @Override
    public void activateSubscription(SubscriptionActivationRequest subscriptionActivationRequest) {
        if (verify()) {
            behavior.activateSubscription(subscriptionActivationRequest);
        }
    }

    public void setBehavior(OnMobileSubscriptionService behavior) {
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
