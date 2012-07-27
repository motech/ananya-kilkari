package org.motechproject.ananya.kilkari.functional.test;

public class SubscriptionData {

    private String msisdn;
    private String pack;
    private String channel;
    private String subscriptionId;

    public SubscriptionData(String pack, String channel, String msisdn) {
        this.pack = pack;
        this.channel = channel;
        this.msisdn = msisdn;
    }

    public String getPack() {
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
