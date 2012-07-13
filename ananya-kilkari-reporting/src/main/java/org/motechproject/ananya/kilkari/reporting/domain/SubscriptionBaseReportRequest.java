package org.motechproject.ananya.kilkari.reporting.domain;

import org.joda.time.DateTime;

import java.io.Serializable;

public class SubscriptionBaseReportRequest implements Serializable {

    private String subscriptionId;

    private DateTime createdAt;

    private String subscriptionStatus;

    public SubscriptionBaseReportRequest(String subscriptionId, String subscriptionStatus, DateTime createdAt) {
        this.subscriptionId = subscriptionId;
        this.subscriptionStatus = subscriptionStatus;
        this.createdAt = createdAt;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public String getSubscriptionStatus() {
        return subscriptionStatus;
    }
}
