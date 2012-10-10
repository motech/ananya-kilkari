package org.motechproject.ananya.kilkari.builder;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.request.SubscriptionWebRequest;
import org.motechproject.ananya.kilkari.obd.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;

public class SubscriptionWebRequestBuilder {

    private SubscriptionWebRequest subscriptionWebRequest;

    public SubscriptionWebRequestBuilder() {
        subscriptionWebRequest = new SubscriptionWebRequest();
    }

    public SubscriptionWebRequestBuilder withDefaults() {
        subscriptionWebRequest = new SubscriptionWebRequest();
        subscriptionWebRequest.setPack(SubscriptionPack.BARI_KILKARI.name());
        subscriptionWebRequest.setChannel(Channel.CONTACT_CENTER.name());
        subscriptionWebRequest.setMsisdn("9876543210");
        subscriptionWebRequest.setBeneficiaryAge("25");
        subscriptionWebRequest.setBeneficiaryName("name");
        subscriptionWebRequest.setDistrict("district");
        subscriptionWebRequest.setBlock("block");
        subscriptionWebRequest.setPanchayat("panchayat");
        subscriptionWebRequest.setDateOfBirth(null);
        subscriptionWebRequest.setExpectedDateOfDelivery(null);
        subscriptionWebRequest.setCreatedAt(DateTime.now());
        return this;
    }

    public SubscriptionWebRequestBuilder withPack(String pack) {
        subscriptionWebRequest.setPack(pack);
        return this;
    }

    public SubscriptionWebRequestBuilder withChannel(String channel) {
        subscriptionWebRequest.setChannel(channel);
        return this;
    }

    public SubscriptionWebRequestBuilder withWeek(String weekNumber) {
        subscriptionWebRequest.setWeek(weekNumber);
        return this;
    }

    public SubscriptionWebRequestBuilder withMsisdn(String msisdn) {
        subscriptionWebRequest.setMsisdn(msisdn);
        return this;
    }

    public SubscriptionWebRequestBuilder withBeneficiaryAge(String age) {
        subscriptionWebRequest.setBeneficiaryAge(age);
        return this;
    }

    public SubscriptionWebRequestBuilder withBeneficiaryName(String name) {
        subscriptionWebRequest.setBeneficiaryName(name);
        return  this;
    }

    public SubscriptionWebRequestBuilder withDOB(String dob) {
        subscriptionWebRequest.setDateOfBirth(dob);
        return this;
    }

    public SubscriptionWebRequestBuilder withEDD(String edd) {
        subscriptionWebRequest.setExpectedDateOfDelivery(edd);
        return this;
    }

    public SubscriptionWebRequest build() {
        return subscriptionWebRequest;
    }

    public SubscriptionWebRequestBuilder withDistrict(String district) {
        subscriptionWebRequest.setDistrict(district);
        return this;
    }

    public SubscriptionWebRequestBuilder withBlock(String block) {
        subscriptionWebRequest.setBlock(block);
        return this;
    }

    public SubscriptionWebRequestBuilder withPanchayat(String panchayat) {
        subscriptionWebRequest.setPanchayat(panchayat);
        return this;
    }

    public SubscriptionWebRequestBuilder withCreatedAt(DateTime createdAt) {
        subscriptionWebRequest.setCreatedAt(createdAt);
        return this;
    }
}
