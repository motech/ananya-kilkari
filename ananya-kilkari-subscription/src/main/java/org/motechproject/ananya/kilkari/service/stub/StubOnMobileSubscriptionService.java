package org.motechproject.ananya.kilkari.service.stub;

import org.motechproject.ananya.kilkari.domain.SubscriptionActivationRequest;
import org.motechproject.ananya.kilkari.profile.Test;
import org.motechproject.ananya.kilkari.service.IOnMobileSubscriptionService;
import org.springframework.stereotype.Service;

@Service
@Test
public class StubOnMobileSubscriptionService implements IOnMobileSubscriptionService {

    private IOnMobileSubscriptionService behavior;

    @Override
    public void activateSubscription(SubscriptionActivationRequest subscriptionActivationRequest) {
        if (verify()) {
            behavior.activateSubscription(subscriptionActivationRequest);
        }
    }

    public void setBehavior(IOnMobileSubscriptionService behavior) {
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
