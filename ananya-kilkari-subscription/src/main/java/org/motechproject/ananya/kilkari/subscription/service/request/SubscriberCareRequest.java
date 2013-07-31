package org.motechproject.ananya.kilkari.subscription.service.request;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.joda.time.DateTime;

import java.io.Serializable;

public class SubscriberCareRequest implements Serializable {
    private static final long serialVersionUID = 8394921863732255779L;
    private String msisdn;
    private String reason;
    private String channel;
    private DateTime createdAt;

    public SubscriberCareRequest(String msisdn, String reason, String channel, DateTime createdAt) {
        this.msisdn = msisdn;
        this.reason = reason;
        this.channel = channel;
        this.createdAt = createdAt;
    }

    public String getReason() {
        return reason;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public String getChannel() {
        return channel;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubscriberCareRequest)) return false;

        SubscriberCareRequest that = (SubscriberCareRequest) o;

        return new EqualsBuilder().append(this.msisdn, that.msisdn)
                .append(this.reason, that.reason)
                .append(this.channel, that.channel)
                .isEquals();

    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(this.msisdn)
                .append(this.reason)
                .append(this.channel)
                .hashCode();
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("msisdn", this.msisdn)
                .append("channel", this.channel)
                .append("reason", this.reason)
                .toString();
    }
}
