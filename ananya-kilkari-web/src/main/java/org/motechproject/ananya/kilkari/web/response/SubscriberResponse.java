package org.motechproject.ananya.kilkari.web.response;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class SubscriberResponse {
    @JsonProperty
    private List<SubscriptionDetails> subscriptionDetails;

    @JsonProperty
    private String status;

    @JsonProperty
    private String description;

    public SubscriberResponse() {
        this.subscriptionDetails = new ArrayList<SubscriptionDetails>();
        this.status = "SUCCESS";
        this.description = "Subscriber details successfully fetched";
    }

    public void addSubscriptionDetail(SubscriptionDetails subscriptionDetail) {
        subscriptionDetails.add(subscriptionDetail);
    }

    public void forInvalidMsisdn() {
        status = "ERROR";
        description = "Invalid Msisdn";
    }
}
