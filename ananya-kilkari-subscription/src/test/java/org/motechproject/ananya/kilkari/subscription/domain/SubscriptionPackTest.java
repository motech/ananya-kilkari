package org.motechproject.ananya.kilkari.subscription.domain;

import org.joda.time.DateTime;
import org.joda.time.Weeks;
import org.junit.Test;

import static org.junit.Assert.*;

public class SubscriptionPackTest {

    @Test
    public void shouldCreateEnumFromString() {
        assertEquals(SubscriptionPack.from("choti_kilkari"), SubscriptionPack.CHOTI_KILKARI);
        assertEquals(SubscriptionPack.from("CHOTI_KILKARI"), SubscriptionPack.CHOTI_KILKARI);
        assertEquals(SubscriptionPack.from("CHOTI_KILKARi"), SubscriptionPack.CHOTI_KILKARI);
        assertEquals(SubscriptionPack.from(" CHOTI_KILKARi "), SubscriptionPack.CHOTI_KILKARI);
    }

    @Test
    public void shouldReturnTrueIfSubscriptionPackIsValid() {
        assertTrue(SubscriptionPack.isValid("choti_kilkari"));
        assertTrue(SubscriptionPack.isValid("CHOTI_KILKARI"));
        assertTrue(SubscriptionPack.isValid("CHOTI_KILKARi"));
        assertTrue(SubscriptionPack.isValid(" CHOTI_KiLKARi "));
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
        assertFalse(SubscriptionPack.BARI_KILKARI.isValidWeekNumber(-1));
        assertFalse(SubscriptionPack.BARI_KILKARI.isValidWeekNumber(65));
        assertTrue(SubscriptionPack.BARI_KILKARI.isValidWeekNumber(1));

        assertFalse(SubscriptionPack.CHOTI_KILKARI.isValidWeekNumber(50));
        assertTrue(SubscriptionPack.CHOTI_KILKARI.isValidWeekNumber(13));

        assertFalse(SubscriptionPack.NANHI_KILKARI.isValidWeekNumber(30));
        assertTrue(SubscriptionPack.NANHI_KILKARI.isValidWeekNumber(28));
    }

    @Test
    public void shouldSetStartDateThreeMonthsBeforeEDDForSixteenMonthsPack() {
        DateTime now = DateTime.now();
        DateTime edd = now.plusMonths(2);
        DateTime startDate = SubscriptionPack.BARI_KILKARI.getStartDate(edd);

        assertEquals(edd.getDayOfWeek(), startDate.getDayOfWeek());
        assertEquals(16, Weeks.weeksBetween(startDate, edd).getWeeks());
    }

    @Test
    public void shouldSetStartDateFiveMonthsAfterDOBForSevenMonthsPack() {
        DateTime now = DateTime.now();
        DateTime dob = now.minusMonths(2);
        DateTime startDate = SubscriptionPack.NANHI_KILKARI.getStartDate(dob);

        assertEquals(dob.getDayOfWeek(), startDate.getDayOfWeek());
        assertTrue(Weeks.weeksBetween(dob, startDate).getWeeks() >= 20);
    }

    @Test
    public void shouldSetStartDateOnSameDateAsDOBForTwelveMonthsPack() {
        DateTime now = DateTime.now();
        DateTime dob = now;
        DateTime startDate = SubscriptionPack.CHOTI_KILKARI.getStartDate(dob);

        assertEquals(now, startDate);
    }

    @Test
    public void shouldSetStartDateBasedOnTheCurrentWeekForSixteenMonthsPack() {
        DateTime now = DateTime.now();
        Integer weekNumber = 4;
        DateTime startDate = SubscriptionPack.BARI_KILKARI.getStartDateForWeek(now, weekNumber);

        assertEquals(now.minusWeeks(3), startDate);
    }

    @Test
    public void shouldCheckIfAPackIsPriorToTheCurrentPack() {
        assertTrue(SubscriptionPack.BARI_KILKARI.startsBefore(SubscriptionPack.CHOTI_KILKARI));
        assertTrue(SubscriptionPack.BARI_KILKARI.startsBefore(SubscriptionPack.NANHI_KILKARI));
        assertTrue(SubscriptionPack.CHOTI_KILKARI.startsBefore(SubscriptionPack.NANHI_KILKARI));

        assertFalse(SubscriptionPack.NANHI_KILKARI.startsBefore(SubscriptionPack.BARI_KILKARI));
        assertFalse(SubscriptionPack.NANHI_KILKARI.startsBefore(SubscriptionPack.CHOTI_KILKARI));
        assertFalse(SubscriptionPack.CHOTI_KILKARI.startsBefore(SubscriptionPack.BARI_KILKARI));
    }
}
