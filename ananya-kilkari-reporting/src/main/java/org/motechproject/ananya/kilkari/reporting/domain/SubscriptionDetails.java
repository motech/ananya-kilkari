package org.motechproject.ananya.kilkari.reporting.domain;

import org.joda.time.DateTime;

public class SubscriptionDetails {
    private String msisdn;

    private String subscriptionId;

    private DateTime creationDate;

    private String status;

    private String pack;

    private DateTime startDate;

    public SubscriptionDetails() {
    }

    public SubscriptionDetails(String msisdn, String pack, DateTime createdAt, String status, String subscriptionId, DateTime startDate) {
        this.pack = pack;
        this.msisdn = msisdn;
        this.creationDate = createdAt;
        this.status = status;
        this.subscriptionId = subscriptionId;
        this.startDate = startDate;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public DateTime getCreationDate() {
        return creationDate;
    }

    public String getStatus() {
        return status;
    }

    public String getPack() {
        return pack;
    }

    public DateTime getStartDate() {
        return startDate;
    }
}
