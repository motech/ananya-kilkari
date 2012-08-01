package org.motechproject.ananya.kilkari.functional.test.utils;

import org.apache.commons.lang.RandomStringUtils;
import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.functional.test.domain.SubscriptionData;
import org.motechproject.ananya.kilkari.request.LocationRequest;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;

public class SubscriptionDataBuilder {

    private SubscriptionData subscriptionData;

    public SubscriptionDataBuilder() {
        subscriptionData = new SubscriptionData();
    }

    public SubscriptionDataBuilder  withDefaults() {
        subscriptionData.setPack(SubscriptionPack.FIFTEEN_MONTHS);
        subscriptionData.setChannel("CALL_CENTER");
        subscriptionData.setMsisdn(RandomStringUtils.randomNumeric(10));
        subscriptionData.setBeneficiaryAge("25");
        subscriptionData.setBeneficiaryName("Dumbledore");
        subscriptionData.setDateOfBirth(DateTime.now().minusMonths(6).toString("dd-MM-yyyy"));
        subscriptionData.setExpectedDateOfDelivery(DateTime.now().plusMonths(5).toString("dd-MM-yyyy"));
        LocationRequest location = new LocationRequest();
        location.setBlock("block");
        location.setDistrict("district");
        location.setPanchayat("panchayat");
        subscriptionData.setLocation(location);
        return this;
    }

    public SubscriptionDataBuilder withPack(SubscriptionPack pack) {
        subscriptionData.setPack(pack);
        return this;
    }

    public SubscriptionDataBuilder withChannel(String channel) {
        subscriptionData.setChannel(channel);
        return this;
    }

    public SubscriptionDataBuilder withMsisdn(String msisdn) {
        subscriptionData.setMsisdn(msisdn);
        return this;
    }

    public SubscriptionDataBuilder withBeneficiaryAge(String age) {
        subscriptionData.setBeneficiaryAge(age);
        return this;
    }

    public SubscriptionDataBuilder withBeneficiaryName(String name) {
        subscriptionData.setBeneficiaryName(name);
        return this;
    }

    public SubscriptionDataBuilder withDOB(String dob) {
        subscriptionData.setDateOfBirth(dob);
        return this;
    }

    public SubscriptionDataBuilder withEDD(String edd) {
        subscriptionData.setExpectedDateOfDelivery(edd);
        return this;
    }

    public SubscriptionDataBuilder withLocation(LocationRequest location) {
        subscriptionData.setLocation(location);
        return this;
    }



    public SubscriptionData build() {
        return subscriptionData;
    }

}

