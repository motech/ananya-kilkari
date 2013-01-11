package org.motechproject.ananya.kilkari.test.data.contract;

import org.motechproject.ananya.kilkari.web.response.AllSubscriptionDetails;

import java.util.ArrayList;
import java.util.List;

public class SubscriberSubscriptions {
    private List<AllSubscriptionDetails> subscriptionsDetails;

    public SubscriberSubscriptions() {
        this.subscriptionsDetails = new ArrayList<>();
    }

    public List<AllSubscriptionDetails> getSubscriptionDetails() {
        return subscriptionsDetails;
    }
}
