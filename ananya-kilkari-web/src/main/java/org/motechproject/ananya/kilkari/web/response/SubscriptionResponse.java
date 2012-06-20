package org.motechproject.ananya.kilkari.web.response;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionResponse {
    @JsonProperty
    private List<SubscriptionDetails> subscriptionDetails;

    public SubscriptionResponse() {
        this.subscriptionDetails = new ArrayList<SubscriptionDetails>();
    }

    public void addSubscriptionDetail(SubscriptionDetails subscriptionDetail) {
        subscriptionDetails.add(subscriptionDetail);
    }
}
