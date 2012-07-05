package org.motechproject.ananya.kilkari.domain;

import java.io.Serializable;

public class SubscriberCareRequest implements Serializable {
    private String msisdn;
    private String reason;

    public String getMsisdn() {
        return msisdn;
    }

    public String getReason() {
        return reason;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return String.format("msisdn: %s; reason: %s;", msisdn, reason);
    }
}
