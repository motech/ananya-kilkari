package org.motechproject.ananya.kilkari.smoke.domain.kilkari;

import org.joda.time.DateTime;

public class SubscriptionRequest {
    private String msisdn;
    private String pack;
    private String channel;
    private DateTime createdAt;
    private String beneficiaryName;
    private String beneficiaryAge;
    private String expectedDateOfDelivery;
    private String dateOfBirth;
    private Location location;

    public SubscriptionRequest(String msisdn, String pack, String channel, DateTime createdAt, String beneficiaryName, String beneficiaryAge, String expectedDateOfDelivery, String dateOfBirth, Location location) {
        this.msisdn = msisdn;
        this.pack = pack;
        this.channel = channel;
        this.createdAt = createdAt;
        this.beneficiaryName = beneficiaryName;
        this.beneficiaryAge = beneficiaryAge;
        this.expectedDateOfDelivery = expectedDateOfDelivery;
        this.dateOfBirth = dateOfBirth;
        this.location = location;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public String getPack() {
        return pack;
    }

    public String getChannel() {
        return channel;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public String getBeneficiaryAge() {
        return beneficiaryAge;
    }

    public String getExpectedDateOfDelivery() {
        return expectedDateOfDelivery;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public Location getLocation() {
        return location;
    }
}
