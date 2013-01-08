package org.motechproject.ananya.kilkari.subscription.domain;

import org.junit.Test;

import static junit.framework.Assert.*;

public class SubscriberCareReasonsTest {
    @Test
    public void shouldValidateSubscriberCareReasons() {
        assertTrue(SubscriberCareReasons.isValid("  nEW_SUBSCRIPTION "));
        assertTrue(SubscriberCareReasons.isValid("  helP "));
        assertFalse(SubscriberCareReasons.isValid("some"));
        assertFalse(SubscriberCareReasons.isValid(""));
        assertFalse(SubscriberCareReasons.isValid(null));
    }

    @Test
    public void shouldGetTheEnumValueForAString() {
        assertEquals(SubscriberCareReasons.NEW_SUBSCRIPTION, SubscriberCareReasons.getFor("  new_Subscription "));
        assertEquals(SubscriberCareReasons.HELP, SubscriberCareReasons.getFor("  helP "));
        assertNull( SubscriberCareReasons.getFor("  some "));
        assertNull(SubscriberCareReasons.getFor(""));
        assertNull(SubscriberCareReasons.getFor(null));
    }
}
