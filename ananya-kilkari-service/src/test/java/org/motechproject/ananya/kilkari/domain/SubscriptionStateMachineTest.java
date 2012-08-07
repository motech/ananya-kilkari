package org.motechproject.ananya.kilkari.domain;

import org.junit.Test;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionStatus;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class SubscriptionStateMachineTest {
    @Test
    public void shouldCheckNewStateTransition() {
        SubscriptionStatus toState = SubscriptionStatus.NEW;

        assertTrue(SubscriptionStateMachine.canTransition(SubscriptionStatus.NEW, toState));

        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.NEW_EARLY, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.ACTIVE, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.ACTIVATION_FAILED, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.COMPLETED, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.DEACTIVATED, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.DEACTIVATION_REQUEST_RECEIVED, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.SUSPENDED, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.PENDING_ACTIVATION, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.PENDING_COMPLETION, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.PENDING_DEACTIVATION, toState));
    }

    @Test
    public void shouldCheckNewEarlyStateTransition() {
        SubscriptionStatus toState = SubscriptionStatus.NEW_EARLY;

        assertTrue(SubscriptionStateMachine.canTransition(SubscriptionStatus.NEW_EARLY, toState));

        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.NEW, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.ACTIVE, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.ACTIVATION_FAILED, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.COMPLETED, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.DEACTIVATED, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.DEACTIVATION_REQUEST_RECEIVED, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.SUSPENDED, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.PENDING_ACTIVATION, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.PENDING_COMPLETION, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.PENDING_DEACTIVATION, toState));
    }

    @Test
    public void shouldCheckPendingActivationStateTransition() {
        SubscriptionStatus toState = SubscriptionStatus.PENDING_ACTIVATION;

        assertTrue(SubscriptionStateMachine.canTransition(SubscriptionStatus.PENDING_ACTIVATION, toState));
        assertTrue(SubscriptionStateMachine.canTransition(SubscriptionStatus.NEW_EARLY, toState));
        assertTrue(SubscriptionStateMachine.canTransition(SubscriptionStatus.NEW, toState));

        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.ACTIVE, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.ACTIVATION_FAILED, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.COMPLETED, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.DEACTIVATED, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.DEACTIVATION_REQUEST_RECEIVED, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.SUSPENDED, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.PENDING_COMPLETION, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.PENDING_DEACTIVATION, toState));
    }

    @Test
    public void shouldCheckActivationFailedStateTransition() {
        SubscriptionStatus toState = SubscriptionStatus.ACTIVATION_FAILED;

        assertTrue(SubscriptionStateMachine.canTransition(SubscriptionStatus.ACTIVATION_FAILED, toState));
        assertTrue(SubscriptionStateMachine.canTransition(SubscriptionStatus.PENDING_ACTIVATION, toState));

        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.NEW_EARLY, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.NEW, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.ACTIVE, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.COMPLETED, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.DEACTIVATED, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.DEACTIVATION_REQUEST_RECEIVED, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.SUSPENDED, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.PENDING_COMPLETION, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.PENDING_DEACTIVATION, toState));
    }

    @Test
    public void shouldCheckActiveStateTransition() {
        SubscriptionStatus toState = SubscriptionStatus.ACTIVE;

        assertTrue(SubscriptionStateMachine.canTransition(SubscriptionStatus.PENDING_ACTIVATION, toState));
        assertTrue(SubscriptionStateMachine.canTransition(SubscriptionStatus.SUSPENDED, toState));
        assertTrue(SubscriptionStateMachine.canTransition(SubscriptionStatus.ACTIVE, toState));

        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.ACTIVATION_FAILED, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.NEW_EARLY, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.NEW, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.COMPLETED, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.DEACTIVATED, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.DEACTIVATION_REQUEST_RECEIVED, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.PENDING_COMPLETION, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.PENDING_DEACTIVATION, toState));
    }

    @Test
    public void shouldCheckSuspendedStateTransition() {
        SubscriptionStatus toState = SubscriptionStatus.SUSPENDED;

        assertTrue(SubscriptionStateMachine.canTransition(SubscriptionStatus.ACTIVE, toState));
        assertTrue(SubscriptionStateMachine.canTransition(SubscriptionStatus.SUSPENDED, toState));

        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.PENDING_ACTIVATION, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.ACTIVATION_FAILED, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.NEW_EARLY, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.NEW, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.COMPLETED, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.DEACTIVATED, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.PENDING_COMPLETION, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.PENDING_DEACTIVATION, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.DEACTIVATION_REQUEST_RECEIVED, toState));
    }

    @Test
    public void shouldCheckPendingCompletionStateTransition() {
        SubscriptionStatus toState = SubscriptionStatus.PENDING_COMPLETION;

        assertTrue(SubscriptionStateMachine.canTransition(SubscriptionStatus.ACTIVE, toState));
        assertTrue(SubscriptionStateMachine.canTransition(SubscriptionStatus.SUSPENDED, toState));
        assertTrue(SubscriptionStateMachine.canTransition(SubscriptionStatus.DEACTIVATION_REQUEST_RECEIVED, toState));

        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.PENDING_ACTIVATION, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.ACTIVATION_FAILED, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.NEW_EARLY, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.NEW, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.COMPLETED, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.DEACTIVATED, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.PENDING_COMPLETION, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.PENDING_DEACTIVATION, toState));
    }

    @Test
    public void shouldCheckCompletedStateTransition() {
        SubscriptionStatus toState = SubscriptionStatus.COMPLETED;

        assertTrue(SubscriptionStateMachine.canTransition(SubscriptionStatus.COMPLETED, toState));
        assertTrue(SubscriptionStateMachine.canTransition(SubscriptionStatus.PENDING_COMPLETION, toState));

        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.ACTIVE, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.SUSPENDED, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.DEACTIVATION_REQUEST_RECEIVED, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.PENDING_ACTIVATION, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.ACTIVATION_FAILED, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.NEW_EARLY, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.NEW, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.DEACTIVATED, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.PENDING_DEACTIVATION, toState));
    }

    @Test
    public void shouldCheckPendingDeactivationStateTransition() {
        SubscriptionStatus toState = SubscriptionStatus.PENDING_DEACTIVATION;

        assertTrue(SubscriptionStateMachine.canTransition(SubscriptionStatus.DEACTIVATION_REQUEST_RECEIVED, toState));
        assertTrue(SubscriptionStateMachine.canTransition(SubscriptionStatus.PENDING_DEACTIVATION, toState));

        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.COMPLETED, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.PENDING_COMPLETION, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.ACTIVE, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.SUSPENDED, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.PENDING_ACTIVATION, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.ACTIVATION_FAILED, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.NEW_EARLY, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.NEW, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.DEACTIVATED, toState));
    }

    @Test
    public void shouldCheckDeactivatedStateTransition() {
        SubscriptionStatus toState = SubscriptionStatus.DEACTIVATED;

        assertTrue(SubscriptionStateMachine.canTransition(SubscriptionStatus.ACTIVE, toState));
        assertTrue(SubscriptionStateMachine.canTransition(SubscriptionStatus.DEACTIVATION_REQUEST_RECEIVED, toState));
        assertTrue(SubscriptionStateMachine.canTransition(SubscriptionStatus.PENDING_DEACTIVATION, toState));
        assertTrue(SubscriptionStateMachine.canTransition(SubscriptionStatus.DEACTIVATED, toState));
        assertTrue(SubscriptionStateMachine.canTransition(SubscriptionStatus.SUSPENDED, toState));
        assertTrue(SubscriptionStateMachine.canTransition(SubscriptionStatus.PENDING_COMPLETION, toState));

        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.COMPLETED, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.PENDING_ACTIVATION, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.ACTIVATION_FAILED, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.NEW_EARLY, toState));
        assertFalse(SubscriptionStateMachine.canTransition(SubscriptionStatus.NEW, toState));
    }
}
