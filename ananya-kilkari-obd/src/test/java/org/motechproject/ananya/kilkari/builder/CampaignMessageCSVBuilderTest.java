package org.motechproject.ananya.kilkari.builder;

import org.junit.Test;
import org.motechproject.ananya.kilkari.domain.CampaignMessage;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CampaignMessageCSVBuilderTest {
    @Test
    public void shouldReturnCsvContentForAList() {
        CampaignMessageCSVBuilder campaignMessageCSVBuilder = new CampaignMessageCSVBuilder();
        ArrayList<CampaignMessage> campaignMessages = new ArrayList<>();
        campaignMessages.add(new CampaignMessage("subscriptionId1","message1", "msisdn1", "operator1"));
        campaignMessages.add(new CampaignMessage("subscriptionId2","message2", "msisdn2", "operator2"));
        campaignMessages.add(new CampaignMessage("subscriptionId3","message3", "msisdn3", "operator3"));

        String csvContent = campaignMessageCSVBuilder.getCSV(campaignMessages);

        String expectedContect = getCSVLine(1) + "\n" + getCSVLine(2) + "\n" + getCSVLine(3) + "\n";

        assertEquals(expectedContect, csvContent);

    }

    private String getCSVLine(int index) {
        return String.format("msisdn%s,message%s,subscriptionId%s,operator%s", index, index, index, index);
    }
}
