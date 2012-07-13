package org.motechproject.ananya.kilkari.subscription.domain;

import org.junit.Test;

import static org.junit.Assert.*;

public class SubscriptionPackTest {

    @Test
    public void shouldCreateEnumFromString() {
        assertEquals(SubscriptionPack.from("twelve_months"), SubscriptionPack.TWELVE_MONTHS);
        assertEquals(SubscriptionPack.from("TWELVE_MONTHS"), SubscriptionPack.TWELVE_MONTHS);
        assertEquals(SubscriptionPack.from("TWELVE_MONTHs"), SubscriptionPack.TWELVE_MONTHS);
        assertEquals(SubscriptionPack.from(" TWELVE_MONTHs "), SubscriptionPack.TWELVE_MONTHS);
    }

    @Test
    public void shouldReturnTrueIfSubscriptionPackIsValid() {
        assertTrue(SubscriptionPack.isValid("twelve_months"));
        assertTrue(SubscriptionPack.isValid("TWELVE_MONTHS"));
        assertTrue(SubscriptionPack.isValid("TWELVE_MONTHs"));
        assertTrue(SubscriptionPack.isValid(" TWELVE_MONTHs "));
    }

    @Test
    public void shouldReturnFalseIfSubscriptionPackIsInvalid() {
        assertFalse(SubscriptionPack.isValid(""));
        assertFalse(SubscriptionPack.isValid(" "));
        assertFalse(SubscriptionPack.isValid(null));
        assertFalse(SubscriptionPack.isValid("abcd"));
    }
}
