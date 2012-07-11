package org.motechproject.ananya.kilkari.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.motechproject.ananya.kilkari.domain.CampaignMessage;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllCampaignMessages extends MotechBaseRepository<CampaignMessage> {

    @Autowired
    public AllCampaignMessages(@Qualifier("obdDbConnector") CouchDbConnector db) {
        super(CampaignMessage.class, db);
        initStandardDesignDocument();
    }

    @View(name = "all_unsent_new_messages", map = "function(doc) {if(doc.type == 'CampaignMessage' && (doc.status == 'NEW' || doc.status == 'DNC') && !doc.sent) {emit([doc.subscriptionId, doc.messageId]);}}")
    public List<CampaignMessage> getAllUnsentNewMessages() {
        return queryView("all_unsent_new_messages");
    }

    @View(name = "all_unsent_retry_messages", map = "function(doc) {if(doc.type == 'CampaignMessage' && doc.status == 'DNP' && !doc.sent) {emit([doc.subscriptionId, doc.messageId]);}}")
    public List<CampaignMessage> getAllUnsentRetryMessages() {
        return queryView("all_unsent_retry_messages");
    }
}
