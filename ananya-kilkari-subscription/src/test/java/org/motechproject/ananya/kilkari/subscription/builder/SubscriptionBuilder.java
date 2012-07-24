package org.motechproject.ananya.kilkari.subscription.builder;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.subscription.domain.*;

public class SubscriptionBuilder {
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

    public SubscriptionBuilder withDefaults() {
        return withMsisdn("9876543210")
                .withOperator(Operator.AIRTEL)
                .withStatus(SubscriptionStatus.ACTIVE)
                .withPack(SubscriptionPack.FIFTEEN_MONTHS)
                .withBlock("block")
                .withDistrict("district")
                .withPanchayat("panchayat")
                .withCreationDate(DateTime.now());
    }

    public SubscriptionBuilder withStatus(SubscriptionStatus status) {
        this.status = status;
        return this;
    }

    public SubscriptionBuilder withOperator(Operator operator) {
        this.operator = operator;
        return this;
    }

    public SubscriptionBuilder withMsisdn(String msisdn) {
        this.msisdn = msisdn;
        return this;
    }

    public SubscriptionBuilder withPack(SubscriptionPack pack) {
        this.pack = pack;
        return this;
    }


    public SubscriptionBuilder withCreationDate(DateTime creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public SubscriptionBuilder withDistrict(String district) {
        this.district = district;
        return this;
    }

    public SubscriptionBuilder withPanchayat(String panchayat) {
        this.panchayat = panchayat;
        return this;
    }

    public SubscriptionBuilder withBlock(String block) {
        this.block = block;
        return this;
    }


    public SubscriptionBuilder withBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
        return this;
    }

    public SubscriptionBuilder withBeneficiaryAge(int beneficiaryAge) {
        this.beneficiaryAge = beneficiaryAge;
        return this;
    }

    public SubscriptionBuilder withDateOfBirth(DateTime dob) {
        this.dob = dob;
        return this;
    }

    public SubscriptionBuilder withExpectedDateOfDelivery(DateTime edd) {
        this.edd = edd;
        return this;
    }

    public Subscription build() {
        Subscription subscription = new Subscription(msisdn, pack, creationDate);
        subscription.setStatus(status);
        subscription.setOperator(operator);
        subscription.setLocation(new Location(panchayat, block, district));
        subscription.setSubscriber(new Subscriber(beneficiaryName, beneficiaryAge, dob, edd));
        return subscription;
    }
}
