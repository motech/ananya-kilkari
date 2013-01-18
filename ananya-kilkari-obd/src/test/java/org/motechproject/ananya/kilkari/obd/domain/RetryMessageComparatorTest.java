package org.motechproject.ananya.kilkari.obd.domain;

import org.joda.time.DateTime;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class RetryMessageComparatorTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldSortBasedOnDNPRetryCount_AndCampaignMessageWeek() {
        CampaignMessage campaignMessage1 = new CampaignMessage("subscriptionId1", "WEEK2", "1111111111", "IDEA", DateTime.now().plusWeeks(1));
        campaignMessage1.markSent();
        campaignMessage1.setStatusCode(CampaignMessageStatus.NA);
        campaignMessage1.markSent();
        campaignMessage1.setStatusCode(CampaignMessageStatus.NA);
        campaignMessage1.markSent();
        assertEquals(2, campaignMessage1.getNARetryCount());

        CampaignMessage campaignMessage2 = new CampaignMessage("subscriptionId2", "WEEK1", "1111111112", "IDEA", DateTime.now().plusWeeks(1));
        campaignMessage2.markSent();
        campaignMessage2.setStatusCode(CampaignMessageStatus.NA);
        campaignMessage2.markSent();
        assertEquals(1, campaignMessage2.getNARetryCount());

        CampaignMessage campaignMessage3 = new CampaignMessage("subscriptionId3", "WEEK5", "1111111113", "IDEA", DateTime.now().plusWeeks(1));
        campaignMessage3.markSent();
        campaignMessage3.setStatusCode(CampaignMessageStatus.NA);
        campaignMessage3.markSent();
        assertEquals(1, campaignMessage3.getNARetryCount());

        CampaignMessage campaignMessage4 = new CampaignMessage("subscriptionId4", "WEEK15", "1111111113", "IDEA", DateTime.now().plusWeeks(1));
        campaignMessage4.markSent();
        campaignMessage4.setStatusCode(CampaignMessageStatus.NA);
        campaignMessage4.markSent();
        assertEquals(1, campaignMessage4.getNARetryCount());

        ArrayList<CampaignMessage> campaignMessages = new ArrayList<>();
        campaignMessages.add(campaignMessage4);
        campaignMessages.add(campaignMessage3);
        campaignMessages.add(campaignMessage2);
        campaignMessages.add(campaignMessage1);

        assertEquals(campaignMessage4, campaignMessages.get(0));
        assertEquals(campaignMessage3, campaignMessages.get(1));
        assertEquals(campaignMessage2, campaignMessages.get(2));
        assertEquals(campaignMessage1, campaignMessages.get(3));

        Collections.sort(campaignMessages, new RetryMessageComparator());

        assertEquals(campaignMessage1, campaignMessages.get(0));
        assertEquals(campaignMessage2, campaignMessages.get(1));
        assertEquals(campaignMessage3, campaignMessages.get(2));
        assertEquals(campaignMessage4, campaignMessages.get(3));
    }

    @Test
    public void shouldThrowExceptionIfMessageIdIsNotInCorrectFormat() {
        final CampaignMessage campaignMessageWithInvalidMessageID = new CampaignMessage("subscriptionId1", "WEEK", "1234567890", "IDEA", DateTime.now().minusWeeks(1));
        final CampaignMessage campaignMessage = new CampaignMessage("subscriptionId2", "WEEK12", "1234567891", "IDEA", DateTime.now().minusWeeks(1));

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Wrong format for messageId: WEEK");

        List<CampaignMessage> campaignMessages = new ArrayList<CampaignMessage>() {{
            add(campaignMessage);
            add(campaignMessageWithInvalidMessageID);
        }};

        Collections.sort(campaignMessages, new NewMessageComparator());
    }
}
