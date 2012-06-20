package org.motechproject.ananya.kilkari.web.response;

import org.codehaus.jackson.annotate.JsonProperty;

public class SubscriptionDetails {
    @JsonProperty
    private String subscriptionId;

    @JsonProperty
    private String pack;

    @JsonProperty
    private String status;

    public SubscriptionDetails(String subscriptionId, String pack, String status) {
        this.subscriptionId = subscriptionId;
        this.pack = pack;
        this.status = status;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public String getPack() {
        return pack;
    }

    public String getStatus() {
        return status;
    }
}
