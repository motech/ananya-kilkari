package org.motechproject.ananya.kilkari.messagecampaign.repository;

import org.ektorp.CouchDbConnector;
import org.junit.Test;
import org.motechproject.server.messagecampaign.dao.AllCampaignEnrollments;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollment;
import org.motechproject.testing.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.assertTrue;

@ContextConfiguration("classpath:applicationKilkariMessageCampaignContext.xml")
@ActiveProfiles("test")
public class AllKilkariCampaignEnrollmentsTest extends SpringIntegrationTest{
    @Autowired
    private AllKilkariCampaignEnrollments allKilkariCampaignEnrollments;
    @Autowired
    private AllCampaignEnrollments allCampaignEnrollments;

    @Qualifier("messageCampaignDBConnector")
    @Autowired
    protected CouchDbConnector messageCampaignDbConnector;

    @Override
    public CouchDbConnector getDBConnector() {
        return messageCampaignDbConnector;
    }

    @Test
    public void shouldDeleteAllCampaignEnrollmentsForExternalId() {
        String externalId = "123456";
        CampaignEnrollment campaignEnrollment = new CampaignEnrollment(externalId, "campaignName");
        allCampaignEnrollments.add(campaignEnrollment);

        allKilkariCampaignEnrollments.deleteFor(externalId);

        assertTrue(allCampaignEnrollments.findByExternalId(externalId).isEmpty());
    }
}
