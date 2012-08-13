package org.motechproject.ananya.kilkari.subscription.domain;

import java.util.Arrays;
import java.util.List;

public enum SubscriptionStatus {
    NEW("New"), NEW_EARLY("Early Subscription"), PENDING_ACTIVATION("Pending Subscription"), ACTIVATION_FAILED("Activation Failed"),
    COMPLETED("Completed"), ACTIVE("Activated"), DEACTIVATION_REQUEST_RECEIVED("Deactivation Requested"), PENDING_DEACTIVATION("Pending Deactivation"),
    DEACTIVATED("Deactivated"), SUSPENDED("Suspended"), PENDING_COMPLETION("Pending Completion");

    private String displayString;

    SubscriptionStatus(String displayString) {
        this.displayString = displayString;
    }

    public String getDisplayString() {
        return displayString;
    }

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

    public boolean isSuspended() {
        return equals(SUSPENDED);
    }

    boolean isInProgress() {
        return this != COMPLETED &&
                this != DEACTIVATED &&
                this != PENDING_DEACTIVATION &&
                this != PENDING_COMPLETION &&
                this != ACTIVATION_FAILED &&
                this != DEACTIVATION_REQUEST_RECEIVED;
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
