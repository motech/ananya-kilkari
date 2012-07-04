package org.motechproject.ananya.kilkari.builder;

import org.motechproject.ananya.kilkari.domain.SubscriptionRequest;

public class SubscriptionRequestBuilder {

    private SubscriptionRequest request;

    public SubscriptionRequestBuilder withDefaults() {
        request = new SubscriptionRequest();
        return this;
    }

    public SubscriptionRequestBuilder withPack(String pack) {
        request.setPack(pack);
        return this;
    }

    public SubscriptionRequestBuilder withChannel(String channel) {
        request.setChannel(channel);
        return this;
    }

    public SubscriptionRequestBuilder withMsisdn(String msisdn) {
        request.setMsisdn(msisdn);
        return this;
    }

    public SubscriptionRequestBuilder withBeneficiaryAge(String age) {
        request.setBeneficiaryAge(age);
        return this;
    }

    public SubscriptionRequestBuilder withBeneficiaryName(String name) {
        request.setBeneficiaryName(name);
        return  this;
    }

    public SubscriptionRequestBuilder withDOB(String dob) {
        request.setDateOfBirth(dob);
        return this;
    }

    public SubscriptionRequestBuilder withEDD(String edd) {
        request.setExpectedDateOfDelivery(edd);
        return this;
    }

    public SubscriptionRequest build() {
        return request;
    }

    public SubscriptionRequestBuilder withDistrict(String district) {
        request.setDistrict(district);
        return this;
    }

    public SubscriptionRequestBuilder withBlock(String block) {
        request.setBlock(block);
        return this;
    }

    public SubscriptionRequestBuilder withPanchayat(String panchayat) {
        request.setPanchayat(panchayat);
        return this;
    }
}
