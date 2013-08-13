package org.motechproject.ananya.kilkari.functional.test.verifiers;

import org.motechproject.ananya.kilkari.functional.test.domain.SubscriptionData;
import org.motechproject.ananya.kilkari.functional.test.utils.TimedRunner;
import org.motechproject.ananya.kilkari.functional.test.utils.TimedRunnerResponse;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionStatus;
import org.motechproject.ananya.kilkari.subscription.repository.AllSubscriptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionVerifier {


    @Autowired
    private AllSubscriptions allSubscriptions;

    public Subscription verifySubscriptionState(final SubscriptionData subscriptionData, final SubscriptionStatus status) {
        Subscription subscription = waitForSubscription(subscriptionData, status);

        if (subscription == null)
            throw new RuntimeException(String.format("Subscription id %s not in status %s",subscriptionData.getSubscriptionId(), status));
        return subscription;

    }

    private Subscription waitForSubscription(final SubscriptionData subscriptionData, final SubscriptionStatus status) {
        return new TimedRunner<Subscription>(20, 6000) {
                public TimedRunnerResponse<Subscription> run() {
                    Subscription subscription = getSubscription(subscriptionData);
                    return subscription != null && subscription.getStatus().equals(status)? new TimedRunnerResponse<>(subscription) : null;
                }
            }.executeWithTimeout();
    }

    private Subscription getSubscription(SubscriptionData subscriptionData) {
        return subscriptionData.getSubscriptionId()==null ?
                allSubscriptions.findSubscriptionInProgress(subscriptionData.getMsisdn(), subscriptionData.getPack())
                : allSubscriptions.findBySubscriptionId(subscriptionData.getSubscriptionId());
    }

}
