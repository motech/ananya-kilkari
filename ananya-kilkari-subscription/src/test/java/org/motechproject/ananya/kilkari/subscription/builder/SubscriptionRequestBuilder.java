package org.motechproject.ananya.kilkari.subscription.builder;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.subscription.domain.Operator;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionStatus;
import org.motechproject.ananya.kilkari.subscription.service.request.Location;
import org.motechproject.ananya.kilkari.subscription.service.request.Subscriber;
import org.motechproject.ananya.kilkari.subscription.service.request.SubscriptionRequest;

public class SubscriptionRequestBuilder {
    private String msisdn;
    private SubscriptionPack pack;
    private Operator operator;
    private SubscriptionStatus status;
    private DateTime creationDate;
    private String district;
    private String panchayat;
    private String block;
    private String beneficiaryName;
    private int beneficiaryAge;
    private DateTime dob;
    private DateTime edd;
    private Integer week;
    private String reason;

    public SubscriptionRequestBuilder withDefaults() {
        return withMsisdn("9876543210")
                .withOperator(Operator.AIRTEL)
                .withStatus(SubscriptionStatus.ACTIVE)
                .withPack(SubscriptionPack.BARI_KILKARI)
                .withBlock("block")
                .withDistrict("district")
                .withPanchayat("panchayat")
                .withReason(null)
                .withCreationDate(DateTime.now());
    }

    public SubscriptionRequestBuilder withStatus(SubscriptionStatus status) {
        this.status = status;
        return this;
    }

    public SubscriptionRequestBuilder withOperator(Operator operator) {
        this.operator = operator;
        return this;
    }

    public SubscriptionRequestBuilder withMsisdn(String msisdn) {
        this.msisdn = msisdn;
        return this;
    }

    public SubscriptionRequestBuilder withPack(SubscriptionPack pack) {
        this.pack = pack;
        return this;
    }


    public SubscriptionRequestBuilder withCreationDate(DateTime creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public SubscriptionRequestBuilder withDistrict(String district) {
        this.district = district;
        return this;
    }

    public SubscriptionRequestBuilder withPanchayat(String panchayat) {
        this.panchayat = panchayat;
        return this;
    }

    public SubscriptionRequestBuilder withBlock(String block) {
        this.block = block;
        return this;
    }

    public SubscriptionRequestBuilder withReason(String reason) {
        this.reason = reason;
        return this;
    }


    public SubscriptionRequestBuilder withBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
        return this;
    }

    public SubscriptionRequestBuilder withBeneficiaryAge(int beneficiaryAge) {
        this.beneficiaryAge = beneficiaryAge;
        return this;
    }

    public SubscriptionRequestBuilder withDateOfBirth(DateTime dob) {
        this.dob = dob;
        return this;
    }

    public SubscriptionRequestBuilder withExpectedDateOfDelivery(DateTime edd) {
        this.edd = edd;
        return this;
    }

    public SubscriptionRequestBuilder withWeek(Integer week) {
        this.week = week;
        return this;
    }

    public SubscriptionRequest build() {
        Location location = new Location(district, block, panchayat);
        Subscriber subscriber = new Subscriber(beneficiaryName, beneficiaryAge, dob, edd, week);
        return new SubscriptionRequest(msisdn, creationDate, pack, location, subscriber, reason);
    }
}
