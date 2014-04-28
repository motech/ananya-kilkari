package org.motechproject.ananya.kilkari.subscription.builder;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.service.request.Location;
import org.motechproject.ananya.kilkari.subscription.service.request.Subscriber;
import org.motechproject.ananya.kilkari.subscription.service.request.SubscriptionRequest;

public class SubscriptionRequestBuilder {
    private String msisdn;
    private SubscriptionPack pack;
    private DateTime creationDate;
    private String beneficiaryName;
    private int beneficiaryAge;
    private DateTime dob;
    private DateTime edd;
    private Integer week;
    private String reason;
    private Location location;
    private String referredBy;
    private boolean referredByFLW;

    public SubscriptionRequestBuilder withDefaults() {
        return withMsisdn("9876543210")
                .withPack(SubscriptionPack.BARI_KILKARI)
                .withLocation("state", "district", "block", "panchayat")
                .withReason(null)
                .withCreationDate(DateTime.now())
        		.withReferredBy(null)
        		.withReferredByFLW(true);
    }

    public SubscriptionRequestBuilder withMsisdn(String msisdn) {
        this.msisdn = msisdn;
        return this;
    }
    public SubscriptionRequestBuilder withReferredByFLW(boolean referredByFLW) {
        this.referredByFLW = referredByFLW;
        return this;
    }
    public SubscriptionRequestBuilder withReferredBy(String referredBy) {
        this.referredBy = referredBy;
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

    public SubscriptionRequestBuilder withLocation(String state, String district, String block, String panchayat) {
        this.location =  new Location(state, district, block, panchayat);
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

    public SubscriptionRequestBuilder withLocation(Location location) {
        this.location = location;
        return this;
    }

    public SubscriptionRequest build() {
        Subscriber subscriber = new Subscriber(beneficiaryName, beneficiaryAge, dob, edd, week);
        return new SubscriptionRequest(msisdn, creationDate, pack, location, subscriber, reason, referredBy, referredByFLW, "ivr");
    }
}
