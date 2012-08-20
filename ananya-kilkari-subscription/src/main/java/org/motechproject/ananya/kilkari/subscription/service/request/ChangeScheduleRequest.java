package org.motechproject.ananya.kilkari.subscription.service.request;


import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.subscription.domain.ChangeType;
import org.motechproject.ananya.kilkari.subscription.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;

public class ChangeScheduleRequest {
    private ChangeType changeType;
    private String msisdn;
    private String subscriptionId;
    private SubscriptionPack pack;
    private Channel channel;
    private DateTime createdAt;
    private DateTime expectedDateOfDelivery;
    private DateTime dateOfBirth;
    private String reason;

    public ChangeScheduleRequest(ChangeType changeType, String msisdn, String subscriptionId, SubscriptionPack pack, Channel channel, DateTime createdAt,
                                 DateTime expectedDateOfDelivery, DateTime dateOfBirth, String reason) {
        this.changeType = changeType;
        this.msisdn = msisdn;
        this.subscriptionId = subscriptionId;
        this.pack = pack;
        this.channel = channel;
        this.createdAt = createdAt;
        this.expectedDateOfDelivery = expectedDateOfDelivery;
        this.dateOfBirth = dateOfBirth;
        this.reason = reason;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public SubscriptionPack getPack() {
        return pack;
    }

    public Channel getChannel() {
        return channel;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public DateTime getExpectedDateOfDelivery() {
        return expectedDateOfDelivery;
    }

    public String getReason() {
        return reason;
    }

    public DateTime getDateOfBirth() {
        return dateOfBirth;
    }

    public void setExpectedDateOfDelivery(DateTime expectedDateOfDelivery) {
        this.expectedDateOfDelivery = expectedDateOfDelivery;
    }

    public void setDateOfBirth(DateTime dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public ChangeType getChangeType() {
        return changeType;
    }
}