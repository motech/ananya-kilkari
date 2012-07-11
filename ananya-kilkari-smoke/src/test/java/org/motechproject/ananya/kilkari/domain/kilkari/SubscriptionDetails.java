package org.motechproject.ananya.kilkari.domain.kilkari;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public class SubscriptionDetails {
    @JsonProperty
    private String subscriptionId;
    @JsonProperty
    private String pack;
    @JsonProperty
    private String status;

    public SubscriptionDetails() {
    }

    public SubscriptionDetails(String subscriptionId, String pack, String status) {
        this.subscriptionId = subscriptionId;
        this.pack = pack;
        this.status = status;
    }

    @JsonIgnore
    public String getSubscriptionId() {
        return subscriptionId;
    }

    @JsonIgnore
    public String getPack() {
        return pack;
    }

    @JsonIgnore
    public String getStatus() {
        return status;
    }
}
