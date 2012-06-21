package org.motechproject.ananya.kilkari.web.response;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class SubscriberResponse extends BaseResponse {
    @JsonProperty
    private List<SubscriptionDetails> subscriptionDetails;

    public SubscriberResponse() {
        super("SUCCESS", "Subscriber details successfully fetched");
        this.subscriptionDetails = new ArrayList<SubscriptionDetails>();
    }

    public void addSubscriptionDetail(SubscriptionDetails subscriptionDetail) {
        subscriptionDetails.add(subscriptionDetail);
    }

    public SubscriberResponse forInvalidMsisdn() {
        status = "ERROR";
        description = "Invalid Msisdn";
        return this;
    }

    public List<SubscriptionDetails> getSubscriptionDetails() {
        return subscriptionDetails;
    }
}
