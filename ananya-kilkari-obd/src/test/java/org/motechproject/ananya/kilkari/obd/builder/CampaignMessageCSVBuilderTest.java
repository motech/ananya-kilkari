package org.motechproject.ananya.kilkari.obd.builder;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessage;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class CampaignMessageCSVBuilderTest {
    @Test
    public void shouldReturnCsvContentForAList() {
        CampaignMessageCSVBuilder campaignMessageCSVBuilder = new CampaignMessageCSVBuilder();
        ArrayList<CampaignMessage> campaignMessages = new ArrayList<>();
        campaignMessages.add(new CampaignMessage("subscriptionId1","message1", "1234567891", "operator1", DateTime.now().plusDays(2)));
        campaignMessages.add(new CampaignMessage("subscriptionId2","message2", "1234567892", "operator2", DateTime.now().plusDays(2)));
        campaignMessages.add(new CampaignMessage("subscriptionId3","message3", "1234567893", "operator3", DateTime.now().plusDays(2)));

        String csvContent = campaignMessageCSVBuilder.getCSV(campaignMessages);

        String expectedContect = getCSVLine(1) + "\n" + getCSVLine(2) + "\n" + getCSVLine(3) + "\n";

        assertEquals(expectedContect, csvContent);
    }

    private String getCSVLine(int index) {
        return String.format("123456789%s,message%s,subscriptionId%s,operator%s", index, index, index, index);
    }
}
