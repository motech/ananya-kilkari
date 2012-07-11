package org.motechproject.ananya.kilkari.service;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.domain.CampaignMessageAlert;
import org.motechproject.ananya.kilkari.domain.Subscription;
import org.motechproject.ananya.kilkari.messagecampaign.service.KilkariMessageCampaignService;
import org.motechproject.ananya.kilkari.repository.AllCampaignMessageAlerts;
import org.motechproject.ananya.kilkari.utils.CampaignMessageIdStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class KilkariCampaignService {

    private KilkariMessageCampaignService kilkariMessageCampaignService;
    private KilkariSubscriptionService kilkariSubscriptionService;
    private CampaignMessageIdStrategy campaignMessageIdStrategy;
    private AllCampaignMessageAlerts allCampaignMessageAlerts;
    private CampaignMessageService campaignMessageService;

    @Autowired
    public KilkariCampaignService(KilkariMessageCampaignService kilkariMessageCampaignService,
                                  KilkariSubscriptionService kilkariSubscriptionService, CampaignMessageIdStrategy campaignMessageIdStrategy, AllCampaignMessageAlerts allCampaignMessageAlerts, CampaignMessageService campaignMessageService) {
        this.kilkariMessageCampaignService = kilkariMessageCampaignService;
        this.kilkariSubscriptionService = kilkariSubscriptionService;
        this.campaignMessageIdStrategy = campaignMessageIdStrategy;
        this.allCampaignMessageAlerts = allCampaignMessageAlerts;
        this.campaignMessageService = campaignMessageService;
    }

    public Map<String, List<DateTime>> getMessageTimings(String msisdn) {
        List<Subscription> subscriptionList = kilkariSubscriptionService.findByMsisdn(msisdn);
        Map<String, List<DateTime>> campaignMessageMap = new HashMap<>();
        for (Subscription subscription : subscriptionList) {
            String subscriptionId = subscription.getSubscriptionId();

            List<DateTime> messageTimings = kilkariMessageCampaignService.getMessageTimings(
                    subscriptionId, subscription.getPack().name(),
                    subscription.getCreationDate(), subscription.endDate());

            campaignMessageMap.put(subscriptionId, messageTimings);
        }
        return campaignMessageMap;
    }

    public void scheduleWeeklyMessage(String subscriptionId) {
        Subscription subscription = kilkariSubscriptionService.findBySubscriptionId(subscriptionId);
        String messageId = campaignMessageIdStrategy.createMessageId(subscription);
        CampaignMessageAlert campaignMessageAlert = allCampaignMessageAlerts.findBySubscriptionId(subscriptionId);

        if (campaignMessageAlert == null) {
            processNewCampaignMessageAlert(subscriptionId, messageId, false);
            return;
        }

        boolean renewed = campaignMessageAlert.isRenewed();
        processExistingCampaignMessageAlert(subscriptionId, messageId, renewed, campaignMessageAlert);
    }

    public void renewSchedule(String subscriptionId) {
        CampaignMessageAlert campaignMessageAlert = allCampaignMessageAlerts.findBySubscriptionId(subscriptionId);

        if (campaignMessageAlert == null) {
            processNewCampaignMessageAlert(subscriptionId, null, true);
            return;
        }

        String messageId = campaignMessageAlert.getMessageId();
        processExistingCampaignMessageAlert(subscriptionId, messageId, true, campaignMessageAlert);
    }

    private void processExistingCampaignMessageAlert(String subscriptionId, String messageId, boolean renewed, CampaignMessageAlert campaignMessageAlert) {
        campaignMessageAlert.updateWith(messageId, renewed);

        if (!campaignMessageAlert.canBeScheduled()) {
            allCampaignMessageAlerts.update(campaignMessageAlert);
            return;
        }

        campaignMessageService.scheduleCampaignMessage(subscriptionId, messageId);
        campaignMessageAlert.clear();
        allCampaignMessageAlerts.update(campaignMessageAlert);
    }

    private void processNewCampaignMessageAlert(String subscriptionId, String messageId, boolean renew) {
        CampaignMessageAlert campaignMessageAlert = new CampaignMessageAlert(subscriptionId, messageId, renew);
        allCampaignMessageAlerts.add(campaignMessageAlert);
        return;
    }

}