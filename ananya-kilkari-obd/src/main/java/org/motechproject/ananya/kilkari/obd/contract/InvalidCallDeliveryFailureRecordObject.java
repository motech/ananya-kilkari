package org.motechproject.ananya.kilkari.obd.contract;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

public class InvalidCallDeliveryFailureRecordObject  implements Serializable {
    @JsonProperty("mdn")
    private final String msisdn;
    @JsonProperty("subscriptionId")
    private final String subscriptionId;
    @JsonProperty("description")
    private final String description;

    public InvalidCallDeliveryFailureRecordObject(String msisdn, String subscriptionId, String description) {
        this.msisdn = msisdn;
        this.subscriptionId = subscriptionId;
        this.description = description;
    }

    @JsonIgnore
    public String getDescription() {
        return description;
    }

    @JsonIgnore
    public String getMsisdn() {
        return msisdn;
    }

    @JsonIgnore
    public String getSubscriptionId() {
        return subscriptionId;
    }
}
