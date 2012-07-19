package org.motechproject.ananya.kilkari.subscription.service.stub;

import org.motechproject.ananya.kilkari.reporting.profile.TestProfile;
import org.motechproject.ananya.kilkari.subscription.domain.ProcessSubscriptionRequest;
import org.motechproject.ananya.kilkari.subscription.gateway.OnMobileSubscriptionGateway;
import org.springframework.stereotype.Service;

@Service
@TestProfile
public class StubOnMobileSubscriptionGateway implements OnMobileSubscriptionGateway {

    private OnMobileSubscriptionGateway behavior;

    @Override
    public void activateSubscription(ProcessSubscriptionRequest processSubscriptionRequest) {
        if (verify()) {
            behavior.activateSubscription(processSubscriptionRequest);
        }
    }

    @Override
    public void deactivateSubscription(ProcessSubscriptionRequest processSubscriptionRequest) {
        if (verify()) {
            behavior.deactivateSubscription(processSubscriptionRequest);
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
}
