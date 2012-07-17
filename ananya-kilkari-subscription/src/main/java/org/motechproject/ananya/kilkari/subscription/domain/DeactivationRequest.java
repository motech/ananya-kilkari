package org.motechproject.ananya.kilkari.subscription.domain;

public class DeactivationRequest {

    private String subscriptionId;
    private Channel channel;

    public DeactivationRequest(String subscriptionId, Channel channel) {
        this.subscriptionId = subscriptionId;
        this.channel = channel;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public Channel getChannel() {
        return channel;
    }
}
