package org.motechproject.ananya.kilkari.functional.test.domain;

import org.motechproject.ananya.kilkari.functional.test.verifiers.CampaignMessageVerifier;
import org.motechproject.ananya.kilkari.functional.test.verifiers.OnMobileOBDVerifier;
import org.motechproject.ananya.kilkari.obd.repository.AllCampaignMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class User {

    @Autowired
    private CampaignMessageVerifier campaignMessageVerifier;
    @Autowired
    private OnMobileOBDVerifier onMobileOBDVerifier;

    public void messageIsReady(SubscriptionData subscriptionData, String weekMessageId) {
        campaignMessageVerifier.verifyCampaignMessageExists(subscriptionData, weekMessageId);
    }

    public void messageIsNotCreated(SubscriptionData subscriptionData, String weekMessageId) {
        campaignMessageVerifier.verifyCampaignMessageIsNotCreatedAfterCampaignAlert(subscriptionData, weekMessageId);
    }

    public void messageWasDeliveredDuringFirstSlot(SubscriptionData subscriptionData, String weekMessageId) {
        onMobileOBDVerifier.verifyThatNewMessageWasDelivered(subscriptionData, weekMessageId);
    }


    public void MessageWasDeliveredDuringSecondSlot(SubscriptionData subscriptionData, String weekMessageId) {
        onMobileOBDVerifier.verifyThatRetryMessageWasDelivered(subscriptionData, weekMessageId);
    }

    public void resetOnMobileOBDVerifier() {
        onMobileOBDVerifier.reset();
    }

    public void resetCampaignMessageVerifier() {
        campaignMessageVerifier.reset();
    }
}
