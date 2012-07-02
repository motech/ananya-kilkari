package org.motechproject.ananya.kilkari.domain;

import org.joda.time.DateTime;

public class SubscriptionCreationReportRequest extends BaseReportRequest {

    private String channel;

    private String msisdn;

    private String pack;

    private String name;

    private int ageOfBeneficiary;

    private DateTime edd;

    private DateTime dob;

    private SubscriberLocation location;

    private String operator;

    public SubscriptionCreationReportRequest(String msisdn, String pack, String channel, String subscriptionId, DateTime createdAt) {
        super(subscriptionId, SubscriptionStatus.NEW.name(), createdAt);
        this.msisdn = msisdn;
        this.pack = pack;
        this.channel = channel;
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

    public DateTime getEdd() {
        return edd;
    }

    public void setEdd(DateTime edd) {
        this.edd = edd;
    }

    public DateTime getDob() {
        return dob;
    }

    public void setDob(DateTime dob) {
        this.dob = dob;
    }

    public SubscriberLocation getLocation() {
        return location;
    }

    public void setLocation(SubscriberLocation location) {
        this.location = location;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
