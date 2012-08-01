package org.motechproject.ananya.kilkari.obd.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.GenerateView;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessageAlert;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllCampaignMessageAlerts extends MotechBaseRepository<CampaignMessageAlert> {

    @Autowired
    public AllCampaignMessageAlerts(@Qualifier("obdDbConnector") CouchDbConnector db) {
        super(CampaignMessageAlert.class, db);
        initStandardDesignDocument();
    }

    @GenerateView
    public CampaignMessageAlert findBySubscriptionId(String subscriptionId) {
        ViewQuery viewQuery = super.createQuery("by_subscriptionId").key(subscriptionId).includeDocs(true);
        List<CampaignMessageAlert> campaignMessageAlerts = db.queryView(viewQuery, CampaignMessageAlert.class);
        return singleResult(campaignMessageAlerts);
    }
}