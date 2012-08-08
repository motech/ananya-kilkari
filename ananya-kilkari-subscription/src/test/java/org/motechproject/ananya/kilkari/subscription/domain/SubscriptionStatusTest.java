package org.motechproject.ananya.kilkari.subscription.domain;

import org.junit.Test;

import static org.junit.Assert.*;

public class SubscriptionStatusTest {

    @Test
    public void canChangePackIfTheStatusIsValid() {
        assertTrue(SubscriptionStatus.ACTIVE.canChangePack());
        assertTrue(SubscriptionStatus.SUSPENDED.canChangePack());
        assertTrue(SubscriptionStatus.NEW_EARLY.canChangePack());

        assertFalse(SubscriptionStatus.DEACTIVATED.canChangePack());
        assertFalse(SubscriptionStatus.NEW.canChangePack());
        assertFalse(SubscriptionStatus.ACTIVATION_FAILED.canChangePack());
        assertFalse(SubscriptionStatus.COMPLETED.canChangePack());
        assertFalse(SubscriptionStatus.DEACTIVATION_REQUEST_RECEIVED.canChangePack());
        assertFalse(SubscriptionStatus.PENDING_ACTIVATION.canChangePack());
        assertFalse(SubscriptionStatus.PENDING_COMPLETION.canChangePack());
        assertFalse(SubscriptionStatus.PENDING_DEACTIVATION.canChangePack());
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
        assertEquals("Early Subscripiton", SubscriptionStatus.NEW_EARLY.getDisplayString());
        assertEquals("Pending Subscription",SubscriptionStatus.PENDING_ACTIVATION.getDisplayString());
        assertEquals("Activation Failed",SubscriptionStatus.ACTIVATION_FAILED.getDisplayString());
        assertEquals("Completed",SubscriptionStatus.COMPLETED.getDisplayString());
        assertEquals("Activated",SubscriptionStatus.ACTIVE.getDisplayString());
        assertEquals("Deactivation Requested",SubscriptionStatus.DEACTIVATION_REQUEST_RECEIVED.getDisplayString());
        assertEquals("Pending Deactivation",SubscriptionStatus.PENDING_DEACTIVATION.getDisplayString());
        assertEquals("Deactivated",SubscriptionStatus.DEACTIVATED.getDisplayString());
        assertEquals("Suspended",SubscriptionStatus.SUSPENDED.getDisplayString());
        assertEquals("Pending Completion",SubscriptionStatus.PENDING_COMPLETION.getDisplayString());
    }
}
