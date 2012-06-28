package org.motechproject.ananya.kilkari.domain;

import org.joda.time.DateTime;

import java.io.Serializable;

public class BaseReportRequest implements Serializable {

    private String subscriptionId;

    private DateTime createdAt;

    private String subscriptionStatus;

    public BaseReportRequest(String subscriptionId, String subscriptionStatus, DateTime createdAt) {
        this.subscriptionId = subscriptionId;
        this.subscriptionStatus = subscriptionStatus;
        this.createdAt = createdAt;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getSubscriptionStatus() {
        return subscriptionStatus;
    }

    public void setSubscriptionStatus(String subscriptionStatus) {
        this.subscriptionStatus = subscriptionStatus;
    }
}
