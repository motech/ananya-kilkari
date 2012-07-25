package org.motechproject.ananya.kilkari.subscription.service.stub;

import org.motechproject.ananya.kilkari.reporting.profile.TestProfile;
import org.motechproject.ananya.kilkari.subscription.request.OMSubscriptionRequest;
import org.motechproject.ananya.kilkari.subscription.gateway.OnMobileSubscriptionGateway;
import org.springframework.stereotype.Service;

@Service
@TestProfile
public class StubOnMobileSubscriptionGateway implements OnMobileSubscriptionGateway {

    private OnMobileSubscriptionGateway behavior;
    private boolean isDeactivateSubscriptionCalled;

    @Override
    public void activateSubscription(OMSubscriptionRequest OMSubscriptionRequest) {
        if (verify()) {
            behavior.activateSubscription(OMSubscriptionRequest);
        }
    }

    @Override
    public void deactivateSubscription(OMSubscriptionRequest OMSubscriptionRequest) {
        if (verify()) {
            behavior.deactivateSubscription(OMSubscriptionRequest);
            isDeactivateSubscriptionCalled = true;
        }
    }

    public void setBehavior(OnMobileSubscriptionGateway behavior) {
        this.behavior = behavior;
    }

    private boolean verify() {
        if (behavior == null) {
            System.err.println(String.format("WARNING: %s: You need to set behavior before calling this method. Use setBehavior method.", StubOnMobileSubscriptionGateway.class.getCanonicalName()));
            return false;
        }
        return true;
    }

    public boolean isDeactivateSubscriptionCalled() {
        return isDeactivateSubscriptionCalled;
    }
}
