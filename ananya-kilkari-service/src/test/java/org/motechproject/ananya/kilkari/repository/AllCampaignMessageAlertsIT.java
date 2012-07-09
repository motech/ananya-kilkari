package org.motechproject.ananya.kilkari.repository;

import org.junit.Test;
import org.motechproject.ananya.kilkari.domain.CampaignMessageAlert;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class AllCampaignMessageAlertsIT extends SpringIntegrationTest {

    @Autowired
    private AllCampaignMessageAlerts allCampaignMessageAlerts;

    @Test
    public void shouldReturnTheCampaignMessageAlertIfexists() {
        String subscriptionId = "subscriptionId";
        String messageId = "messageId";
        CampaignMessageAlert actualCampaignMessage = new CampaignMessageAlert(subscriptionId, messageId, true);
        kilkariDbConnector.create(actualCampaignMessage);
        markForDeletion(actualCampaignMessage);

        CampaignMessageAlert expectedCampaignMessage = allCampaignMessageAlerts.findBySubscriptionId(subscriptionId);
        assertEquals(subscriptionId, expectedCampaignMessage.getSubscriptionId());
        assertEquals(messageId, expectedCampaignMessage.getMessageId());
        assertTrue(expectedCampaignMessage.isRenewed());
    }
}
