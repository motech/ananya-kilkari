package org.motechproject.ananya.kilkari.reporting.domain;

import org.joda.time.DateTime;

import java.io.Serializable;

public class SubscriberReportRequest implements Serializable {
    private String subscriptionId;
    private String channel;
    private DateTime createdAt;
    private String beneficiaryName;
    private String beneficiaryAge;
    private String expectedDateOfDelivery;
    private String dateOfBirth;
    private SubscriberLocation location;

    public SubscriberReportRequest(String subscriptionId, String channel, DateTime createdAt,
                                   String beneficiaryName, String beneficiaryAge, String expectedDateOfDelivery,
                                   String dateOfBirth, SubscriberLocation location) {
        this.subscriptionId = subscriptionId;
        this.channel = channel;
        this.createdAt = createdAt;
        this.beneficiaryName = beneficiaryName;
        this.beneficiaryAge = beneficiaryAge;
        this.expectedDateOfDelivery = expectedDateOfDelivery;
        this.dateOfBirth = dateOfBirth;
        this.location = location;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public String getChannel() {
        return channel;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public String getBeneficiaryAge() {
        return beneficiaryAge;
    }

    public String getExpectedDateOfDelivery() {
        return expectedDateOfDelivery;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public SubscriberLocation getLocation() {
        return location;
    }
}
