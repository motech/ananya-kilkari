package org.motechproject.ananya.kilkari.utils;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SubscriptionUtilsTest {
    @Test
    public void shouldReturnCurrentWeekNumberBasedOnSubscriptionCreationDateAndPack() {
        Subscription fifteenMonthSubscription = new Subscription("9999999999", SubscriptionPack.FIFTEEN_MONTHS, DateTime.now().minusWeeks(2));
        assertEquals(3, SubscriptionUtils.currentSubscriptionPackWeek(fifteenMonthSubscription));

        Subscription twelveMonthSubscription = new Subscription("9999999999", SubscriptionPack.TWELVE_MONTHS, DateTime.now().minusWeeks(2));
        assertEquals(15, SubscriptionUtils.currentSubscriptionPackWeek(twelveMonthSubscription));
        
        Subscription sevenMonthSubscription = new Subscription("9999999999", SubscriptionPack.SEVEN_MONTHS, DateTime.now().minusWeeks(2));
        assertEquals(35, SubscriptionUtils.currentSubscriptionPackWeek(sevenMonthSubscription));
    }
    
    @Test
    public void shouldReturnTrueIfThePackHasBeenCompleted() {
        Subscription fifteenMonthSubscription = new Subscription("9999999999", SubscriptionPack.FIFTEEN_MONTHS, DateTime.now().minusWeeks(59));
        assertTrue(SubscriptionUtils.hasPackBeenCompleted(fifteenMonthSubscription));

        Subscription twelveMonthSubscription = new Subscription("9999999999", SubscriptionPack.TWELVE_MONTHS, DateTime.now().minusWeeks(47));
        assertTrue(SubscriptionUtils.hasPackBeenCompleted(twelveMonthSubscription));

        Subscription sevenMonthSubscription = new Subscription("9999999999", SubscriptionPack.SEVEN_MONTHS, DateTime.now().minusWeeks(27));
        assertTrue(SubscriptionUtils.hasPackBeenCompleted(sevenMonthSubscription));
    }

    @Test
    public void shouldReturnFalseIfThePackHasNotBeenCompleted() {
        Subscription fifteenMonthSubscription = new Subscription("9999999999", SubscriptionPack.FIFTEEN_MONTHS, DateTime.now().minusWeeks(58));
        assertFalse(SubscriptionUtils.hasPackBeenCompleted(fifteenMonthSubscription));

        Subscription twelveMonthSubscription = new Subscription("9999999999", SubscriptionPack.TWELVE_MONTHS, DateTime.now().minusWeeks(46));
        assertFalse(SubscriptionUtils.hasPackBeenCompleted(twelveMonthSubscription));

        Subscription sevenMonthSubscription = new Subscription("9999999999", SubscriptionPack.SEVEN_MONTHS, DateTime.now().minusWeeks(26));
        assertFalse(SubscriptionUtils.hasPackBeenCompleted(sevenMonthSubscription));
    }
}
