package org.motechproject.ananya.kilkari.domain;

import org.joda.time.DateTime;

import java.io.Serializable;

public class BaseReportRequest implements Serializable {

    private String subscriptionId;

    private DateTime createdAt;

    private SubscriptionStatus subscriptionStatus;

    public BaseReportRequest(String subscriptionId, SubscriptionStatus subscriptionStatus, DateTime createdAt) {
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

    public SubscriptionStatus getSubscriptionStatus() {
        return subscriptionStatus;
    }
}
