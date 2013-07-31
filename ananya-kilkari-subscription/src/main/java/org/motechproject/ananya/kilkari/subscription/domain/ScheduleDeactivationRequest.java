package org.motechproject.ananya.kilkari.subscription.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.joda.time.DateTime;

import java.io.Serializable;

public class ScheduleDeactivationRequest implements Serializable {
    private static final long serialVersionUID = 1345054206621372774L;
    private String subscriptionId;
    private DateTime deactivationDate;
    private String reason;
    private Integer graceCount;

    public ScheduleDeactivationRequest(String subscriptionId, DateTime deactivationDate, String reason, Integer graceCount) {
        this.subscriptionId = subscriptionId;
        this.deactivationDate = deactivationDate;
        this.reason = reason;
        this.graceCount = graceCount;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public DateTime getDeactivationDate() {
        return deactivationDate;
    }

    public String getReason() {
        return reason;
    }

    public Integer getGraceCount() {
        return graceCount;
    }

    @Override
    public boolean equals(Object that) {
        return EqualsBuilder.reflectionEquals(this, that);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
