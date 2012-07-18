package org.motechproject.ananya.kilkari.utils;

import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.Mockito;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class CampaignMessageIdStrategyTest {

    @Test
    public void shouldCreateMessageIdIfPackWasSubscribed2DaysBack() {
        Subscription subscription = Mockito.mock(Subscription.class);
        when(subscription.getCreationDate()).thenReturn(DateTime.now().minusDays(2));
        when(subscription.getPack()).thenReturn(SubscriptionPack.FIFTEEN_MONTHS);

        String messageId = new CampaignMessageIdStrategy().createMessageId(subscription);

        assertEquals("WEEK1", messageId);
    }

    @Test
    public void shouldCreateMessageIdIfPackWasSubscribedToday() {
        Subscription subscription = Mockito.mock(Subscription.class);
        when(subscription.getCreationDate()).thenReturn(DateTime.now());
        when(subscription.getPack()).thenReturn(SubscriptionPack.FIFTEEN_MONTHS);

        String messageId = new CampaignMessageIdStrategy().createMessageId(subscription);

        assertEquals("WEEK1", messageId);
    }

    @Test
    public void shouldCreateMessageIdForThisWeekIfPackWasSubscribedLessThanAWeekBack() {
        Subscription subscription = Mockito.mock(Subscription.class);
        when(subscription.getCreationDate()).thenReturn(DateTime.now().minusDays(7).plusHours(2));
        when(subscription.getPack()).thenReturn(SubscriptionPack.FIFTEEN_MONTHS);

        String messageId = new CampaignMessageIdStrategy().createMessageId(subscription);

        assertEquals("WEEK1", messageId);
    }

    @Test
    public void shouldCreateMessageIdForNextWeekIfPackWasSubscribedMoreThanAWeekBack() {
        Subscription subscription = Mockito.mock(Subscription.class);
        when(subscription.getCreationDate()).thenReturn(DateTime.now().minusDays(7).minusHours(2));
        when(subscription.getPack()).thenReturn(SubscriptionPack.FIFTEEN_MONTHS);

        String messageId = new CampaignMessageIdStrategy().createMessageId(subscription);

        assertEquals("WEEK2", messageId);
    }

    @Test
         public void shouldCreateMessageIdForNextWeekIfPackWasSubscribedMoreThanAWeekBackAndThePackIsTwelveMonthsPack() {
        Subscription subscription = Mockito.mock(Subscription.class);
        when(subscription.getCreationDate()).thenReturn(DateTime.now().minusDays(7).minusHours(2));
        when(subscription.getPack()).thenReturn(SubscriptionPack.TWELVE_MONTHS);

        String messageId = new CampaignMessageIdStrategy().createMessageId(subscription);

        assertEquals("WEEK14", messageId);
    }

    @Test
    public void shouldCreateMessageIdForNextWeekIfPackWasSubscribedMoreThanAWeekBackAndThePackIsSevenMonthsPack() {
        Subscription subscription = Mockito.mock(Subscription.class);
        when(subscription.getCreationDate()).thenReturn(DateTime.now().minusDays(7).minusHours(2));
        when(subscription.getPack()).thenReturn(SubscriptionPack.SEVEN_MONTHS);

        String messageId = new CampaignMessageIdStrategy().createMessageId(subscription);

        assertEquals("WEEK34", messageId);
    }

    @Test
    public void shouldReturnTrueIfThePackHasBeenCompleted() {
        Subscription fifteenMonthsubscription = new Subscription("9999999999", SubscriptionPack.FIFTEEN_MONTHS, DateTime.now().minusWeeks(59));
        assertTrue(new CampaignMessageIdStrategy().hasPackBeenCompleted(fifteenMonthsubscription));

        Subscription twelveMonthsubscription = new Subscription("9999999999", SubscriptionPack.TWELVE_MONTHS, DateTime.now().minusWeeks(47));
        assertTrue(new CampaignMessageIdStrategy().hasPackBeenCompleted(twelveMonthsubscription));

        Subscription sevenMonthsubscription = new Subscription("9999999999", SubscriptionPack.SEVEN_MONTHS, DateTime.now().minusWeeks(27));
        assertTrue(new CampaignMessageIdStrategy().hasPackBeenCompleted(sevenMonthsubscription));
    }

    @Test
    public void shouldReturnFalseIfThePackHasNotBeenCompleted() {
        Subscription fifteenMonthsubscription = new Subscription("9999999999", SubscriptionPack.FIFTEEN_MONTHS, DateTime.now().minusWeeks(58));
        assertFalse(new CampaignMessageIdStrategy().hasPackBeenCompleted(fifteenMonthsubscription));

        Subscription twelveMonthsubscription = new Subscription("9999999999", SubscriptionPack.TWELVE_MONTHS, DateTime.now().minusWeeks(46));
        assertFalse(new CampaignMessageIdStrategy().hasPackBeenCompleted(twelveMonthsubscription));

        Subscription sevenMonthsubscription = new Subscription("9999999999", SubscriptionPack.SEVEN_MONTHS, DateTime.now().minusWeeks(26));
        assertFalse(new CampaignMessageIdStrategy().hasPackBeenCompleted(sevenMonthsubscription));
    }
}
