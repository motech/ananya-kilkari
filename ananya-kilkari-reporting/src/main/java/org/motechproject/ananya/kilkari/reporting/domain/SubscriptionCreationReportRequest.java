package org.motechproject.ananya.kilkari.reporting.domain;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.joda.time.DateTime;

public class SubscriptionCreationReportRequest extends SubscriptionBaseReportRequest {

    private String channel;

    private String msisdn;

    private String pack;

    private String name;

    private Integer ageOfBeneficiary;

    private DateTime edd;

    private DateTime dob;

    private SubscriberLocation location;

    private DateTime startDate;

    public SubscriptionCreationReportRequest(SubscriptionDetails subscriptionDetails, String channel, Integer ageOfBeneficiary, String name, DateTime dob, DateTime edd, SubscriberLocation location) {
        super(subscriptionDetails.getSubscriptionId(), subscriptionDetails.getStatus(), subscriptionDetails.getCreationDate());
        this.name = name;
        this.msisdn = subscriptionDetails.getMsisdn();
        this.pack = subscriptionDetails.getPack();
        this.channel = channel;
        this.ageOfBeneficiary = ageOfBeneficiary;
        this.dob = dob;
        this.edd = edd;
        this.location = location;
        this.startDate = subscriptionDetails.getStartDate();
    }

    public String getChannel() {
        return channel;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public String getPack() {
        return pack;
    }

    public String getName() {
        return name;
    }

    public Integer getAgeOfBeneficiary() {
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

    public DateTime getStartDate() {
        return startDate;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("channel", channel).toString();
    }
}
