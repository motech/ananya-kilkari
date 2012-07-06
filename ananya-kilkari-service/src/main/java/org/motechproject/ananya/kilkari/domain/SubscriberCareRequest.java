package org.motechproject.ananya.kilkari.domain;

import org.joda.time.DateTime;

import java.io.Serializable;

public class SubscriberCareRequest implements Serializable {
    private String msisdn;
    private String reason;
    private DateTime createdAt;

    public SubscriberCareRequest(String msisdn, String reason) {
        this.msisdn = msisdn;
        this.reason = reason;
        this.createdAt = DateTime.now();
    }

    public String getReason() {
        return reason;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return String.format("msisdn: %s; reason: %s; createdAt: %s", msisdn, reason, createdAt);
    }
}
