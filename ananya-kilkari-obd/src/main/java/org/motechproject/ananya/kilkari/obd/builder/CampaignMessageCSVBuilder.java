package org.motechproject.ananya.kilkari.obd.builder;

import org.motechproject.ananya.kilkari.obd.domain.CampaignMessage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CampaignMessageCSVBuilder {

    public String getCSV(List<CampaignMessage> campaignMessages) {
        StringBuilder csvContent = new StringBuilder();
        for(CampaignMessage campaignMessage  : campaignMessages){
            appendCampaignMessage(csvContent, campaignMessage);
        }
        return csvContent.toString();
    }

    private void appendCampaignMessage(StringBuilder csvContent, CampaignMessage campaignMessage) {
        csvContent
                .append(campaignMessage.getMsisdn()).append(",")
                .append(campaignMessage.getMessageId()).append(",")
                .append(campaignMessage.getSubscriptionId()).append(",")
                .append(campaignMessage.getOperator());
        csvContent.append("\n");
    }
}
