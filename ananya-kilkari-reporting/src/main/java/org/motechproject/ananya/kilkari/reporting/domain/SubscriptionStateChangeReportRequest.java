package org.motechproject.ananya.kilkari.reporting.domain;

import org.joda.time.DateTime;

public class SubscriptionStateChangeReportRequest extends BaseReportRequest {

    private String reason;
    private String operator;
    private Integer graceCount;

    public SubscriptionStateChangeReportRequest(String subscriptionId, String status, DateTime createdAt, String reason, String operator) {
        super(subscriptionId, status, createdAt);
        this.reason = reason;
        this.operator = operator;
    }

    public SubscriptionStateChangeReportRequest(String subscriptionId, String status, DateTime createdAt, String reason, String operator, Integer graceCount) {
        this(subscriptionId, status, createdAt, reason, operator);
        this.graceCount = graceCount;
    }

    public String getReason() {
        return reason;
    }

    public String getOperator() {
        return operator;
    }

    public Integer getGraceCount() {
        return graceCount;
    }
}
