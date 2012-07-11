package org.motechproject.ananya.kilkari.handlers.callback;

import org.motechproject.ananya.kilkari.request.CallbackRequestWrapper;
import org.motechproject.ananya.kilkari.service.KilkariCampaignService;
import org.motechproject.ananya.kilkari.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ActivateHandler implements SubscriptionStateHandler {
    private SubscriptionService subscriptionService;
    private KilkariCampaignService kilkariCampaignService;

    @Autowired
    public ActivateHandler(SubscriptionService subscriptionService, KilkariCampaignService kilkariCampaignService) {
        this.subscriptionService = subscriptionService;
        this.kilkariCampaignService = kilkariCampaignService;
    }

    public void perform(CallbackRequestWrapper callbackRequestWrapper) {
        String subscriptionId = callbackRequestWrapper.getSubscriptionId();
        subscriptionService.activate(subscriptionId, callbackRequestWrapper.getCreatedAt(), callbackRequestWrapper.getOperator());
        kilkariCampaignService.renewSchedule(subscriptionId);
    }
}
