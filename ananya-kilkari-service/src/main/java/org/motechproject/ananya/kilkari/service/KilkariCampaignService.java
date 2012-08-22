package org.motechproject.ananya.kilkari.service;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.message.service.CampaignMessageAlertService;
import org.motechproject.ananya.kilkari.message.service.InboxService;
import org.motechproject.ananya.kilkari.messagecampaign.service.MessageCampaignService;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.utils.CampaignMessageIdStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class KilkariCampaignService {

    private MessageCampaignService messageCampaignService;
    private KilkariSubscriptionService kilkariSubscriptionService;
    private CampaignMessageAlertService campaignMessageAlertService;
    private InboxService inboxService;

    private final Logger logger = LoggerFactory.getLogger(KilkariCampaignService.class);

    KilkariCampaignService() {
    }

    @Autowired
    public KilkariCampaignService(MessageCampaignService messageCampaignService,
                                  KilkariSubscriptionService kilkariSubscriptionService,
                                  CampaignMessageAlertService campaignMessageAlertService,
                                  InboxService inboxService) {
        this.messageCampaignService = messageCampaignService;
        this.kilkariSubscriptionService = kilkariSubscriptionService;
        this.campaignMessageAlertService = campaignMessageAlertService;
        this.inboxService = inboxService;
    }

    public Map<String, List<DateTime>> getMessageTimings(String msisdn) {
        List<Subscription> subscriptionList = kilkariSubscriptionService.findByMsisdn(msisdn);
        Map<String, List<DateTime>> campaignMessageMap = new HashMap<>();
        for (Subscription subscription : subscriptionList) {
            String subscriptionId = subscription.getSubscriptionId();

            try {
                List<DateTime> messageTimings = messageCampaignService.getMessageTimings(
                        subscriptionId, subscription.getCreationDate(), subscription.endDate());
                campaignMessageMap.put(subscriptionId, messageTimings);
            } catch (NullPointerException ne) {
                //ignore
            }
        }
        return campaignMessageMap;
    }

    public void scheduleWeeklyMessage(String subscriptionId, String campaignName) {
        Subscription subscription = kilkariSubscriptionService.findBySubscriptionId(subscriptionId);

        final String messageId = new CampaignMessageIdStrategy().createMessageId(campaignName, messageCampaignService.getCampaignStartDate(subscriptionId, campaignName), subscription.getPack());
        final DateTime messageExpiryDate = subscription.getCurrentWeeksMessageExpiryDate();

        campaignMessageAlertService.scheduleCampaignMessageAlert(subscriptionId, messageId, messageExpiryDate, subscription.getMsisdn(), subscription.getOperator().name());

        if (subscription.hasBeenActivated())
            inboxService.newMessage(subscriptionId, messageId);
    }

    public void processCampaignCompletion(String subscriptionId) {
        Subscription subscription = kilkariSubscriptionService.findBySubscriptionId(subscriptionId);
        if (!subscription.isInDeactivatedState())
            kilkariSubscriptionService.processSubscriptionCompletion(subscription);
    }
}