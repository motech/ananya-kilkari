package org.motechproject.ananya.kilkari.obd.domain;

import org.joda.time.DateTime;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class CompareMessageIdStrategyTest {

    @Test
    public void shouldReturnAnObjectWithLowerCampaignMessageWeek() {
        final CampaignMessage campaignMessageForWeek1 = new CampaignMessage("subscriptionId1", "WEEK1", DateTime.now(), "1234567890", "IDEA", DateTime.now().minusWeeks(1));
        final CampaignMessage campaignMessageForWeek3 = new CampaignMessage("subscriptionId3", "WEEK3", DateTime.now(), "1234567890", "IDEA", DateTime.now().minusWeeks(1));

        int compare = CompareMessageIdStrategy.compare(campaignMessageForWeek1, campaignMessageForWeek3);
        assertEquals(-1, compare);
    }
}
