package org.motechproject.ananya.kilkari.subscription.domain;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
}
