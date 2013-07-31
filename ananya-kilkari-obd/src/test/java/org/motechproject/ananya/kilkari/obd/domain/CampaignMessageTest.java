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
    public void shouldMarkNotAnswered() {
        CampaignMessage campaignMessage = new CampaignMessage();
        campaignMessage.markSent();

        campaignMessage.setFailureStatusCode(CampaignMessageStatus.NA);

        assertFalse(campaignMessage.isSent());
        assertEquals(CampaignMessageStatus.NA, campaignMessage.getStatus());
    }

    @Test
    public void shouldMarkNotDelivered() {
        CampaignMessage campaignMessage = new CampaignMessage();
        campaignMessage.markSent();

        campaignMessage.setFailureStatusCode(CampaignMessageStatus.ND);

        assertFalse(campaignMessage.isSent());
        assertEquals(CampaignMessageStatus.ND, campaignMessage.getStatus());
    }

    @Test
    public void shouldMarkSwitchedOff() {
        CampaignMessage campaignMessage = new CampaignMessage();
        campaignMessage.markSent();

        campaignMessage.setFailureStatusCode(CampaignMessageStatus.SO);

        assertFalse(campaignMessage.isSent());
        assertEquals(CampaignMessageStatus.SO, campaignMessage.getStatus());
    }

    @Test
    public void markSentShouldIncrementRetryCountForNA() {
        CampaignMessage campaignMessage = new CampaignMessage("subscriptionId", "WEEEK13", DateTime.now(), "1234567890", "airtel", DateTime.now().plusDays(2));
        campaignMessage.markSent();
        assertEquals(0, campaignMessage.getNARetryCount());

        campaignMessage.setFailureStatusCode(CampaignMessageStatus.NA);
        campaignMessage.markSent();
        assertEquals(1, campaignMessage.getNARetryCount());

        campaignMessage.setFailureStatusCode(CampaignMessageStatus.NA);
        campaignMessage.markSent();
        assertEquals(2, campaignMessage.getNARetryCount());

        campaignMessage.setFailureStatusCode(CampaignMessageStatus.ND);
        campaignMessage.markSent();
        assertEquals(2, campaignMessage.getNARetryCount());
    }

    @Test
    public void markSentShouldIncrementRetryCountForND() {
        CampaignMessage campaignMessage = new CampaignMessage("subscriptionId", "WEEEK13", DateTime.now(), "1234567890", "airtel", DateTime.now().minusDays(2));
        campaignMessage.markSent();
        assertEquals(0, campaignMessage.getNARetryCount());

        campaignMessage.setFailureStatusCode(CampaignMessageStatus.ND);
        campaignMessage.markSent();
        assertEquals(0, campaignMessage.getNARetryCount());
        assertEquals(1, campaignMessage.getNDRetryCount());

        campaignMessage.setFailureStatusCode(CampaignMessageStatus.NA);
        campaignMessage.markSent();
        assertEquals(1, campaignMessage.getNDRetryCount());
        assertEquals(1, campaignMessage.getNARetryCount());
    }

    @Test
    public void markSentShouldIncrementRetryCountForSO() {
        CampaignMessage campaignMessage = new CampaignMessage("subscriptionId", "WEEEK13", DateTime.now(), "1234567890", "airtel", DateTime.now().plusDays(2));
        campaignMessage.markSent();
        assertEquals(0, campaignMessage.getSORetryCount());

        campaignMessage.setFailureStatusCode(CampaignMessageStatus.SO);
        campaignMessage.markSent();
        assertEquals(1, campaignMessage.getSORetryCount());

        campaignMessage.setFailureStatusCode(CampaignMessageStatus.SO);
        campaignMessage.markSent();
        assertEquals(2, campaignMessage.getSORetryCount());

        campaignMessage.setFailureStatusCode(CampaignMessageStatus.ND);
        campaignMessage.markSent();
        assertEquals(2, campaignMessage.getSORetryCount());
    }

    @Test
    public void shouldGetRetryCountForNAStatus(){
        CampaignMessage campaignMessage = new CampaignMessage("subscriptionId", "WEEEK13", DateTime.now(), "1234567890", "airtel", DateTime.now());
        campaignMessage.setFailureStatusCode(CampaignMessageStatus.NA);
        campaignMessage.markSent();

        assertEquals(1, campaignMessage.getRetryCountForCurrentStatus());
    }

    @Test
    public void shouldGetRetryCountForNDStatus(){
        CampaignMessage campaignMessage = new CampaignMessage("subscriptionId", "WEEEK13", DateTime.now(), "1234567890", "airtel", DateTime.now().minusWeeks(2));
        campaignMessage.setFailureStatusCode(CampaignMessageStatus.ND);
        campaignMessage.markSent();

        assertEquals(1, campaignMessage.getRetryCountForCurrentStatus());
    }

    @Test
    public void shouldGetRetryCountForSOStatus(){
        CampaignMessage campaignMessage = new CampaignMessage("subscriptionId", "WEEEK13", DateTime.now(), "1234567890", "airtel", DateTime.now());
        campaignMessage.setFailureStatusCode(CampaignMessageStatus.SO);
        campaignMessage.markSent();

        assertEquals(1, campaignMessage.getRetryCountForCurrentStatus());
    }
}
