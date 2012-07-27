package org.motechproject.ananya.kilkari.functional.test;

import org.motechproject.ananya.kilkari.functional.test.utils.TimedRunner;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionStatus;
import org.motechproject.ananya.kilkari.subscription.repository.AllSubscriptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionStatusVerifier {

    @Autowired
    private AllSubscriptions allSubscriptions;

    public Subscription verify(final SubscriptionData subscriptionData, final SubscriptionStatus status){
        Subscription subscription = new TimedRunner<Subscription>(3, 2000) {
            public Subscription run() {
                Subscription subscriptionInProgress = allSubscriptions.findSubscriptionInProgress(subscriptionData.getMsisdn(), SubscriptionPack.from(subscriptionData.getPack()));
                return subscriptionInProgress==null? null : getSubscriptionInStatus(subscriptionInProgress, status);
            }
        }.executeWithTimeout();

        if(subscription==null)
            throw new RuntimeException("Subscription not in " + status );
        return subscription;
        
    }

    private Subscription getSubscriptionInStatus(Subscription subscriptionInProgress, SubscriptionStatus status) {
        return subscriptionInProgress.getStatus().equals(status)?subscriptionInProgress:null;
    }


}
