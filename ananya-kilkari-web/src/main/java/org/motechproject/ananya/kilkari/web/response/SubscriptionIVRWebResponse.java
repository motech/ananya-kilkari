package org.motechproject.ananya.kilkari.web.response;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionIVRWebResponse {

    @JsonProperty
    private List<SubscriptionDetails> subscriptionDetails;

    public SubscriptionIVRWebResponse() {
        this.subscriptionDetails = new ArrayList<>();
    }

    public void addSubscriptionDetail(SubscriptionDetails subscriptionDetail) {
        subscriptionDetails.add(subscriptionDetail);
    }

    public List<SubscriptionDetails> getSubscriptionDetails() {
        return subscriptionDetails;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubscriptionIVRWebResponse)) return false;

        SubscriptionIVRWebResponse that = (SubscriptionIVRWebResponse) o;

        return new EqualsBuilder().append(this.subscriptionDetails, that.subscriptionDetails)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(this.subscriptionDetails).hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append(this.subscriptionDetails).toString();
    }

}
