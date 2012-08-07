package org.motechproject.ananya.kilkari.subscription.domain;

import java.util.Arrays;
import java.util.List;

public enum SubscriptionStatus {
    NEW, NEW_EARLY, PENDING_ACTIVATION, ACTIVATION_FAILED, COMPLETED, ACTIVE, DEACTIVATION_REQUEST_RECEIVED, PENDING_DEACTIVATION, DEACTIVATED, SUSPENDED, PENDING_COMPLETION;

    public boolean canRenew() {
        return this == ACTIVE || this == SUSPENDED;
    }

    public boolean canDeactivateOnRenewal() {
        return equals(SUSPENDED);
    }

    public boolean canActivate() {
        return equals(PENDING_ACTIVATION);
    }
    
    public boolean canChangePack(){
        return Arrays.asList(ACTIVE,SUSPENDED,NEW_EARLY).contains(this);
    }

    public boolean isActive() {
        return equals(ACTIVE);
    }

    public boolean isNewEarly() {
        return equals(NEW_EARLY);
    }

    boolean isInProgress() {
        return this != COMPLETED &&
                this != DEACTIVATED &&
                this != PENDING_DEACTIVATION &&
                this != PENDING_COMPLETION &&
                this != ACTIVATION_FAILED;
    }

    boolean hasCompletionRequestSent() {
        return PENDING_COMPLETION.equals(this);
    }

    public boolean isInDeactivatedState() {
        return getDeactivatedStates().contains(this);
    }

    public boolean hasBeenActivated() {
        return !(this == SubscriptionStatus.PENDING_ACTIVATION || this == SubscriptionStatus.ACTIVATION_FAILED);
    }

    private static List<SubscriptionStatus> getDeactivatedStates() {
        return Arrays.asList(PENDING_DEACTIVATION, DEACTIVATED, DEACTIVATION_REQUEST_RECEIVED);
    }
}
