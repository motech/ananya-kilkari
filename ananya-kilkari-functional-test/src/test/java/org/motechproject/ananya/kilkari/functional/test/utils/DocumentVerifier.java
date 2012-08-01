package org.motechproject.ananya.kilkari.functional.test.utils;

import org.motechproject.ananya.kilkari.functional.test.domain.SubscriptionData;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessage;
import org.motechproject.ananya.kilkari.obd.repository.AllCampaignMessages;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionStatus;
import org.motechproject.ananya.kilkari.subscription.repository.AllSubscriptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DocumentVerifier {

    @Autowired
    private AllSubscriptions allSubscriptions;

    @Autowired
    private AllCampaignMessages allCampaignMessages;

    public Subscription verifySubscriptionState(final SubscriptionData subscriptionData, final SubscriptionStatus status) {
        Subscription subscription = new TimedRunner<Subscription>(50, 6000) {
            public Subscription run() {
                Subscription subscription = getSubscription(subscriptionData);
                return subscription != null && subscription.getStatus().equals(status)? subscription : null;
            }
        }.executeWithTimeout();

        if (subscription == null)
            throw new RuntimeException(String.format("Subscription id %s not in status %s",subscriptionData.getSubscriptionId(), status));
        return subscription;

    }

    private Subscription getSubscription(SubscriptionData subscriptionData) {
        return subscriptionData.getSubscriptionId()==null ?
                allSubscriptions.findSubscriptionInProgress(subscriptionData.getMsisdn(), subscriptionData.getPack())
                : allSubscriptions.findBySubscriptionId(subscriptionData.getSubscriptionId());
    }

    public void verifyCampaignMessageExists(final SubscriptionData subscriptionData,final String weekMessageId) {
        CampaignMessage campaignMessage = new TimedRunner<CampaignMessage>(50, 6000) {
            public CampaignMessage run() {
                CampaignMessage campaignMessage = allCampaignMessages.find(subscriptionData.getSubscriptionId(),weekMessageId) ;
                return campaignMessage!=null && campaignMessage.getMessageId().equals(weekMessageId)? campaignMessage : null;
            }
        }.executeWithTimeout();

        if(campaignMessage==null)
            throw new RuntimeException(String.format("Campaign Message for subscription id - %s not in OBD db for week %s ",subscriptionData.getSubscriptionId(),weekMessageId ));
    }


}
