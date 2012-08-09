package org.motechproject.ananya.kilkari.subscription.service.request;


import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.subscription.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;

public class ChangePackRequest {
    private String msisdn;
    private String subscriptionId;
    private SubscriptionPack pack;
    private Channel channel;
    private DateTime createdAt;
    private DateTime expectedDateOfDelivery;
    private DateTime dateOfBirth;

    public ChangePackRequest(String msisdn, String subscriptionId, SubscriptionPack pack, Channel channel, DateTime createdAt,
                             DateTime expectedDateOfDelivery, DateTime dateOfBirth) {
        this.msisdn = msisdn;
        this.subscriptionId = subscriptionId;
        this.pack = pack;
        this.channel = channel;
        this.createdAt = createdAt;
        this.expectedDateOfDelivery = expectedDateOfDelivery;
        this.dateOfBirth = dateOfBirth;
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

    public DateTime getDateOfBirth() {
        return dateOfBirth;
    }

    public void setExpectedDateOfDelivery(DateTime expectedDateOfDelivery) {
        this.expectedDateOfDelivery = expectedDateOfDelivery;
    }

    public void setDateOfBirth(DateTime dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}