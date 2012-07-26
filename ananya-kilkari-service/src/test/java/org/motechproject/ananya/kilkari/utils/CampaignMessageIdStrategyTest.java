package org.motechproject.ananya.kilkari.utils;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;

import static org.junit.Assert.assertEquals;

public class CampaignMessageIdStrategyTest {

    @Test
    public void shouldCreateMessageIdIfPackWasSubscribed2DaysBack() {
        String campaignName = "kilkari-mother-child-campaign-fifteen-months";

        String messageId = new CampaignMessageIdStrategy().createMessageId(campaignName, DateTime.now().minusDays(2), SubscriptionPack.FIFTEEN_MONTHS);

        assertEquals("WEEK1", messageId);
    }

    @Test
    public void shouldCreateMessageIdIfPackWasSubscribedToday() {
        String campaignName = "kilkari-mother-child-campaign-twelve-months";

        String messageId = new CampaignMessageIdStrategy().createMessageId(campaignName, DateTime.now(), SubscriptionPack.TWELVE_MONTHS);

        assertEquals("WEEK13", messageId);
    }

    @Test
    public void shouldCreateMessageIdForInfantDeathCampaign(){
        String campaignName = "kilkari-mother-child-campaign-infant-death";

        String messageId = new CampaignMessageIdStrategy().createMessageId(campaignName, DateTime.now(), SubscriptionPack.FIFTEEN_MONTHS);

        assertEquals("ID1", messageId);
    }

    @Test
    public void shouldCreateMessageIdForMiscarriageCampaign(){
        String campaignName = "kilkari-mother-child-campaign-miscarriage";

        String messageId = new CampaignMessageIdStrategy().createMessageId(campaignName, DateTime.now(), SubscriptionPack.FIFTEEN_MONTHS);

        assertEquals("MC1", messageId);
    }
}
