package org.motechproject.ananya.kilkari.performance.tests.service.db;

import org.motechproject.ananya.kilkari.obd.domain.CampaignMessage;
import org.motechproject.ananya.kilkari.obd.repository.AllCampaignMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OBDDbService {
    @Autowired
    private AllCampaignMessages allCampaignMessages;

    public void add(CampaignMessage campaignMessage) {
        allCampaignMessages.add(campaignMessage);
    }

    public List<CampaignMessage> getAll() {
        return allCampaignMessages.getAll();
    }

    public void warmIndexes() {
        for (int i = 0; i < 10; i++) {
            try {
                allCampaignMessages.findBySubscriptionId("asdasd");
            } catch (Exception e) {
                System.out.println("Exception warming indexes : " + e.toString());
            }
        }
    }
}
