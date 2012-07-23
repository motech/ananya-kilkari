package org.motechproject.ananya.kilkari.obd.domain;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class CampaignMessageTest {

    @Test
    public void shouldConstructCampaignMessageTestWithDefaults() {
        CampaignMessage campaignMessage = new CampaignMessage();
        assertEquals(CampaignMessageStatus.NEW, campaignMessage.getStatus());
        assertFalse(campaignMessage.isSent());
    }

    @Test
    public void shouldReturn10DigitPhoneNumberWhenGivenANumber() {
        String expectedNumber = "1234567890";
        assertEquals(expectedNumber, new CampaignMessage("subscriptionId1", "messageId1", "1234567890", "operator1").getMsisdn());
        assertEquals(expectedNumber, new CampaignMessage("subscriptionId1", "messageId1", "911234567890", "operator1").getMsisdn());
        assertEquals(expectedNumber, new CampaignMessage("subscriptionId1", "messageId1", "001234567890", "operator1").getMsisdn());
    }

    @Test
    public void shouldMarkSent() {
        CampaignMessage campaignMessage = new CampaignMessage();
        campaignMessage.markSent();
        assertTrue(campaignMessage.isSent());
    }

    @Test
    public void shouldMarkDidNotPickup() {
        CampaignMessage campaignMessage = new CampaignMessage();
        campaignMessage.markSent();
        campaignMessage.markDidNotPickup();

        assertFalse(campaignMessage.isSent());
        assertEquals(CampaignMessageStatus.DNP, campaignMessage.getStatus());
    }

    @Test
    public void shouldMarkDidNotCall() {
        CampaignMessage campaignMessage = new CampaignMessage();
        campaignMessage.markSent();
        campaignMessage.markDidNotCall();

        assertFalse(campaignMessage.isSent());
        assertEquals(CampaignMessageStatus.DNC, campaignMessage.getStatus());
    }

    @Test
    public void markSentShouldIncrementRetryCountForDNP() {
        CampaignMessage campaignMessage = new CampaignMessage();
        campaignMessage.markSent();
        assertEquals(0, campaignMessage.getDnpRetryCount());
        campaignMessage.markDidNotPickup();
        campaignMessage.markSent();
        assertEquals(1, campaignMessage.getDnpRetryCount());
        campaignMessage.markDidNotPickup();
        campaignMessage.markSent();
        assertEquals(2, campaignMessage.getDnpRetryCount());
        campaignMessage.markDidNotCall();
        campaignMessage.markSent();
        assertEquals(2, campaignMessage.getDnpRetryCount());
    }

    @Test
    public void shouldSortBasedOnRetryCount() {
        CampaignMessage campaignMessage1 = new CampaignMessage();
        campaignMessage1.markSent();
        campaignMessage1.markDidNotPickup();
        campaignMessage1.markSent();
        campaignMessage1.markDidNotPickup();
        campaignMessage1.markSent();
        assertEquals(2, campaignMessage1.getDnpRetryCount());

        CampaignMessage campaignMessage2 = new CampaignMessage();
        campaignMessage2.markSent();
        campaignMessage2.markDidNotPickup();
        campaignMessage2.markSent();
        assertEquals(1, campaignMessage2.getDnpRetryCount());

        ArrayList<CampaignMessage> campaignMessages = new ArrayList<>();
        campaignMessages.add(campaignMessage2);
        campaignMessages.add(campaignMessage1);

        assertEquals(campaignMessage2, campaignMessages.get(0));
        assertEquals(campaignMessage1, campaignMessages.get(1));

        Collections.sort(campaignMessages);

        assertEquals(campaignMessage1, campaignMessages.get(0));
        assertEquals(campaignMessage2, campaignMessages.get(1));
    }
}
