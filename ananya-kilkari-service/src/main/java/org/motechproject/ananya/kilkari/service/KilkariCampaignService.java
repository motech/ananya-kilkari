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
    private OBDService obdService;

    @Autowired
    public KilkariCampaignService(KilkariMessageCampaignService kilkariMessageCampaignService,
                                  KilkariSubscriptionService kilkariSubscriptionService, CampaignMessageIdStrategy campaignMessageIdStrategy, AllCampaignMessageAlerts allCampaignMessageAlerts, OBDService obdService) {
        this.kilkariMessageCampaignService = kilkariMessageCampaignService;
        this.kilkariSubscriptionService = kilkariSubscriptionService;
        this.campaignMessageIdStrategy = campaignMessageIdStrategy;
        this.allCampaignMessageAlerts = allCampaignMessageAlerts;
        this.obdService = obdService;
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

        if(campaignMessageAlert == null) {
            processNewCampaignMessageAlert(subscriptionId, messageId);
            return;
        }

        processExistingCampaignMessageAlert(subscriptionId, messageId, campaignMessageAlert);
    }

    private void processExistingCampaignMessageAlert(String subscriptionId, String messageId, CampaignMessageAlert campaignMessageAlert) {
        campaignMessageAlert.setMessageId(messageId);

        if(!campaignMessageAlert.canBeScheduled()) {
            allCampaignMessageAlerts.update(campaignMessageAlert);
            return;
        }

        obdService.scheduleCampaignMessage(subscriptionId, messageId);
        allCampaignMessageAlerts.remove(campaignMessageAlert);
    }

    private void processNewCampaignMessageAlert(String subscriptionId, String messageId) {
        CampaignMessageAlert campaignMessageAlert = new CampaignMessageAlert(subscriptionId, messageId);
        allCampaignMessageAlerts.add(campaignMessageAlert);
        return;
    }

}