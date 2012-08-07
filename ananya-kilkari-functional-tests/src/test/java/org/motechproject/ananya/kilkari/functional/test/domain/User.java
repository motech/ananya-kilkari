package org.motechproject.ananya.kilkari.functional.test.domain;

import org.motechproject.ananya.kilkari.functional.test.verifiers.CampaignMessageVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class User {

    @Autowired
    private CampaignMessageVerifier campaignMessageVerifier;

    public void messageIsReady(SubscriptionData subscriptionData, String weekMessageId) {
        campaignMessageVerifier.verifyCampaignMessageExists(subscriptionData, weekMessageId);
    }

    public void messageIsNotReady(SubscriptionData subscriptionData, String weekMessageId) {
        campaignMessageVerifier.verifyCampaignMessageDoesNotExists(subscriptionData, weekMessageId);
    }
}
