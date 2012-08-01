package org.motechproject.ananya.kilkari.functional.test.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.motechproject.ananya.kilkari.request.LocationRequest;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;

public class SubscriptionData {

    private String msisdn;
    private SubscriptionPack pack;
    private String channel;

    @JsonIgnore
    private String subscriptionId;
    private String beneficiaryName;
    private String beneficiaryAge;
    private String expectedDateOfDelivery;
    private String dateOfBirth;
    private LocationRequest location;


    public SubscriptionData() {
    }

    public SubscriptionPack getPack() {
        return pack;
    }

    public String getChannel() {
        return channel;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId= subscriptionId;
    }

    @JsonIgnore
    public String getSubscriptionId() {
        return subscriptionId;
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

    public LocationRequest getLocation() {
        return location;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public void setPack(SubscriptionPack pack) {
        this.pack = pack;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
    }

    public void setBeneficiaryAge(String beneficiaryAge) {
        this.beneficiaryAge = beneficiaryAge;
    }

    public void setExpectedDateOfDelivery(String expectedDateOfDelivery) {
        this.expectedDateOfDelivery = expectedDateOfDelivery;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setLocation(LocationRequest location) {
        this.location = location;
    }
}
