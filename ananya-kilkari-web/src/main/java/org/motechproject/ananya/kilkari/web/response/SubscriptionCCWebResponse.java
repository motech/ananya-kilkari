package org.motechproject.ananya.kilkari.web.response;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "subscriber")
public class SubscriptionCCWebResponse extends SubscriptionBaseWebResponse {

    @JsonProperty
    @XmlElementWrapper(name = "subscriptionDetails")
    @XmlElement(name = "subscriptionDetail")
    private List<AllSubscriptionDetails> subscriptionDetails;

    public SubscriptionCCWebResponse() {
        this.subscriptionDetails = new ArrayList<>();
    }

    public void addSubscriptionDetail(AllSubscriptionDetails subscriptionDetail) {
        subscriptionDetails.add(subscriptionDetail);
    }

    public List<AllSubscriptionDetails> getSubscriptionDetails() {
        return subscriptionDetails;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubscriptionCCWebResponse)) return false;

        SubscriptionCCWebResponse that = (SubscriptionCCWebResponse) o;

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
