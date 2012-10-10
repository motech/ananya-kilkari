package org.motechproject.ananya.kilkari.obd.service.request;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

public class InvalidFailedCallReport implements Serializable {
    @JsonProperty("mdn")
    private String msisdn;
    @JsonProperty("subscriptionId")
    private String subscriptionId;
    @JsonProperty("description")
    private String description;

    public InvalidFailedCallReport() {
    }

    public InvalidFailedCallReport(String msisdn, String subscriptionId, String description) {
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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public boolean equals(Object other) {
        return EqualsBuilder.reflectionEquals(this, other);
    }
}
