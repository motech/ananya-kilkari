package org.motechproject.ananya.kilkari.domain;

import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubscriptionStateMachine {
    private static Map<SubscriptionStatus, List<SubscriptionStatus>> stateMap = new HashMap<SubscriptionStatus, List<SubscriptionStatus>>() {
        {
            put(SubscriptionStatus.NEW, constructValidStates(SubscriptionStatus.NEW));
            put(SubscriptionStatus.NEW_EARLY, constructValidStates(SubscriptionStatus.NEW_EARLY));
            put(SubscriptionStatus.PENDING_ACTIVATION, constructValidStates(SubscriptionStatus.NEW, SubscriptionStatus.NEW_EARLY, SubscriptionStatus.PENDING_ACTIVATION));
            put(SubscriptionStatus.ACTIVATION_FAILED, constructValidStates(SubscriptionStatus.PENDING_ACTIVATION, SubscriptionStatus.ACTIVATION_FAILED));
            put(SubscriptionStatus.ACTIVE, constructValidStates(SubscriptionStatus.PENDING_ACTIVATION, SubscriptionStatus.SUSPENDED, SubscriptionStatus.ACTIVE));
            put(SubscriptionStatus.SUSPENDED, constructValidStates(SubscriptionStatus.ACTIVE, SubscriptionStatus.SUSPENDED));
            put(SubscriptionStatus.PENDING_COMPLETION, constructValidStates(SubscriptionStatus.ACTIVE, SubscriptionStatus.SUSPENDED, SubscriptionStatus.DEACTIVATION_REQUEST_RECEIVED));
            put(SubscriptionStatus.COMPLETED, constructValidStates(SubscriptionStatus.COMPLETED, SubscriptionStatus.PENDING_COMPLETION));
            put(SubscriptionStatus.PENDING_DEACTIVATION, constructValidStates(SubscriptionStatus.PENDING_DEACTIVATION, SubscriptionStatus.DEACTIVATION_REQUEST_RECEIVED));
            put(SubscriptionStatus.DEACTIVATED, constructValidStates(SubscriptionStatus.ACTIVE, SubscriptionStatus.DEACTIVATION_REQUEST_RECEIVED, SubscriptionStatus.PENDING_DEACTIVATION,
                    SubscriptionStatus.DEACTIVATED, SubscriptionStatus.SUSPENDED, SubscriptionStatus.PENDING_COMPLETION));
        }
    };

    private static ArrayList<SubscriptionStatus> constructValidStates(SubscriptionStatus... subscriptionStatuses) {
        ArrayList<SubscriptionStatus> validStates = new ArrayList();
        for (SubscriptionStatus status : subscriptionStatuses) {
            validStates.add(status);
        }
        return validStates;
    }

    public static boolean canTransition(SubscriptionStatus fromState, SubscriptionStatus toState) {
        List<SubscriptionStatus> subscriptionStatuses = stateMap.get(toState);
        return subscriptionStatuses.contains(fromState);
    }
}
