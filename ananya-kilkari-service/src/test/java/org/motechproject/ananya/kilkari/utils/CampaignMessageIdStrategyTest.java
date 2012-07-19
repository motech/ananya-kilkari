package org.motechproject.ananya.kilkari.utils;

import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.Mockito;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class CampaignMessageIdStrategyTest {

    @Test
    public void shouldCreateMessageIdIfPackWasSubscribed2DaysBack() {
        Subscription subscription = new Subscription("7676767678",SubscriptionPack.FIFTEEN_MONTHS,DateTime.now().minusDays(2));

        String messageId = new CampaignMessageIdStrategy().createMessageId(subscription);

        assertEquals("WEEK1", messageId);
    }

    @Test
    public void shouldCreateMessageIdIfPackWasSubscribedToday() {
        Subscription subscription = new Subscription("7676767678",SubscriptionPack.FIFTEEN_MONTHS,DateTime.now());
        String messageId = new CampaignMessageIdStrategy().createMessageId(subscription);

        assertEquals("WEEK1", messageId);
    }

    @Test
    public void shouldCreateMessageIdForThisWeekIfPackWasSubscribedLessThanAWeekBack() {
        Subscription subscription = new Subscription("7676767678",SubscriptionPack.FIFTEEN_MONTHS,DateTime.now().minusDays(7).plusHours(2));

        String messageId = new CampaignMessageIdStrategy().createMessageId(subscription);

        assertEquals("WEEK1", messageId);
    }

    @Test
    public void shouldCreateMessageIdForNextWeekIfPackWasSubscribedMoreThanAWeekBack() {
        Subscription subscription = new Subscription("7676767678",SubscriptionPack.FIFTEEN_MONTHS,DateTime.now().minusDays(7).minusHours(2));
        String messageId = new CampaignMessageIdStrategy().createMessageId(subscription);

        assertEquals("WEEK2", messageId);
    }

    @Test
         public void shouldCreateMessageIdForNextWeekIfPackWasSubscribedMoreThanAWeekBackAndThePackIsTwelveMonthsPack() {
        Subscription subscription = new Subscription("7676767678",SubscriptionPack.TWELVE_MONTHS,DateTime.now().minusDays(7).minusHours(2));
        String messageId = new CampaignMessageIdStrategy().createMessageId(subscription);

        assertEquals("WEEK14", messageId);
    }

    @Test
    public void shouldCreateMessageIdForNextWeekIfPackWasSubscribedMoreThanAWeekBackAndThePackIsSevenMonthsPack() {
        Subscription subscription = new Subscription("7676767678",SubscriptionPack.SEVEN_MONTHS,DateTime.now().minusDays(7).minusHours(2));
        String messageId = new CampaignMessageIdStrategy().createMessageId(subscription);

        assertEquals("WEEK34", messageId);
    }
}
