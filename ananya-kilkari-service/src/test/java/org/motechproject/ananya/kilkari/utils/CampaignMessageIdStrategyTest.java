package org.motechproject.ananya.kilkari.utils;

import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.Mockito;
import org.motechproject.ananya.kilkari.domain.Subscription;
import org.motechproject.ananya.kilkari.domain.SubscriptionPack;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class CampaignMessageIdStrategyTest {

    @Test
    public void shouldCreateMessageIdIfPackWasSubscribed2DaysBack() {
        Subscription subscription = Mockito.mock(Subscription.class);
        when(subscription.getCreationDate()).thenReturn(DateTime.now().minusDays(2));
        when(subscription.getPack()).thenReturn(SubscriptionPack.FIFTEEN_MONTHS);

        String messageId = new CampaignMessageIdStrategy().createMessageId(subscription);

        assertEquals("Week1", messageId);
    }

    @Test
    public void shouldCreateMessageIdIfPackWasSubscribedToday() {
        Subscription subscription = Mockito.mock(Subscription.class);
        when(subscription.getCreationDate()).thenReturn(DateTime.now());
        when(subscription.getPack()).thenReturn(SubscriptionPack.FIFTEEN_MONTHS);

        String messageId = new CampaignMessageIdStrategy().createMessageId(subscription);

        assertEquals("Week1", messageId);
    }

    @Test
    public void shouldCreateMessageIdForThisWeekIfPackWasSubscribedLessThanAWeekBack() {
        Subscription subscription = Mockito.mock(Subscription.class);
        when(subscription.getCreationDate()).thenReturn(DateTime.now().minusDays(7).plusHours(2));
        when(subscription.getPack()).thenReturn(SubscriptionPack.FIFTEEN_MONTHS);

        String messageId = new CampaignMessageIdStrategy().createMessageId(subscription);

        assertEquals("Week1", messageId);
    }

    @Test
    public void shouldCreateMessageIdForNextWeekIfPackWasSubscribedMoreThanAWeekBack() {
        Subscription subscription = Mockito.mock(Subscription.class);
        when(subscription.getCreationDate()).thenReturn(DateTime.now().minusDays(7).minusHours(2));
        when(subscription.getPack()).thenReturn(SubscriptionPack.FIFTEEN_MONTHS);

        String messageId = new CampaignMessageIdStrategy().createMessageId(subscription);

        assertEquals("Week2", messageId);
    }

    @Test
         public void shouldCreateMessageIdForNextWeekIfPackWasSubscribedMoreThanAWeekBackAndThePackIsTwelveMonthsPack() {
        Subscription subscription = Mockito.mock(Subscription.class);
        when(subscription.getCreationDate()).thenReturn(DateTime.now().minusDays(7).minusHours(2));
        when(subscription.getPack()).thenReturn(SubscriptionPack.TWELVE_MONTHS);

        String messageId = new CampaignMessageIdStrategy().createMessageId(subscription);

        assertEquals("Week14", messageId);
    }

    @Test
    public void shouldCreateMessageIdForNextWeekIfPackWasSubscribedMoreThanAWeekBackAndThePackIsSevenMonthsPack() {
        Subscription subscription = Mockito.mock(Subscription.class);
        when(subscription.getCreationDate()).thenReturn(DateTime.now().minusDays(7).minusHours(2));
        when(subscription.getPack()).thenReturn(SubscriptionPack.SEVEN_MONTHS);

        String messageId = new CampaignMessageIdStrategy().createMessageId(subscription);

        assertEquals("Week34", messageId);
    }


}
