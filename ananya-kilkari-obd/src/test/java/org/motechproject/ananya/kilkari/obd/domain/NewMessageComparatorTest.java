package org.motechproject.ananya.kilkari.obd.domain;

import org.joda.time.DateTime;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class NewMessageComparatorTest {
    @Test
    public void shouldReturnObjectWithAFailureOverAnObjectWithoutOnComparison() {
        CampaignMessage erroredCampaignMessage = new CampaignMessage();
        erroredCampaignMessage.setStatusCode(CampaignMessageStatus.DNC);
        CampaignMessage campaignMessageWithoutError = new CampaignMessage();
        List<CampaignMessage> campaignMessageList = new ArrayList<>();
        campaignMessageList.add(erroredCampaignMessage);
        campaignMessageList.add(campaignMessageWithoutError);

        Collections.sort(campaignMessageList, new NewMessageComparator());

        assertEquals(erroredCampaignMessage, campaignMessageList.get(0));
        assertEquals(campaignMessageWithoutError, campaignMessageList.get(1));
    }

    @Test
    public void shouldReturnObjectWithAHigherDNCRetryOnComparison() {
        CampaignMessage campaignMessageWithHigherDNCRetry = new CampaignMessage("subscriptionId", "WEEEK13", "1234567890", "airtel", DateTime.now().plusDays(2));
        campaignMessageWithHigherDNCRetry.setStatusCode(CampaignMessageStatus.DNC);
        campaignMessageWithHigherDNCRetry.markSent();
        campaignMessageWithHigherDNCRetry.markSent();

        CampaignMessage campaignMessageWithLowerDNCRetry = new CampaignMessage("subscriptionId1", "WEEEK13", "1234567890", "airtel", DateTime.now().plusDays(2));
        campaignMessageWithHigherDNCRetry.setStatusCode(CampaignMessageStatus.DNC);
        campaignMessageWithHigherDNCRetry.markSent();

        List<CampaignMessage> campaignMessageList = new ArrayList<>();
        campaignMessageList.add(campaignMessageWithHigherDNCRetry);
        campaignMessageList.add(campaignMessageWithLowerDNCRetry);

        Collections.sort(campaignMessageList, new NewMessageComparator());

        assertEquals(campaignMessageWithHigherDNCRetry, campaignMessageList.get(0));
        assertEquals(campaignMessageWithLowerDNCRetry, campaignMessageList.get(1));
    }
}
