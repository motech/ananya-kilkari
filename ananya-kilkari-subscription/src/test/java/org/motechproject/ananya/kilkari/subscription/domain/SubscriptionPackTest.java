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

    @Test
    public void shouldFailValidationIfWeekIsOutOfPacksRange() {
        assertFalse(SubscriptionPack.FIFTEEN_MONTHS.isWeekWithinPackRange(-1));
        assertTrue(SubscriptionPack.FIFTEEN_MONTHS.isWeekWithinPackRange(1));

        assertFalse(SubscriptionPack.TWELVE_MONTHS.isWeekWithinPackRange(2));
        assertTrue(SubscriptionPack.TWELVE_MONTHS.isWeekWithinPackRange(13));

        assertFalse(SubscriptionPack.SEVEN_MONTHS.isWeekWithinPackRange(30));
        assertTrue(SubscriptionPack.SEVEN_MONTHS.isWeekWithinPackRange(32));

        assertFalse(SubscriptionPack.TWELVE_MONTHS.isWeekWithinPackRange(62));
    }
}
