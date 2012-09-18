package org.motechproject.ananya.kilkari.messagecampaign.repository;

import org.motechproject.server.messagecampaign.dao.AllCampaignEnrollments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AllKilkariCampaignEnrollments {

    private AllCampaignEnrollments allCampaignEnrollments;

    @Autowired
    public AllKilkariCampaignEnrollments(AllCampaignEnrollments allCampaignEnrollments) {
        this.allCampaignEnrollments = allCampaignEnrollments;
    }

    public void deleteFor(String externalId) {
        allCampaignEnrollments.removeAll("externalId", externalId);
    }
}
