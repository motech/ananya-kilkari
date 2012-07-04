package org.motechproject.ananya.kilkari.domain;

import org.joda.time.DateTime;

public class SubscriptionCreationReportRequest extends BaseReportRequest {

    private Channel channel;

    private String msisdn;

    private SubscriptionPack pack;

    private String name;

    private int ageOfBeneficiary;

    private DateTime edd;

    private DateTime dob;

    private SubscriberLocation location;

    private String operator;

    public SubscriptionCreationReportRequest(Subscription subscription, Channel channel, int ageOfBeneficiary, String name, DateTime dob, DateTime edd, SubscriberLocation location) {
        super(subscription.getSubscriptionId(), subscription.getStatus(), subscription.getCreationDate());
        this.name = name;
        this.msisdn = subscription.getMsisdn();
        this.pack = subscription.getPack();
        this.channel = channel;
        this.ageOfBeneficiary = ageOfBeneficiary;
        this.dob = dob;
        this.edd = edd;
        this.location = location;
    }

    public Channel getChannel() {
        return channel;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public SubscriptionPack getPack() {
        return pack;
    }

    public String getName() {
        return name;
    }

    public int getAgeOfBeneficiary() {
        return ageOfBeneficiary;
    }

    public DateTime getEdd() {
        return edd;
    }

    public DateTime getDob() {
        return dob;
    }

    public SubscriberLocation getLocation() {
        return location;
    }

    public String getOperator() {
        return operator;
    }
}
