package org.motechproject.ananya.kilkari.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;

import java.io.Serializable;

public class SubscriptionReportRequest implements Serializable {

    private String subscriptionId;

    private String channel;

    private String msisdn;

    private String pack;

    private String name;

    private int ageOfBeneficiary;

    private DateTime estimatedDateOfDelivery;

    private DateTime dateOfBirth;

    private SubscriberLocation location;

    private int subscriptionWeekNumber;

    private String operator;

    public SubscriptionReportRequest(String msisdn, String pack, String channel, String subscriptionId) {
        this.msisdn = msisdn;
        this.pack = pack;
        this.channel = channel;
        this.subscriptionId = subscriptionId;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getPack() {
        return pack;
    }

    public void setPack(String pack) {
        this.pack = pack;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAgeOfBeneficiary() {
        return ageOfBeneficiary;
    }

    public void setAgeOfBeneficiary(int ageOfBeneficiary) {
        this.ageOfBeneficiary = ageOfBeneficiary;
    }

    public DateTime getEstimatedDateOfDelivery() {
        return estimatedDateOfDelivery;
    }

    public void setEstimatedDateOfDelivery(DateTime estimatedDateOfDelivery) {
        this.estimatedDateOfDelivery = estimatedDateOfDelivery;
    }

    public DateTime getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(DateTime dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public SubscriberLocation getLocation() {
        return location;
    }

    public void setLocation(SubscriberLocation location) {
        this.location = location;
    }

    public int getSubscriptionWeekNumber() {
        return subscriptionWeekNumber;
    }

    public void setSubscriptionWeekNumber(int subscriptionWeekNumber) {
        this.subscriptionWeekNumber = subscriptionWeekNumber;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
