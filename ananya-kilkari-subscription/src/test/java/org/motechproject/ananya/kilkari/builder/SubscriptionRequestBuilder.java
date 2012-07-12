package org.motechproject.ananya.kilkari.builder;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.domain.Channel;
import org.motechproject.ananya.kilkari.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.domain.SubscriptionRequest;

public class SubscriptionRequestBuilder {

    private SubscriptionRequest request;

    public SubscriptionRequestBuilder() {
        request = new SubscriptionRequest();
    }

    public SubscriptionRequestBuilder withDefaults() {
        request = new SubscriptionRequest();
        request.setPack(SubscriptionPack.FIFTEEN_MONTHS.name());
        request.setChannel(Channel.CALL_CENTER.name());
        request.setMsisdn("9876543210");
        request.setBeneficiaryAge("25");
        request.setBeneficiaryName("name");
        request.setDistrict("district");
        request.setBlock("block");
        request.setPanchayat("panchayat");
        request.setDateOfBirth(null);
        request.setExpectedDateOfDelivery(DateTime.now().plusDays(30).toString("dd-MM-yyyy"));
        request.setCreatedAt(DateTime.now());
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

    public SubscriptionRequestBuilder withCreatedAt(DateTime createdAt) {
        request.setCreatedAt(createdAt);
        return this;
    }
}
