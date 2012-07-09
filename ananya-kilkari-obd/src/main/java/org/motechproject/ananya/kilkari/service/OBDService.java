package org.motechproject.ananya.kilkari.service;

import org.motechproject.ananya.kilkari.domain.CampaignMessage;
import org.motechproject.ananya.kilkari.repository.AllCampaignMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OBDService {

    private AllCampaignMessages allCampaignMessages;

    @Autowired
    public OBDService(AllCampaignMessages allCampaignMessages) {
        this.allCampaignMessages = allCampaignMessages;
    }

    public void scheduleCampaignMessage(String subscriptionId, String messageId) {
        allCampaignMessages.add(new CampaignMessage(subscriptionId, messageId));
    }
}
