package org.motechproject.ananya.kilkari.utils;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;

import static org.junit.Assert.assertEquals;

public class CampaignMessageIdStrategyTest {

    @Test
    public void shouldCreateMessageIdIfPackWasSubscribed2DaysBack() {
        String campaignName = "kilkari-mother-child-campaign-sixteen-months";

        String messageId = new CampaignMessageIdStrategy().createMessageId(campaignName, DateTime.now().minusDays(2), SubscriptionPack.BARI_KILKARI);

        assertEquals("WEEK1", messageId);
    }

    @Test
    public void shouldCreateMessageIdIfPackWasSubscribedToday() {
        String campaignName = "kilkari-mother-child-campaign-twelve-months";

        String messageId = new CampaignMessageIdStrategy().createMessageId(campaignName, DateTime.now(), SubscriptionPack.NAVJAAT_KILKARI);

        assertEquals("WEEK17", messageId);
    }

    @Test
    public void shouldCreateMessageIdForInfantDeathCampaign(){
        String campaignName = "kilkari-mother-child-campaign-infant-death";

        String messageId = new CampaignMessageIdStrategy().createMessageId(campaignName, DateTime.now(), SubscriptionPack.BARI_KILKARI);

        assertEquals("ID1", messageId);
    }

    @Test
    public void shouldCreateMessageIdForMiscarriageCampaign(){
        String campaignName = "kilkari-mother-child-campaign-miscarriage";

        String messageId = new CampaignMessageIdStrategy().createMessageId(campaignName, DateTime.now(), SubscriptionPack.BARI_KILKARI);

        assertEquals("MC1", messageId);
    }
}
