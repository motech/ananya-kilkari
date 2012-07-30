package org.motechproject.ananya.kilkari.obd.domain;

import org.joda.time.DateTime;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class NewMessageComparatorTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

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

    @Test
    public void shouldReturnAnObjectWithLowerCampaignMessageWeek() {
        final CampaignMessage campaignMessageForWeek1 = new CampaignMessage("subscripitonId", "WEEK1", "1234567890", "IDEA", DateTime.now().minusWeeks(1));
        final CampaignMessage campaignMessageForWeek2 = new CampaignMessage("subscripitonId", "WEEK2", "1234567890", "IDEA", DateTime.now().minusWeeks(1));
        final CampaignMessage campaignMessageForWeek3 = new CampaignMessage("subscripitonId", "WEEK3", "1234567890", "IDEA", DateTime.now().minusWeeks(1));

        List<CampaignMessage> campaignMessages = new ArrayList<CampaignMessage>() {{
            add(campaignMessageForWeek3);
            add(campaignMessageForWeek2);
            add(campaignMessageForWeek1);
        }};

        Collections.sort(campaignMessages, new NewMessageComparator());

        assertEquals(campaignMessageForWeek1, campaignMessages.get(0));
        assertEquals(campaignMessageForWeek2, campaignMessages.get(1));
        assertEquals(campaignMessageForWeek3, campaignMessages.get(2));
    }

    @Test
    public void shouldThrowExceptionIfMessageIdIsNotInCorrectFormat() {
        final CampaignMessage campaignMessageWithInvalidMessageID = new CampaignMessage("subscripitonId1", "WEEK", "1234567890", "IDEA", DateTime.now().minusWeeks(1));
        final CampaignMessage campaignMessage = new CampaignMessage("subscripitonId2", "WEEK12", "1234567891", "IDEA", DateTime.now().minusWeeks(1));

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Wrong format for messageId: WEEK");

        List<CampaignMessage> campaignMessages = new ArrayList<CampaignMessage>() {{
            add(campaignMessage);
            add(campaignMessageWithInvalidMessageID);
        }};

        Collections.sort(campaignMessages, new NewMessageComparator());
    }

    @Test
    public void shouldReturnAnObjectWithDNCBeforeNew_AndHigherRetryCount_AndLowerCampaignMessageWeek() {
        final CampaignMessage campaignMessageWithPriority6 = new CampaignMessage("subscripitonId6", "WEEK17", "1234567890", "IDEA", DateTime.now().minusWeeks(1));
        campaignMessageWithPriority6.setStatusCode(CampaignMessageStatus.NEW);

        final CampaignMessage campaignMessageWithPriority7 = new CampaignMessage("subscripitonId7", "WEEK33", "1234567890", "IDEA", DateTime.now().minusWeeks(1));
        campaignMessageWithPriority7.setStatusCode(CampaignMessageStatus.NEW);

        final CampaignMessage campaignMessageWithPriority5 = new CampaignMessage("subscripitonId1", "WEEK12", "1234567890", "IDEA", DateTime.now().minusWeeks(1));
        campaignMessageWithPriority5.setStatusCode(CampaignMessageStatus.NEW);

        final CampaignMessage campaignMessageWithPriority4 = new CampaignMessage("subscripitonId2", "WEEK1", "1234567891", "IDEA", DateTime.now().minusWeeks(1));
        campaignMessageWithPriority4.setStatusCode(CampaignMessageStatus.NEW);

        final CampaignMessage campaignMessageWithPriority2 = new CampaignMessage("subscripitonId3", "WEEK7", "1234567893", "IDEA", DateTime.now().minusWeeks(1));
        campaignMessageWithPriority2.setStatusCode(CampaignMessageStatus.DNC);
        campaignMessageWithPriority2.markSent();
        campaignMessageWithPriority2.markSent();

        final CampaignMessage campaignMessageWithPriority3 = new CampaignMessage("subscripitonId4", "WEEK12", "1234567894", "IDEA", DateTime.now().minusWeeks(1));
        campaignMessageWithPriority3.setStatusCode(CampaignMessageStatus.DNC);
        campaignMessageWithPriority3.markSent();
        campaignMessageWithPriority3.markSent();

        final CampaignMessage campaignMessageWithPriority1 = new CampaignMessage("subscripitonId5", "WEEK6", "1234567895", "IDEA", DateTime.now().minusWeeks(1));
        campaignMessageWithPriority1.setStatusCode(CampaignMessageStatus.DNC);
        campaignMessageWithPriority1.markSent();
        campaignMessageWithPriority1.markSent();
        campaignMessageWithPriority1.markSent();


        List<CampaignMessage> campaignMessages = new ArrayList<CampaignMessage>() {{
            add(campaignMessageWithPriority2);
            add(campaignMessageWithPriority5);
            add(campaignMessageWithPriority4);
            add(campaignMessageWithPriority1);
            add(campaignMessageWithPriority3);
            add(campaignMessageWithPriority7);
            add(campaignMessageWithPriority6);
        }};

        Collections.sort(campaignMessages, new NewMessageComparator());

        assertEquals(campaignMessageWithPriority1, campaignMessages.get(0));
        assertEquals(campaignMessageWithPriority2, campaignMessages.get(1));
        assertEquals(campaignMessageWithPriority3, campaignMessages.get(2));
        assertEquals(campaignMessageWithPriority4, campaignMessages.get(3));
        assertEquals(campaignMessageWithPriority5, campaignMessages.get(4));
        assertEquals(campaignMessageWithPriority6, campaignMessages.get(5));
        assertEquals(campaignMessageWithPriority7, campaignMessages.get(6));
    }
}
