package org.motechproject.ananya.kilkari.functional.test.domain;

import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;

public class SubscriptionData {

    private String msisdn;
    private SubscriptionPack pack;
    private String channel;
    private String subscriptionId;

    public SubscriptionData(SubscriptionPack pack, String channel, String msisdn) {
        this.pack = pack;
        this.channel = channel;
        this.msisdn = msisdn;
    }

    public SubscriptionPack getPack() {
        return pack;
    }

    public String getChannel() {
        return channel;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId= subscriptionId;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }
}
