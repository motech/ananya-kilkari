package org.motechproject.ananya.kilkari.service;

import org.junit.Test;
import org.motechproject.ananya.kilkari.domain.CampaignMessage;
import org.motechproject.ananya.kilkari.repository.AllCampaignMessages;
import org.motechproject.ananya.kilkari.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertNotNull;

public class CampaignMessageServiceIT extends SpringIntegrationTest {

    @Autowired
    private CampaignMessageService campaignMessageService;

    @Autowired
    private AllCampaignMessages allCampaignMessages;

    @Test
    public void shouldSaveTheCampaignMessageToDB() {
        String subscriptionId = "subscriptionId";
        String messageId = "messageId";

        campaignMessageService.scheduleCampaignMessage(subscriptionId, messageId);

        CampaignMessage campaignMessage = findCampaignMessageFor(subscriptionId, messageId);
        assertNotNull(campaignMessage);
        markForDeletion(campaignMessage);
    }

    private CampaignMessage findCampaignMessageFor(String subscriptionId, String messageId) {
        List<CampaignMessage> campaignMessages = allCampaignMessages.getAll();
        for(CampaignMessage campaignMessage: campaignMessages) {
            if(messageId.equals(campaignMessage.getMessageId()) && subscriptionId.equals(campaignMessage.getSubscriptionId())) {
                return campaignMessage;
            }
        }

        return null;
    }
}
