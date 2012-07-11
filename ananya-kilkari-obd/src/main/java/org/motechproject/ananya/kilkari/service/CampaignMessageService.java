package org.motechproject.ananya.kilkari.service;

import org.motechproject.ananya.kilkari.builder.CampaignMessageCSVBuilder;
import org.motechproject.ananya.kilkari.domain.CampaignMessage;
import org.motechproject.ananya.kilkari.gateway.OnMobileOBDGateway;
import org.motechproject.ananya.kilkari.repository.AllCampaignMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CampaignMessageService {

    private AllCampaignMessages allCampaignMessages;
    private OnMobileOBDGateway onMobileOBDGateway;
    private CampaignMessageCSVBuilder campaignMessageCSVBuilder;

    @Autowired
    public CampaignMessageService(AllCampaignMessages allCampaignMessages, OnMobileOBDGateway onMobileOBDGateway, CampaignMessageCSVBuilder campaignMessageCSVBuilder) {
        this.allCampaignMessages = allCampaignMessages;
        this.onMobileOBDGateway = onMobileOBDGateway;
        this.campaignMessageCSVBuilder = campaignMessageCSVBuilder;
    }

    public void scheduleCampaignMessage(String subscriptionId, String messageId) {
        allCampaignMessages.add(new CampaignMessage(subscriptionId, messageId));
    }

    public void sendNewMessages() {
        List<CampaignMessage> allNewMessages = allCampaignMessages.getAllUnsentNewMessages();
        sendMessagesToOBD(allNewMessages);
    }

    public void sendRetryMessages() {
        List<CampaignMessage> allRetryMessages = allCampaignMessages.getAllUnsentRetryMessages();
        sendMessagesToOBD(allRetryMessages);
    }

    private void sendMessagesToOBD(List<CampaignMessage> messages) {
        if(messages.isEmpty())
            return;
        String campaignMessageCSVContent = campaignMessageCSVBuilder.getCSV(messages);
        onMobileOBDGateway.send(campaignMessageCSVContent);
        for (CampaignMessage message : messages) {
            message.markSent();
            allCampaignMessages.update(message);
        }
    }
}