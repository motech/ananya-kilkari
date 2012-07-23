package org.motechproject.ananya.kilkari.obd.domain;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;

import static junit.framework.Assert.assertEquals;

public class RetryMessageComparatorTest {
    @Test
    public void shouldSortBasedOnDNPRetryCount() {
        CampaignMessage campaignMessage1 = new CampaignMessage();
        campaignMessage1.markSent();
        campaignMessage1.setStatusCode(CampaignMessageStatus.DNP);
        campaignMessage1.markSent();
        campaignMessage1.setStatusCode(CampaignMessageStatus.DNP);
        campaignMessage1.markSent();
        assertEquals(2, campaignMessage1.getDnpRetryCount());

        CampaignMessage campaignMessage2 = new CampaignMessage();
        campaignMessage2.markSent();
        campaignMessage2.setStatusCode(CampaignMessageStatus.DNP);
        campaignMessage2.markSent();
        assertEquals(1, campaignMessage2.getDnpRetryCount());

        ArrayList<CampaignMessage> campaignMessages = new ArrayList<>();
        campaignMessages.add(campaignMessage2);
        campaignMessages.add(campaignMessage1);

        assertEquals(campaignMessage2, campaignMessages.get(0));
        assertEquals(campaignMessage1, campaignMessages.get(1));

        Collections.sort(campaignMessages, new RetryMessageComparator());

        assertEquals(campaignMessage1, campaignMessages.get(0));
        assertEquals(campaignMessage2, campaignMessages.get(1));
    }
}
