package org.motechproject.ananya.kilkari.message.service;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.message.domain.AlertTriggerType;
import org.motechproject.ananya.kilkari.message.domain.CampaignMessageAlert;
import org.motechproject.ananya.kilkari.message.repository.AllCampaignMessageAlerts;
import org.motechproject.ananya.kilkari.obd.service.CampaignMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CampaignMessageAlertService {

    private AllCampaignMessageAlerts allCampaignMessageAlerts;
    private CampaignMessageService campaignMessageService;
    private final Logger logger = LoggerFactory.getLogger(CampaignMessageAlertService.class);

    @Autowired
    public CampaignMessageAlertService(AllCampaignMessageAlerts allCampaignMessageAlerts, CampaignMessageService campaignMessageService) {
        this.allCampaignMessageAlerts = allCampaignMessageAlerts;
        this.campaignMessageService = campaignMessageService;
    }

    public String scheduleCampaignMessageAlertForActivation(String subscriptionId, String msisdn, String operator) {
        return scheduleCampaignMessageAlert(subscriptionId, msisdn, operator, AlertTriggerType.ACTIVATION);
    }

    public String scheduleCampaignMessageAlertForRenewal(String subscriptionId, String msisdn, String operator) {
        return scheduleCampaignMessageAlert(subscriptionId, msisdn, operator, AlertTriggerType.RENEWAL);
    }

    public void scheduleCampaignMessageAlert(String subscriptionId, final String messageId, final DateTime messageExpiryDate, String msisdn, String operator) {
        CampaignMessageAlertUpdater updater = new CampaignMessageAlertUpdater() {
            @Override
            public void update(CampaignMessageAlert existingCampaignMessageAlert) {
                existingCampaignMessageAlert.updateWith(messageId, existingCampaignMessageAlert.isRenewed(), messageExpiryDate);
            }
        };

        logger.info(String.format("Processing weekly message alert for subscriptionId: %s, messageId: %s", subscriptionId, messageId));

        processCampaignMessageAlert(subscriptionId, updater, AlertTriggerType.WEEKLY_MESSAGE, msisdn, operator);
    }

    public void clearMessageId(String subscriptionId) {
        CampaignMessageAlert campaignMessageAlert = allCampaignMessageAlerts.findBySubscriptionId(subscriptionId);
        if(campaignMessageAlert != null) {
            campaignMessageAlert.clearMessageId();
            allCampaignMessageAlerts.update(campaignMessageAlert);
        }
    }

    private String scheduleCampaignMessageAlert(String subscriptionId, String msisdn, String operator, AlertTriggerType alertTriggerType) {
        CampaignMessageAlertUpdater updater = new CampaignMessageAlertUpdater() {
            @Override
            public void update(CampaignMessageAlert campaignMessageAlert) {
                campaignMessageAlert.updateWith(campaignMessageAlert.getMessageId(), true, campaignMessageAlert.getMessageExpiryDate());
            }
        };

        return processCampaignMessageAlert(subscriptionId, updater, alertTriggerType, msisdn, operator);
    }

    private String processCampaignMessageAlert(String subscriptionId, CampaignMessageAlertUpdater updater, AlertTriggerType alertTriggerType, String msisdn, String operator) {
        CampaignMessageAlert campaignMessageAlert = allCampaignMessageAlerts.findBySubscriptionId(subscriptionId);
        if (campaignMessageAlert == null) {
            logger.info(String.format("Could not find campaign message alert for subscriptionId: %s", subscriptionId));
            return processNewCampaignMessageAlert(subscriptionId, updater);
        }

        return processExistingCampaignMessageAlert(campaignMessageAlert, updater, alertTriggerType, msisdn, operator);
    }

    private String processNewCampaignMessageAlert(String subscriptionId, CampaignMessageAlertUpdater updater) {
        CampaignMessageAlert campaignMessageAlert = new CampaignMessageAlert(subscriptionId, null, false, null);
        updater.update(campaignMessageAlert);
        logger.info(String.format("Creating a new record for campaign message alert: %s", campaignMessageAlert));
        allCampaignMessageAlerts.add(campaignMessageAlert);
        return campaignMessageAlert.getMessageId();
    }

    private String processExistingCampaignMessageAlert(CampaignMessageAlert campaignMessageAlert,
                                                       CampaignMessageAlertUpdater updater, AlertTriggerType alertTriggerType, String msisdn, String operator) {
        logger.info(String.format("Found campaign message alert: %s", campaignMessageAlert));
        updater.update(campaignMessageAlert);
        logger.info(String.format("Updated campaign message alert: %s", campaignMessageAlert));

        if (!campaignMessageAlert.canBeScheduled(!alertTriggerType.isActivation())) {
            logger.info("Campaign message alert can not be scheduled. Saving it.");
            allCampaignMessageAlerts.update(campaignMessageAlert);
            return campaignMessageAlert.getMessageId();
        }

        logger.info("Campaign message alert can be scheduled. Scheduling and deleting it.");
        campaignMessageService.scheduleCampaignMessage(campaignMessageAlert.getSubscriptionId(), campaignMessageAlert.getMessageId(), msisdn, operator, campaignMessageAlert.getMessageExpiryDate());
        allCampaignMessageAlerts.remove(campaignMessageAlert);
        return campaignMessageAlert.getMessageId();
    }

    public void deleteFor(String subscriptionId) {
        allCampaignMessageAlerts.deleteFor(subscriptionId);
    }

    public CampaignMessageAlert findBy(String subscriptionId) {
        return allCampaignMessageAlerts.findBySubscriptionId(subscriptionId);
    }

    private interface CampaignMessageAlertUpdater {
        public void update(CampaignMessageAlert campaignMessageAlert);
    }
}
