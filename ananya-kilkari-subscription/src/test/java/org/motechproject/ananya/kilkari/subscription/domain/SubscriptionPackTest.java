package org.motechproject.ananya.kilkari.subscription.domain;

import org.joda.time.DateTime;
import org.joda.time.Weeks;
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

    @Test
    public void shouldSetStartDateThreeMonthsBeforeEDDForFifteenMonthsPack() {
        DateTime now = DateTime.now();
        DateTime edd = now.plusMonths(2);
        DateTime startDate = SubscriptionPack.FIFTEEN_MONTHS.adjustStartDate(edd);

        assertEquals(edd.getDayOfWeek(), startDate.getDayOfWeek());
        assertTrue(Weeks.weeksBetween(startDate, edd).getWeeks() >= 12);
    }

    @Test
    public void shouldSetStartDateFiveMonthsAfterDOBForSevenMonthsPack() {
        DateTime now = DateTime.now();
        DateTime dob = now.minusMonths(2);
        DateTime startDate = SubscriptionPack.SEVEN_MONTHS.adjustStartDate(dob);

        assertEquals(dob.getDayOfWeek(), startDate.getDayOfWeek());
        assertTrue(Weeks.weeksBetween(dob, startDate).getWeeks() >= 20);
    }

    @Test
    public void shouldSetStartDateOnSameDateAsDOBForTwelveMonthsPack() {
        DateTime now = DateTime.now();
        DateTime dob = now;
        DateTime startDate = SubscriptionPack.TWELVE_MONTHS.adjustStartDate(dob);

        assertEquals(now, startDate);
    }

    @Test
    public void shouldSetStartDateBasedOnTheCurrentWeekForFifteenMonthsPack() {
        DateTime now = DateTime.now();
        Integer weekNumber = 4;
        DateTime startDate = SubscriptionPack.FIFTEEN_MONTHS.adjustStartDate(now, weekNumber);

        assertEquals(now.minusWeeks(3), startDate);
    }
}
