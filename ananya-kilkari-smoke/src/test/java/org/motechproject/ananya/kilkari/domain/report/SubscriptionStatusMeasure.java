package org.motechproject.ananya.kilkari.domain.report;

public class SubscriptionStatusMeasure {
    private String msisdn;
    private final String status;
    private final String pack;
    private final String channel;

    public SubscriptionStatusMeasure(String msisdn, String status, String pack, String channel) {
        this.msisdn = msisdn;
        this.status = status;
        this.pack = pack;
        this.channel = channel;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public String getStatus() {
        return status;
    }

    public String getPack() {
        return pack;
    }

    public String getChannel() {
        return channel;
    }
}
