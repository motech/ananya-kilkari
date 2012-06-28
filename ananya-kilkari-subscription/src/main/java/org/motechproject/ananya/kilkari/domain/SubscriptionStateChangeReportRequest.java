package org.motechproject.ananya.kilkari.domain;

import java.io.Serializable;

public class SubscriptionStateChangeReportRequest implements Serializable {

    private String subscriptionId;

    private String status;

    public SubscriptionStateChangeReportRequest(String subscriptionId, String status) {
        this.subscriptionId = subscriptionId;
        this.status = status;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public String getStatus() {
        return status;
    }
}
