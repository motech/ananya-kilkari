package org.motechproject.ananya.kilkari.subscription.domain;

import java.util.ArrayList;
import java.util.List;

public class Subscriptions extends ArrayList<Subscription> {

    public Subscriptions() {
    }

    public Subscriptions(List<Subscription> subscriptionList) {
        super(subscriptionList);
    }

    public Subscription subscriptionInProgress() {
        for (Subscription subscription : this) {
            if (subscription.isInProgress())
                return subscription;
        }
        return null;
    }

    public List<Subscription> subscriptionsInProgress(){
        List<Subscription> subscriptionsInProgress = new ArrayList<>();
        for(Subscription subscription : this)
            if(subscription.isInProgress())
                subscriptionsInProgress.add(subscription);
        return subscriptionsInProgress;
    }
}
