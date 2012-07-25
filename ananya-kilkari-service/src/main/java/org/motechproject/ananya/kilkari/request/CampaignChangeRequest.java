package org.motechproject.ananya.kilkari.request;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;

import java.io.Serializable;

public class CampaignChangeRequest implements Serializable {

    @JsonProperty
    private String subscriptionId;
    @JsonProperty
    private String reason;
    @JsonIgnore
    private DateTime createdAt;

    public CampaignChangeRequest() {
        this.createdAt = DateTime.now();
    }

    @JsonIgnore
    public String getSubscriptionId() {
        return subscriptionId;
    }

    @JsonIgnore
    public String getReason() {
        return reason;
    }

    @JsonIgnore
    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CampaignChangeRequest)) return false;

        CampaignChangeRequest that = (CampaignChangeRequest) o;

        return new EqualsBuilder()
                .append(this.reason, that.reason)
                .append(this.subscriptionId, that.subscriptionId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(this.reason)
                .append(this.subscriptionId)
                .hashCode();
    }
}
