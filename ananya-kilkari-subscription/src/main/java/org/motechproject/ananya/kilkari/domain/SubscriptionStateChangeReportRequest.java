package org.motechproject.ananya.kilkari.domain;

import org.joda.time.DateTime;

public class SubscriptionStateChangeReportRequest extends BaseReportRequest {

    public SubscriptionStateChangeReportRequest(String subscriptionId, String status, DateTime createdAt) {
        super(subscriptionId, status, createdAt);
    }
}
