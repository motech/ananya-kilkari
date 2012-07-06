package org.motechproject.ananya.kilkari.service;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.domain.Subscription;
import org.motechproject.ananya.kilkari.messagecampaign.service.KilkariMessageCampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class KilkariCampaignService {

    public static final String SEVEN_MONTH_CAMPAIGN_NAME = "kilkari-mother-child-campaign-seven-months";
    public static final String TWELVE_MONTH_CAMPAIGN_NAME = "kilkari-mother-child-campaign-twelve-months";
    public static final String FIFTEEN_MONTH_CAMPAIGN_NAME = "kilkari-mother-child-campaign-fifteen-months";

    private KilkariMessageCampaignService kilkariMessageCampaignService;
    private KilkariSubscriptionService kilkariSubscriptionService;

    @Autowired
    public KilkariCampaignService(KilkariMessageCampaignService kilkariMessageCampaignService, 
                                  KilkariSubscriptionService kilkariSubscriptionService) {
        this.kilkariMessageCampaignService = kilkariMessageCampaignService;
        this.kilkariSubscriptionService = kilkariSubscriptionService;
    }

    public Map<String, List<DateTime>> getMessageTimings(String msisdn) {
        List<Subscription> subscriptionList = kilkariSubscriptionService.getSubscriptionsFor(msisdn);
        Map<String, List<DateTime>> campaignMessageMap = new HashMap<>();
        for(Subscription subscription : subscriptionList){
            String subscriptionId = subscription.getSubscriptionId();

            List<DateTime> messageTimings = kilkariMessageCampaignService.getMessageTimings(
                    subscriptionId, subscription.getPack().name(),
                    subscription.getCreationDate(), subscription.endDate());

            campaignMessageMap.put(subscriptionId, messageTimings);
        }
        return campaignMessageMap;
    }
}
