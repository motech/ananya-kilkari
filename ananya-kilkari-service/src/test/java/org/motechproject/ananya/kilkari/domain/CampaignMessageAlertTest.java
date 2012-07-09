package org.motechproject.ananya.kilkari.domain;

import org.junit.Test;

import static org.junit.Assert.*;

public class CampaignMessageAlertTest {

    @Test
    public void shouldConstructCampaignMessageAlert() {
        CampaignMessageAlert campaignMessageAlert = new CampaignMessageAlert("subscriptionId", "messageId", true);
        assertEquals("subscriptionId", campaignMessageAlert.getSubscriptionId());
        assertEquals("messageId", campaignMessageAlert.getMessageId());
        assertTrue(campaignMessageAlert.isRenewed());

        campaignMessageAlert = new CampaignMessageAlert("", "", false);
        assertEquals("", campaignMessageAlert.getSubscriptionId());
        assertEquals("", campaignMessageAlert.getMessageId());
        assertFalse(campaignMessageAlert.isRenewed());

        campaignMessageAlert = new CampaignMessageAlert(null, null);
        assertNull(campaignMessageAlert.getSubscriptionId());
        assertNull(campaignMessageAlert.getMessageId());
        assertFalse(campaignMessageAlert.isRenewed());
    }

    @Test
    public void shouldBeScheduledIfMessageIdIsPresentAndRenewed() {
        CampaignMessageAlert campaignMessageAlert = new CampaignMessageAlert("subscriptionId", "messageId", true);
        assertTrue(campaignMessageAlert.canBeScheduled());
    }

    @Test
    public void shouldNotBeScheduledIfNotRenewed() {
        CampaignMessageAlert campaignMessageAlert = new CampaignMessageAlert("subscriptionId", "messageId", false);
        assertFalse(campaignMessageAlert.canBeScheduled());
    }

    @Test
    public void shouldNotBeScheduledIfMessageIdIsNotPresent() {
        CampaignMessageAlert campaignMessageAlert = new CampaignMessageAlert("subscriptionId", "", true);
        assertFalse(campaignMessageAlert.canBeScheduled());

        campaignMessageAlert = new CampaignMessageAlert("subscriptionId", null, true);
        assertFalse(campaignMessageAlert.canBeScheduled());
    }
}
