package org.motechproject.ananya.kilkari.subscription.domain;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class SubscriptionStatusTest {

    @Test
    public void canChangeSubscriptionIfTheStatusIsValid() {
        assertTrue(SubscriptionStatus.ACTIVE.canChangeSubscription());
        assertTrue(SubscriptionStatus.SUSPENDED.canChangeSubscription());
        assertTrue(SubscriptionStatus.NEW_EARLY.canChangeSubscription());

        assertFalse(SubscriptionStatus.DEACTIVATED.canChangeSubscription());
        assertFalse(SubscriptionStatus.NEW.canChangeSubscription());
        assertFalse(SubscriptionStatus.ACTIVATION_FAILED.canChangeSubscription());
        assertFalse(SubscriptionStatus.COMPLETED.canChangeSubscription());
        assertFalse(SubscriptionStatus.DEACTIVATION_REQUEST_RECEIVED.canChangeSubscription());
        assertFalse(SubscriptionStatus.PENDING_ACTIVATION.canChangeSubscription());
        assertFalse(SubscriptionStatus.PENDING_COMPLETION.canChangeSubscription());
        assertFalse(SubscriptionStatus.PENDING_DEACTIVATION.canChangeSubscription());
    }

    @Test
    public void shouldCheckForEarlySubscriptionStatus() {
        assertTrue(SubscriptionStatus.NEW_EARLY.isNewEarly());
    }

    @Test
    public void shouldCheckForSuspendedSubscriptionStatus() {
        assertTrue(SubscriptionStatus.SUSPENDED.isSuspended());
    }

    @Test
    public void shouldGetTheDisplayString() {
        assertEquals("New", SubscriptionStatus.NEW.getDisplayString());
        assertEquals("Early Subscription", SubscriptionStatus.NEW_EARLY.getDisplayString());
        assertEquals("Pending Subscription", SubscriptionStatus.PENDING_ACTIVATION.getDisplayString());
        assertEquals("Activation Failed", SubscriptionStatus.ACTIVATION_FAILED.getDisplayString());
        assertEquals("Completed", SubscriptionStatus.COMPLETED.getDisplayString());
        assertEquals("Activated", SubscriptionStatus.ACTIVE.getDisplayString());
        assertEquals("Deactivation Requested", SubscriptionStatus.DEACTIVATION_REQUEST_RECEIVED.getDisplayString());
        assertEquals("Pending Deactivation", SubscriptionStatus.PENDING_DEACTIVATION.getDisplayString());
        assertEquals("Deactivated", SubscriptionStatus.DEACTIVATED.getDisplayString());
        assertEquals("Suspended", SubscriptionStatus.SUSPENDED.getDisplayString());
        assertEquals("Pending Completion", SubscriptionStatus.PENDING_COMPLETION.getDisplayString());
    }

    @Test
    public void shouldHaveStateChecksForNewState() {
        final SubscriptionStatus fromState = SubscriptionStatus.NEW;
        List<SubscriptionStatus> validStates = new ArrayList<SubscriptionStatus>() {{
            add(SubscriptionStatus.NEW);
            add(SubscriptionStatus.PENDING_ACTIVATION);
            add(SubscriptionStatus.DEACTIVATION_REQUEST_RECEIVED);
        }};

        assertTransitions(fromState, validStates);
    }

    @Test
    public void shouldHaveStateChecksForNewEarlyState() {
        final SubscriptionStatus fromState = SubscriptionStatus.NEW_EARLY;
        List<SubscriptionStatus> validStates = new ArrayList<SubscriptionStatus>() {{
            add(SubscriptionStatus.NEW_EARLY);
            add(SubscriptionStatus.PENDING_ACTIVATION);
            add(SubscriptionStatus.DEACTIVATION_REQUEST_RECEIVED);
        }};

        assertTransitions(fromState, validStates);
    }

    @Test
    public void shouldHaveStateChecksForPendingActivationState() {
        final SubscriptionStatus fromState = SubscriptionStatus.PENDING_ACTIVATION;
        List<SubscriptionStatus> validStates = new ArrayList<SubscriptionStatus>(){{
            add(SubscriptionStatus.PENDING_ACTIVATION);
            add(SubscriptionStatus.ACTIVATION_FAILED);
            add(SubscriptionStatus.ACTIVE);
            add(SubscriptionStatus.DEACTIVATION_REQUEST_RECEIVED);
        }};

        assertTransitions(fromState, validStates);
    }

    @Test
    public void shouldHaveStateChecksForActivationFailedState() {
        final SubscriptionStatus fromState = SubscriptionStatus.ACTIVATION_FAILED;
        List<SubscriptionStatus> validStates = new ArrayList<SubscriptionStatus>(){{
            add(SubscriptionStatus.ACTIVATION_FAILED);
        }};

        assertTransitions(fromState, validStates);
    }

    @Test
    public void shouldHaveStateChecksForCompletedState() {
        final SubscriptionStatus fromState = SubscriptionStatus.COMPLETED;
        List<SubscriptionStatus> validStates = new ArrayList<SubscriptionStatus>(){{
            add(SubscriptionStatus.COMPLETED);
        }};

        assertTransitions(fromState, validStates);
    }

    @Test
    public void shouldHaveStateChecksForActivatedState() {
        final SubscriptionStatus fromState = SubscriptionStatus.ACTIVE;
        List<SubscriptionStatus> validStates = new ArrayList<SubscriptionStatus>(){{
            add(SubscriptionStatus.ACTIVE);
            add(SubscriptionStatus.SUSPENDED);
            add(SubscriptionStatus.PENDING_COMPLETION);
            add(SubscriptionStatus.DEACTIVATION_REQUEST_RECEIVED);
            add(SubscriptionStatus.DEACTIVATED);
        }};

        assertTransitions(fromState, validStates);
    }

    @Test
    public void shouldHaveStateChecksForDeactivationRequestReceivedState() {
        final SubscriptionStatus fromState = SubscriptionStatus.DEACTIVATION_REQUEST_RECEIVED;
        List<SubscriptionStatus> validStates = new ArrayList<SubscriptionStatus>(){{
            add(SubscriptionStatus.DEACTIVATION_REQUEST_RECEIVED);
            add(SubscriptionStatus.PENDING_COMPLETION);
            add(SubscriptionStatus.PENDING_DEACTIVATION);
            add(SubscriptionStatus.DEACTIVATED);
        }};

        assertTransitions(fromState, validStates);
    }

    @Test
    public void shouldHaveStateChecksForPendingDeactivationState() {
        final SubscriptionStatus fromState = SubscriptionStatus.PENDING_DEACTIVATION;
        List<SubscriptionStatus> validStates = new ArrayList<SubscriptionStatus>(){{
            add(SubscriptionStatus.PENDING_DEACTIVATION);
            add(SubscriptionStatus.DEACTIVATED);
        }};

        assertTransitions(fromState, validStates);
    }

    @Test
    public void shouldHaveStateChecksForDeactivationState() {
        final SubscriptionStatus fromState = SubscriptionStatus.DEACTIVATED;
        List<SubscriptionStatus> validStates = new ArrayList<SubscriptionStatus>(){{
            add(SubscriptionStatus.DEACTIVATED);
        }};

        assertTransitions(fromState, validStates);
    }

    @Test
    public void shouldHaveStateChecksForSuspendedState() {
        final SubscriptionStatus fromState = SubscriptionStatus.SUSPENDED;
        List<SubscriptionStatus> validStates = new ArrayList<SubscriptionStatus>(){{
            add(SubscriptionStatus.SUSPENDED);
            add(SubscriptionStatus.ACTIVE);
            add(SubscriptionStatus.PENDING_COMPLETION);
            add(SubscriptionStatus.DEACTIVATION_REQUEST_RECEIVED);
            add(SubscriptionStatus.DEACTIVATED);
        }};

        assertTransitions(fromState, validStates);
    }

    @Test
    public void shouldHaveStateChecksForPendingCompletionState() {
        final SubscriptionStatus fromState = SubscriptionStatus.PENDING_COMPLETION;
        List<SubscriptionStatus> validStates = new ArrayList<SubscriptionStatus>(){{
            add(SubscriptionStatus.PENDING_COMPLETION);
            add(SubscriptionStatus.COMPLETED);
            add(SubscriptionStatus.DEACTIVATED);
        }};

        assertTransitions(fromState, validStates);
    }

    private void assertTransitions(SubscriptionStatus fromStatus, List<SubscriptionStatus> validStates) {
        for (SubscriptionStatus status : SubscriptionStatus.values()) {
            if (validStates.contains(status))
                assertTrue("Should allow transition to : " + status.name(), fromStatus.canTransitionTo(status));
            else
                assertFalse("Should not allow transition to : " + status.name(), fromStatus.canTransitionTo(status));
        }
    }
}
