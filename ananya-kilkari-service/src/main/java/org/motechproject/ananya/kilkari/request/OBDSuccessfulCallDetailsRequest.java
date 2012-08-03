package org.motechproject.ananya.kilkari.request;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.ananya.kilkari.reporting.domain.CampaignMessageCallSource;

public class OBDSuccessfulCallDetailsRequest extends CallDetailsRequest {
    @JsonProperty
    private String serviceOption;

    @JsonIgnore
    private String subscriptionId;

    public OBDSuccessfulCallDetailsRequest() {
        super(CampaignMessageCallSource.OBD);
    }

    public String getServiceOption() {
        return serviceOption;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setServiceOption(String serviceOption) {
        this.serviceOption = serviceOption;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OBDSuccessfulCallDetailsRequest)) return false;

        OBDSuccessfulCallDetailsRequest that = (OBDSuccessfulCallDetailsRequest) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(this.serviceOption, that.serviceOption)
                .append(this.subscriptionId, that.subscriptionId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .appendSuper(super.hashCode())
                .append(this.serviceOption)
                .append(this.subscriptionId)
                .hashCode();
    }
}
