package org.motechproject.ananya.kilkari.domain;

import java.io.Serializable;

public class SubscriptionActivationRequest implements Serializable {
    private final String msisdn;
    private final SubscriptionPack pack;
    private final Channel channel;
    private String subscriptionId;

    public SubscriptionActivationRequest(String msisdn, SubscriptionPack pack, Channel channel, String subscriptionId) {
        this.msisdn = msisdn;
        this.pack = pack;
        this.channel = channel;
        this.subscriptionId = subscriptionId;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public SubscriptionPack getPack() {
        return pack;
    }

    public Channel getChannel() {
        return channel;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }
}
