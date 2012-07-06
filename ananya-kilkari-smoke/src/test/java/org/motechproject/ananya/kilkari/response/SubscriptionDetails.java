package org.motechproject.ananya.kilkari.response;

public class SubscriptionDetails {
    private String subscriptionId;
    private String pack;
    private String status;

    public SubscriptionDetails(String subscriptionId, String pack, String status) {
        this.subscriptionId = subscriptionId;
        this.pack = pack;
        this.status = status;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public String getPack() {
        return pack;
    }

    public String getStatus() {
        return status;
    }
}
