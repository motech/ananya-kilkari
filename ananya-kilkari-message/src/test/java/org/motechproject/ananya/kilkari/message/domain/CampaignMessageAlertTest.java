package org.motechproject.ananya.kilkari.message.domain;

import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.*;

public class CampaignMessageAlertTest {

    @Test
    public void shouldConstructCampaignMessageAlert() {
        CampaignMessageAlert campaignMessageAlert = new CampaignMessageAlert("subscriptionId", "messageId", true, DateTime.now().plusWeeks(1));
        assertEquals("subscriptionId", campaignMessageAlert.getSubscriptionId());
        assertEquals("messageId", campaignMessageAlert.getMessageId());
        assertTrue(campaignMessageAlert.isRenewed());

        campaignMessageAlert = new CampaignMessageAlert("", "", false, DateTime.now().plusWeeks(1));
        assertEquals("", campaignMessageAlert.getSubscriptionId());
        assertEquals("", campaignMessageAlert.getMessageId());
        assertFalse(campaignMessageAlert.isRenewed());
    }

    @Test
    public void shouldUpdateWithMessageIdSetRenewStatusAndMessageExpiryTime() {
        CampaignMessageAlert campaignMessageAlert = new CampaignMessageAlert("subscriptionId", "messageId", true, DateTime.now().plusWeeks(1));
        String expectedMessageId = "newMessageId";
        boolean expectedRenew = false;
        DateTime expectedMessageExpiryTime = DateTime.now().minusWeeks(1);

        campaignMessageAlert.updateWith(expectedMessageId, expectedRenew, expectedMessageExpiryTime);

        assertEquals(expectedMessageId, campaignMessageAlert.getMessageId());
        assertEquals(expectedRenew, campaignMessageAlert.isRenewed());
        assertEquals(expectedMessageExpiryTime, campaignMessageAlert.getMessageExpiryDate());
    }

    @Test
    public void canBeScheduled_IfAlertRaisedAndTriggeredForActivation() {
        DateTime messageExpiryTime = DateTime.now().plusDays(1);
        CampaignMessageAlert campaignMessageAlert = new CampaignMessageAlert("subscriptionId", "messageId", true, messageExpiryTime);
        assertTrue(campaignMessageAlert.canBeScheduled(false));
    }
    
    @Test
    public void canBeScheduled_IfAlertRaisedIsExpiredAndActivated() {
        DateTime messageExpiryTime = DateTime.now().minusDays(1);
        CampaignMessageAlert campaignMessageAlert = new CampaignMessageAlert("subscriptionId", "messageId", true, messageExpiryTime);
        assertTrue(campaignMessageAlert.canBeScheduled(false));
    }

    @Test
    public void canNotBeScheduled_IfAlertIsNotRaisedAndActivated() {
        DateTime messageExpiryTime = DateTime.now().plusDays(1);
        CampaignMessageAlert campaignMessageAlert = new CampaignMessageAlert("subscriptionId", null, true, messageExpiryTime);
        assertFalse(campaignMessageAlert.canBeScheduled(false));
    }

    @Test
    public void canNotBeScheduled_IfAlertIsRaisedAndNotActivatedOrRenewed() {
        DateTime messageExpiryTime = DateTime.now().plusDays(1);
        CampaignMessageAlert campaignMessageAlert = new CampaignMessageAlert("subscriptionId", "WEEK1", false, messageExpiryTime);
        assertFalse(campaignMessageAlert.canBeScheduled(true));
    }

    @Test
    public void canNotBeScheduled_IfAlertIsRaisedAndRenewedButMessageExpired() {
        DateTime messageExpiryTime = DateTime.now().minusDays(1);
        CampaignMessageAlert campaignMessageAlert = new CampaignMessageAlert("subscriptionId", "WEEK1", true, messageExpiryTime);
        assertFalse(campaignMessageAlert.canBeScheduled(true));
    }
}
