package org.motechproject.ananya.kilkari.domain.kilkari;

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
