package org.motechproject.ananya.kilkari.web.response;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SubscriptionDetails {
    @JsonProperty
    @XmlElement
    private String subscriptionId;

    @JsonProperty
    @XmlElement
    private String pack;

    @JsonProperty
    @XmlElement
    private String status;

    @JsonProperty
    @XmlElement
    private String lastCampaignId;

    public SubscriptionDetails() {
    }

    public SubscriptionDetails(String subscriptionId, String pack, String status, String lastCampaignId) {
        this.subscriptionId = subscriptionId;
        this.pack = pack;
        this.status = status;
        this.lastCampaignId = lastCampaignId;
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

    @JsonIgnore
    public String getLastCampaignId() {
        return lastCampaignId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubscriptionDetails)) return false;

        SubscriptionDetails that = (SubscriptionDetails) o;

        return new EqualsBuilder().append(this.subscriptionId, that.subscriptionId)
                .append(this.pack, that.pack)
                .append(this.status, that.status)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(this.subscriptionId)
                .append(this.pack)
                .append(this.status).hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append(subscriptionId)
                .append(pack)
                .append(status).toString();
    }
}
