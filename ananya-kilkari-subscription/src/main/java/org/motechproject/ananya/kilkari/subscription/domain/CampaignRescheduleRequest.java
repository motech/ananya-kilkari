package org.motechproject.ananya.kilkari.subscription.domain;


import org.joda.time.DateTime;

public class CampaignRescheduleRequest {

    private String subscriptionId;
    private CampaignChangeReason reason;
    private DateTime createdAt;

    public CampaignRescheduleRequest(String subscriptionId, CampaignChangeReason reason, DateTime createdAt) {
        this.subscriptionId = subscriptionId;
        this.reason = reason;
        this.createdAt = createdAt;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public CampaignChangeReason getReason() {
        return reason;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }
}
