package org.motechproject.ananya.kilkari.message.repository;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.motechproject.ananya.kilkari.message.domain.CampaignMessageAlert;
import org.motechproject.ananya.kilkari.message.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.*;

public class AllCampaignMessageAlertsIT extends SpringIntegrationTest {

    @Autowired
    private AllCampaignMessageAlerts allCampaignMessageAlerts;

    @Test
    public void shouldReturnTheCampaignMessageAlertIfExists() {
        String subscriptionId = "subscriptionId";
        String messageId = "messageId";
        DateTime messageExpiryDate = DateTime.now().plusWeeks(1);
        CampaignMessageAlert actualCampaignMessage = new CampaignMessageAlert(subscriptionId, messageId, true, messageExpiryDate);
        messageDbConnector.create(actualCampaignMessage);
        markForDeletion(actualCampaignMessage);

        CampaignMessageAlert expectedCampaignMessage = allCampaignMessageAlerts.findBySubscriptionId(subscriptionId);
        assertEquals(subscriptionId, expectedCampaignMessage.getSubscriptionId());
        assertEquals(messageId, expectedCampaignMessage.getMessageId());
        assertEquals(messageExpiryDate.withZone(DateTimeZone.UTC), expectedCampaignMessage.getMessageExpiryDate());
        assertTrue(expectedCampaignMessage.isRenewed());
    }

    @Test
    public void shouldDeleteExistingCampaignMessageAlert(){
        String subscriptionId = "subscriptionId";
        CampaignMessageAlert campaignMessageAlert = new CampaignMessageAlert(subscriptionId, "messageId", false, DateTime.now().plusWeeks(1));
        messageDbConnector.create(campaignMessageAlert);

        allCampaignMessageAlerts.deleteFor(subscriptionId);

        assertNull(allCampaignMessageAlerts.findBySubscriptionId(subscriptionId));
    }

    @Test
    public void shouldNotDeleteACampaignMessageAlertIfItDoesNotExist(){
        String subscriptionId = "abcd1234";

        allCampaignMessageAlerts.deleteFor(subscriptionId);
    }
}
