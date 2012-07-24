package org.motechproject.ananya.kilkari.subscription.domain;

import org.joda.time.DateTime;

public class DeactivationRequest {

    private String subscriptionId;
    private Channel channel;
    private DateTime createdAt;

    public DeactivationRequest(String subscriptionId, Channel channel, DateTime createdAt) {
        this.subscriptionId = subscriptionId;
        this.channel = channel;
        this.createdAt = createdAt;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public Channel getChannel() {
        return channel;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }
}
