package org.motechproject.ananya.kilkari.domain;

import org.joda.time.DateTime;

public class SubscriptionStateChangeReportRequest extends BaseReportRequest {

    private String reason;
    private String operator;

    public SubscriptionStateChangeReportRequest(String subscriptionId, String status, DateTime createdAt, String reason, String operator) {
        super(subscriptionId, status, createdAt);
        this.reason = reason;
        this.operator = operator;
    }

    public String getReason() {
        return reason;
    }

    public String getOperator() {
        return operator;
    }
}
