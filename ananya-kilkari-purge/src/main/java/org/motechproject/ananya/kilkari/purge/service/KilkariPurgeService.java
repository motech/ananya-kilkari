package org.motechproject.ananya.kilkari.purge.service;

import org.motechproject.ananya.kilkari.message.repository.AllCampaignMessageAlerts;
import org.motechproject.ananya.kilkari.message.repository.AllInboxMessages;
import org.motechproject.ananya.kilkari.messagecampaign.repository.AllKilkariCampaignEnrollments;
import org.motechproject.ananya.kilkari.obd.repository.AllCampaignMessages;
import org.motechproject.ananya.kilkari.obd.repository.AllInvalidCallRecords;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.repository.AllSubscriberCareDocs;
import org.motechproject.ananya.kilkari.subscription.repository.AllSubscriptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KilkariPurgeService {
    private AllCampaignMessageAlerts allCampaignMessageAlerts;
    private AllSubscriptions allSubscriptions;
    private AllInboxMessages allInboxMessages;
    private AllCampaignMessages allCampaignMessages;
    private AllSubscriberCareDocs allSubscriberCareDocs;
    private AllInvalidCallRecords allInvalidCallRecords;
    private AllKilkariCampaignEnrollments allKilkariCampaignEnrollments;
    private final Logger logger = LoggerFactory.getLogger(KilkariPurgeService.class);

    @Autowired
    public KilkariPurgeService(AllCampaignMessageAlerts allCampaignMessageAlerts,
                               AllSubscriptions allSubscriptions,
                               AllInboxMessages allInboxMessages,
                               AllCampaignMessages allCampaignMessages,
                               AllSubscriberCareDocs allSubscriberCareDocs,
                               AllInvalidCallRecords allInvalidCallRecords,
                               AllKilkariCampaignEnrollments allKilkariCampaignEnrollments) {
        this.allCampaignMessageAlerts = allCampaignMessageAlerts;
        this.allSubscriptions = allSubscriptions;
        this.allInboxMessages = allInboxMessages;
        this.allCampaignMessages = allCampaignMessages;
        this.allSubscriberCareDocs = allSubscriberCareDocs;
        this.allInvalidCallRecords = allInvalidCallRecords;
        this.allKilkariCampaignEnrollments = allKilkariCampaignEnrollments;
    }

    public void purge(String msisdn) {
        logger.info("Started purging kilkari records for msisdn: " + msisdn);
        deleteByMsisdn(msisdn);
        List<Subscription> subscriptionList = allSubscriptions.findByMsisdn(msisdn);
        if (subscriptionList.isEmpty()) {
            logger.info(String.format("[CouchDB Purger] No subscription found for msisdn: %s", msisdn));
            return;
        }
        deleteBySubscriptionId(subscriptionList);
        logger.info("[CouchDB Purger] Finished purging kilkari records for msisdn: " + msisdn);
    }

    private void deleteByMsisdn(String msisdn) {
        logger.info(String.format("[CouchDB Purger] Deleting SubscriberCareDocs based on msisdn for: %s", msisdn));
        allSubscriberCareDocs.deleteFor(msisdn);
    }

    private void deleteBySubscriptionId(List<Subscription> subscriptionList) {
        for (Subscription subscription : subscriptionList) {
            String subscriptionId = subscription.getSubscriptionId();
            String baseLogMessage = String.format("[CouchDB Purger] Deleting %s for subscriptionId: %s, msisdn: %s", "%s",subscriptionId, subscription.getMsisdn());
            logger.info(String.format(baseLogMessage,"Campaign Message Alert"));
            allCampaignMessageAlerts.deleteFor(subscriptionId);
            logger.info(String.format(baseLogMessage,"Inbox Message"));
            allInboxMessages.deleteFor(subscriptionId);
            logger.info(String.format(baseLogMessage,"Obd Campaign Message"));
            allCampaignMessages.removeAll(subscriptionId);
            logger.info(String.format(baseLogMessage,"Invalid Call record"));
            allInvalidCallRecords.deleteFor(subscriptionId);
            logger.info(String.format(baseLogMessage,"Campaign Enrollment"));
            allKilkariCampaignEnrollments.deleteFor(subscriptionId);
            logger.info(String.format(baseLogMessage,"Subscription"));
            allSubscriptions.deleteFor(subscriptionId);
        }
    }
}
