package org.motechproject.ananya.kilkari.obd.repository;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessage;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
public class AllCampaignMessages extends MotechBaseRepository<CampaignMessage> {

    @Autowired
    public AllCampaignMessages(@Qualifier("obdDbConnector") CouchDbConnector db) {
        super(CampaignMessage.class, db);
        initStandardDesignDocument();
    }

    @GenerateView
    public List<CampaignMessage> findBySubscriptionId(String subscriptionId) {
        return queryView("by_subscriptionId", subscriptionId);
    }

    @View(name = "all_unsent_new_messages", map = "function(doc) {if(doc.type == 'CampaignMessage' && doc.status == 'NEW' && !doc.sent) {emit([doc.weekEndingDate, doc.subscriptionId, doc.messageId]);}}")
    public List<CampaignMessage> getAllUnsentNewMessages() {
        return queryView("all_unsent_new_messages");
    }

    @View(name = "all_unsent_na_messages", map = "function(doc) {if(doc.type == 'CampaignMessage' && doc.status == 'NA' && !doc.sent) {emit([doc.weekEndingDate, doc.subscriptionId, doc.messageId]);}}")
    public List<CampaignMessage> getAllUnsentNAMessages() {
        return queryView("all_unsent_na_messages");
    }

    @View(name = "all_unsent_new_and_na_messages", map = "function(doc) {if(doc.type == 'CampaignMessage' && (doc.status == 'NEW' || doc.status == 'NA') && !doc.sent) {emit([doc.weekEndingDate, doc.subscriptionId, doc.messageId]);}}")
    public List<CampaignMessage> getAllUnsentNewAndNAMessages() {
        List<CampaignMessage> messages = queryView("all_unsent_new_and_na_messages");
        Collections.sort(messages);
        return messages;
    }

    @View(name = "all_unsent_retry_messages", map = "function(doc) {if(doc.type == 'CampaignMessage' && doc.status != 'NEW' && !doc.sent) {emit([doc.weekEndingDate, doc.subscriptionId, doc.messageId]);}}")
    public List<CampaignMessage> getAllUnsentRetryMessages() {
        List<CampaignMessage> messages = queryView("all_unsent_retry_messages");
        Collections.sort(messages);
        return messages.isEmpty() ? new ArrayList<CampaignMessage>() : messages;
    }

    @View(name = "find_by_subscriptionId_and_messageId", map = "function(doc) {if(doc.type === 'CampaignMessage') emit([doc.subscriptionId, doc.messageId]);}")
    public CampaignMessage find(String subscriptionId, String campaignId) {
        List<CampaignMessage> campaignMessages = queryView("find_by_subscriptionId_and_messageId", ComplexKey.of(subscriptionId, campaignId));
        return singleResult(campaignMessages);
    }

    public void delete(CampaignMessage campaignMessage) {
        remove(campaignMessage);
    }

    public void removeAll(String subscriptionId) {
        removeAll("subscriptionId", subscriptionId);
    }
}
