package org.motechproject.ananya.kilkari.functional.test.domain;

public class UnSubscriptionData {

    private String channel;
    private String reason;

    public UnSubscriptionData() {
    }

    public UnSubscriptionData(String channel, String reason) {
        this.channel = channel;
        this.reason = reason;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
