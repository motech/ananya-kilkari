package org.motechproject.ananya.kilkari.subscription.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum SubscriptionStatus {
    NEW("New") {
        @Override
        public boolean canTransitionTo(SubscriptionStatus toStatus) {
            List<SubscriptionStatus> validStates = new ArrayList<SubscriptionStatus>() {{
                add(SubscriptionStatus.NEW);
                add(SubscriptionStatus.PENDING_ACTIVATION);
                add(SubscriptionStatus.DEACTIVATION_REQUEST_RECEIVED);
            }};
            return validStates.contains(toStatus);
        }
    },
    NEW_EARLY("Early Subscription") {
        @Override
        public boolean canTransitionTo(SubscriptionStatus toStatus) {
            List<SubscriptionStatus> validStates = new ArrayList<SubscriptionStatus>() {{
                add(SubscriptionStatus.NEW_EARLY);
                add(SubscriptionStatus.PENDING_ACTIVATION);
                add(SubscriptionStatus.DEACTIVATED);
            }};
            return validStates.contains(toStatus);
        }
    },
    PENDING_ACTIVATION("Pending Subscription") {
        @Override
        public boolean canTransitionTo(SubscriptionStatus toStatus) {
            List<SubscriptionStatus> validStates = new ArrayList<SubscriptionStatus>() {{
                add(SubscriptionStatus.PENDING_ACTIVATION);
                add(SubscriptionStatus.ACTIVATION_FAILED);
                add(SubscriptionStatus.ACTIVE);
                add(SubscriptionStatus.DEACTIVATION_REQUEST_RECEIVED);
            }};
            return validStates.contains(toStatus);
        }
    },
    ACTIVATION_FAILED("Activation Failed") {
        @Override
        public boolean canTransitionTo(SubscriptionStatus toStatus) {
            List<SubscriptionStatus> validStates = new ArrayList<SubscriptionStatus>() {{
                add(SubscriptionStatus.ACTIVATION_FAILED);
            }};
            return validStates.contains(toStatus);
        }
    },
    COMPLETED("Completed") {
        @Override
        public boolean canTransitionTo(SubscriptionStatus toStatus) {
            List<SubscriptionStatus> validStates = new ArrayList<SubscriptionStatus>() {{
                add(SubscriptionStatus.COMPLETED);
            }};
            return validStates.contains(toStatus);
        }
    },
    ACTIVE("Activated") {
        @Override
        public boolean canTransitionTo(SubscriptionStatus toStatus) {
            List<SubscriptionStatus> validStates = new ArrayList<SubscriptionStatus>() {{
                add(SubscriptionStatus.ACTIVE);
                add(SubscriptionStatus.SUSPENDED);
                add(SubscriptionStatus.PENDING_COMPLETION);
                add(SubscriptionStatus.DEACTIVATION_REQUEST_RECEIVED);
                add(SubscriptionStatus.DEACTIVATED);
            }};
            return validStates.contains(toStatus);
        }
    },
    DEACTIVATION_REQUEST_RECEIVED("Deactivation Requested") {
        @Override
        public boolean canTransitionTo(SubscriptionStatus toStatus) {
            List<SubscriptionStatus> validStates = new ArrayList<SubscriptionStatus>() {{
                add(SubscriptionStatus.DEACTIVATION_REQUEST_RECEIVED);
                add(SubscriptionStatus.PENDING_COMPLETION);
                add(SubscriptionStatus.PENDING_DEACTIVATION);
                add(SubscriptionStatus.DEACTIVATED);
            }};
            return validStates.contains(toStatus);
        }
    },
    PENDING_DEACTIVATION("Pending Deactivation") {
        @Override
        public boolean canTransitionTo(SubscriptionStatus toStatus) {
            List<SubscriptionStatus> validStates = new ArrayList<SubscriptionStatus>() {{
                add(SubscriptionStatus.PENDING_DEACTIVATION);
                add(SubscriptionStatus.DEACTIVATED);
            }};
            return validStates.contains(toStatus);
        }
    },
    DEACTIVATED("Deactivated") {
        @Override
        public boolean canTransitionTo(SubscriptionStatus toStatus) {
            List<SubscriptionStatus> validStates = new ArrayList<SubscriptionStatus>() {{
                add(SubscriptionStatus.DEACTIVATED);
            }};
            return validStates.contains(toStatus);
        }
    },
    SUSPENDED("Suspended") {
        @Override
        public boolean canTransitionTo(SubscriptionStatus toStatus) {
            List<SubscriptionStatus> validStates = new ArrayList<SubscriptionStatus>() {{
                add(SubscriptionStatus.SUSPENDED);
                add(SubscriptionStatus.ACTIVE);
                add(SubscriptionStatus.PENDING_COMPLETION);
                add(SubscriptionStatus.DEACTIVATION_REQUEST_RECEIVED);
                add(SubscriptionStatus.DEACTIVATED);
            }};
            return validStates.contains(toStatus);
        }
    },
    PENDING_COMPLETION("Pending Completion") {
        @Override
        public boolean canTransitionTo(SubscriptionStatus toStatus) {
            List<SubscriptionStatus> validStates = new ArrayList<SubscriptionStatus>() {{
                add(SubscriptionStatus.PENDING_COMPLETION);
                add(SubscriptionStatus.COMPLETED);
                add(SubscriptionStatus.DEACTIVATED);
            }};
            return validStates.contains(toStatus);
        }
    };

    private String displayString;

    SubscriptionStatus(String displayString) {
        this.displayString = displayString;
    }

    public abstract boolean canTransitionTo(SubscriptionStatus toStatus);

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

    public boolean canChangeSubscription() {
        return Arrays.asList(ACTIVE, SUSPENDED, NEW_EARLY).contains(this);
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
