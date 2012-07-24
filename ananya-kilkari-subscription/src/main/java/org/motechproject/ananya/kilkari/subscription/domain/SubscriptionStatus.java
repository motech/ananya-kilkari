package org.motechproject.ananya.kilkari.subscription.domain;

import java.util.Arrays;
import java.util.List;

public enum SubscriptionStatus {
    NEW, PENDING_ACTIVATION, ACTIVATION_FAILED, COMPLETED, ACTIVE, DEACTIVATION_REQUESTED, PENDING_DEACTIVATION, DEACTIVATED, SUSPENDED, PENDING_COMPLETION;

    public static List<SubscriptionStatus> getDeactivatedStates(){
        return Arrays.asList(PENDING_DEACTIVATION,DEACTIVATED,DEACTIVATION_REQUESTED);
    }
}
