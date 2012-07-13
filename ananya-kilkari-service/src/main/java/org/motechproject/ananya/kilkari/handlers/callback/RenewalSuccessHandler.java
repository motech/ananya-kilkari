package org.motechproject.ananya.kilkari.handlers.callback;

import org.motechproject.ananya.kilkari.request.CallbackRequestWrapper;
import org.motechproject.ananya.kilkari.service.KilkariCampaignService;
import org.motechproject.ananya.kilkari.subscription.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RenewalSuccessHandler implements SubscriptionStateHandler {
    private SubscriptionService subscriptionService;
    private KilkariCampaignService kilkariCampaignService;

    @Autowired
    public RenewalSuccessHandler(SubscriptionService subscriptionService, KilkariCampaignService kilkariCampaignService) {
        this.subscriptionService = subscriptionService;
        this.kilkariCampaignService = kilkariCampaignService;
    }

    @Override
    public void perform(CallbackRequestWrapper callbackRequestWrapper) {
        String subscriptionId = callbackRequestWrapper.getSubscriptionId();
        subscriptionService.renewSubscription(subscriptionId, callbackRequestWrapper.getCreatedAt(), callbackRequestWrapper.getGraceCount());
        kilkariCampaignService.renewSchedule(subscriptionId);
    }
}
