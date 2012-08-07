package org.motechproject.ananya.kilkari.smoke.domain.builder;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.smoke.domain.Location;
import org.motechproject.ananya.kilkari.smoke.domain.SubscriptionRequest;

public class SubscriptionRequestBuilder {

    private SubscriptionRequest request;

    public SubscriptionRequestBuilder() {
        request = new SubscriptionRequest();
    }

    public SubscriptionRequestBuilder withDefaults() {
        request.setPack("bari_kilkari");
        request.setChannel("CALL_CENTER");
        request.setMsisdn("9876543210");
        request.setBeneficiaryAge("25");
        request.setBeneficiaryName("name");
        request.setLocation(new Location("D1","B1","P1"));
        request.setDateOfBirth(DateTime.now().minusYears(24).toString("dd-MM-yyyy"));
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

    public SubscriptionRequestBuilder withCreatedAt(DateTime createdAt) {
        request.setCreatedAt(createdAt);
        return this;
    }

    public SubscriptionRequestBuilder withLocation(Location location) {
        request.setLocation(location);
        return this;
    }
}
