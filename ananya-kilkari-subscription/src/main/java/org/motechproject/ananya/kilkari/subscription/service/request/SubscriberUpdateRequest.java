package org.motechproject.ananya.kilkari.subscription.service.request;


import org.joda.time.DateTime;

public class SubscriberUpdateRequest {
    private String subscriptionId;
    private String channel;
    private DateTime createdAt;
    private String beneficiaryName;
    private String beneficiaryAge;
    private String expectedDateOfDelivery;
    private String dateOfBirth;
    private Location location;

    public SubscriberUpdateRequest(String subscriptionId, String channel, DateTime createdAt,
                                   String beneficiaryName, String beneficiaryAge, String expectedDateOfDelivery,
                                   String dateOfBirth, Location location) {
        this.subscriptionId = subscriptionId;
        this.channel = channel;
        this.createdAt = createdAt;
        this.beneficiaryName = beneficiaryName;
        this.beneficiaryAge = beneficiaryAge;
        this.expectedDateOfDelivery = expectedDateOfDelivery;
        this.dateOfBirth = dateOfBirth;
        this.location = location;
    }

    public String getSubscriptionId() {
        return subscriptionId;
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

    public String getBlock() {
        return location.getBlock();
    }

    public String getDistrict() {
        return location.getDistrict();
    }

    public String getPanchayat() {
        return location.getPanchayat();
    }


    public Location getLocation() {
        return location == null ? Location.NULL : location;
    }

    public boolean hasLocation() {
        return !Location.NULL.equals(getLocation());
    }
}
