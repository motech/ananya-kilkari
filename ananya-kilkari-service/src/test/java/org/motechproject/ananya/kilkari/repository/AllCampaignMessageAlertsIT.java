package org.motechproject.ananya.kilkari.repository;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ananya.kilkari.domain.CampaignMessageAlert;
import org.motechproject.ananya.kilkari.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class AllCampaignMessageAlertsIT extends SpringIntegrationTest {

    @Autowired
    private AllCampaignMessageAlerts allCampaignMessageAlerts;

    @Test
    public void shouldReturnTheCampaignMessageAlertIfExists() {
        String subscriptionId = "subscriptionId";
        String messageId = "messageId";
        CampaignMessageAlert actualCampaignMessage = new CampaignMessageAlert(subscriptionId, messageId, true, DateTime.now().plusWeeks(1));
        kilkariDbConnector.create(actualCampaignMessage);
        markForDeletion(actualCampaignMessage);

        CampaignMessageAlert expectedCampaignMessage = allCampaignMessageAlerts.findBySubscriptionId(subscriptionId);
        assertEquals(subscriptionId, expectedCampaignMessage.getSubscriptionId());
        assertEquals(messageId, expectedCampaignMessage.getMessageId());
        assertTrue(expectedCampaignMessage.isRenewed());
    }
}
