package org.motechproject.ananya.kilkari.performance.tests.domain;

import java.util.ArrayList;
import java.util.List;

public class SubscriberResponse {

    private List<SubscriptionDetails> subscriptionDetails;

    public SubscriberResponse() {
        this.subscriptionDetails = new ArrayList<SubscriptionDetails>();
    }

    public List<SubscriptionDetails> getSubscriptionDetails() {
        return subscriptionDetails;
    }
}
