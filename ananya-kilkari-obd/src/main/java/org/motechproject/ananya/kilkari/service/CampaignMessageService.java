package org.motechproject.ananya.kilkari.service;

import org.motechproject.ananya.kilkari.domain.CampaignMessage;
import org.motechproject.ananya.kilkari.repository.AllCampaignMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CampaignMessageService {

    private AllCampaignMessages allCampaignMessages;

    @Autowired
    public CampaignMessageService(AllCampaignMessages allCampaignMessages) {
        this.allCampaignMessages = allCampaignMessages;
    }

    public void scheduleCampaignMessage(String subscriptionId, String messageId) {
        allCampaignMessages.add(new CampaignMessage(subscriptionId, messageId));
    }

    public void sendFreshMessages() {
        List<CampaignMessage> allFreshMessages = allCampaignMessages.getAllUnsentNewMessages();
        for (CampaignMessage freshMessage : allFreshMessages) {
            freshMessage.markSent();
            allCampaignMessages.update(freshMessage);
        }
    }

    public void sendRetryMessages() {
        List<CampaignMessage> allRetryMessages = allCampaignMessages.getAllUnsentRetryMessages();
        for (CampaignMessage retryMessage : allRetryMessages) {
            retryMessage.markSent();
            allCampaignMessages.update(retryMessage);
        }
    }
}