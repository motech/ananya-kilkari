package org.motechproject.ananya.kilkari.subscription.service.response;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionStatus;
import org.motechproject.ananya.kilkari.subscription.service.request.Location;


public class SubscriptionDetailsResponse {
    private String subscriptionId;
    private SubscriptionPack pack;
    private SubscriptionStatus status;
    private String campaignId;
    private String beneficiaryName;
    private Integer beneficiaryAge;
    private Integer startWeekNumber;
    private String expectedDateOfDelivery;
    private String dateOfBirth;
    private Location location;

    public SubscriptionDetailsResponse(String subscriptionId, SubscriptionPack pack, SubscriptionStatus status, String campaignId) {
        this.subscriptionId = subscriptionId;
        this.pack = pack;
        this.status = status;
        this.campaignId = campaignId;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public SubscriptionPack getPack() {
        return pack;
    }

    public SubscriptionStatus getStatus() {
        return status;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public Integer getBeneficiaryAge() {
        return beneficiaryAge;
    }

    public Integer getStartWeekNumber() {
        return startWeekNumber;
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

    public void updateSubscriberDetails(String name, Integer age, DateTime dob, DateTime edd, Integer week, Location location) {
        beneficiaryName = name;
        beneficiaryAge = age;
        startWeekNumber = week;
        dateOfBirth = dob == null ? null : dob.toString();
        expectedDateOfDelivery = edd == null ? null : edd.toString();
        this.location = location;
    }
}
