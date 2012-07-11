package org.motechproject.ananya.kilkari.domain;

import org.junit.Test;

import static org.junit.Assert.*;

public class CampaignMessageTest {

    @Test
    public void shouldConstructCampaignMessageTestWithDefaults() {
        CampaignMessage campaignMessage = new CampaignMessage();
        assertEquals(CampaignMessageStatus.NEW, campaignMessage.getStatus());
        assertFalse(campaignMessage.isSent());
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
        assertEquals(0, campaignMessage.getRetryCount());
        campaignMessage.markDidNotPickup();
        campaignMessage.markSent();
        assertEquals(1, campaignMessage.getRetryCount());
        campaignMessage.markDidNotPickup();
        campaignMessage.markSent();
        assertEquals(2, campaignMessage.getRetryCount());
        campaignMessage.markDidNotCall();
        campaignMessage.markSent();
        assertEquals(2, campaignMessage.getRetryCount());
    }
}
