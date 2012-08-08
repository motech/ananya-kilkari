package org.motechproject.ananya.kilkari.obd.domain;

import org.joda.time.DateTime;
import org.junit.Test;

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
    public void shouldMarkSent() {
        CampaignMessage campaignMessage = new CampaignMessage();
        campaignMessage.markSent();
        assertTrue(campaignMessage.isSent());
    }

    @Test
    public void shouldMarkDidNotPickup() {
        CampaignMessage campaignMessage = new CampaignMessage();
        campaignMessage.markSent();
        campaignMessage.setStatusCode(CampaignMessageStatus.DNP);

        assertFalse(campaignMessage.isSent());
        assertEquals(CampaignMessageStatus.DNP, campaignMessage.getStatus());
    }

    @Test
    public void shouldMarkDidNotCall() {
        CampaignMessage campaignMessage = new CampaignMessage();
        campaignMessage.markSent();
        campaignMessage.setStatusCode(CampaignMessageStatus.DNC);

        assertFalse(campaignMessage.isSent());
        assertEquals(CampaignMessageStatus.DNC, campaignMessage.getStatus());
    }

    @Test
    public void markSentShouldIncrementRetryCountForDNP() {
        CampaignMessage campaignMessage = new CampaignMessage("subscriptionId", "WEEEK13", "1234567890", "airtel", DateTime.now().plusDays(2));
        campaignMessage.markSent();
        assertEquals(0, campaignMessage.getDnpRetryCount());

        campaignMessage.setStatusCode(CampaignMessageStatus.DNP);
        campaignMessage.markSent();
        assertEquals(1, campaignMessage.getDnpRetryCount());

        campaignMessage.setStatusCode(CampaignMessageStatus.DNP);
        campaignMessage.markSent();
        assertEquals(2, campaignMessage.getDnpRetryCount());

        campaignMessage.setStatusCode(CampaignMessageStatus.DNC);
        campaignMessage.markSent();
        assertEquals(2, campaignMessage.getDnpRetryCount());
    }

    @Test
    public void markSentShouldIncrementRetryCountForDNC() {
        CampaignMessage campaignMessage = new CampaignMessage("subscriptionId", "WEEEK13", "1234567890", "airtel", DateTime.now().minusDays(2));
        campaignMessage.markSent();
        assertEquals(0, campaignMessage.getDnpRetryCount());

        campaignMessage.setStatusCode(CampaignMessageStatus.DNC);
        campaignMessage.markSent();
        assertEquals(0, campaignMessage.getDnpRetryCount());
        assertEquals(1, campaignMessage.getDncRetryCount());

        campaignMessage.setStatusCode(CampaignMessageStatus.DNP);
        campaignMessage.markSent();
        assertEquals(1, campaignMessage.getDncRetryCount());
        assertEquals(1, campaignMessage.getDnpRetryCount());
    }

    @Test
    public void shouldNotIncrementDNCCountIfTheCurrentDateIsNotGreaterThanWeekEndingDate() {
        CampaignMessage campaignMessage = new CampaignMessage("subscriptionId", "WEEEK13", "1234567890", "airtel", DateTime.now().plusDays(2));
        campaignMessage.setStatusCode(CampaignMessageStatus.DNC);
        campaignMessage.markSent();

        assertEquals(0, campaignMessage.getDncRetryCount());
    }
}
