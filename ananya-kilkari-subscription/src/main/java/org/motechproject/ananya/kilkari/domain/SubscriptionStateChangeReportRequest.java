package org.motechproject.ananya.kilkari.domain;

import org.joda.time.DateTime;

public class SubscriptionStateChangeReportRequest extends BaseReportRequest {

    private String reason;

    public SubscriptionStateChangeReportRequest(String subscriptionId, String status, DateTime createdAt, String reason) {
        super(subscriptionId, status, createdAt);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
