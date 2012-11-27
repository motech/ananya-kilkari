package org.motechproject.ananya.kilkari.subscription.service.response;

import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionStatus;
import org.motechproject.ananya.kilkari.subscription.service.request.Location;
import org.motechproject.ananya.reports.kilkari.contract.response.LocationResponse;

public class SubscriptionDetailsResponse {
    private String subscriptionId;
    private SubscriptionPack pack;
    private SubscriptionStatus status;
    private String campaignId;
    private String beneficiaryName;
    private String beneficiaryAge;
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

    public String getBeneficiaryAge() {
        return beneficiaryAge;
    }

    public Integer getStartWeekNumber() {
        return startWeekNumber;
    }

    public String weekNumber(){
        return startWeekNumber == null ? null : startWeekNumber.toString();
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

    public void updateSubscriberDetails(String name, String age, Integer week, String dob, String edd, LocationResponse locationResponse) {
        beneficiaryName = name;
        beneficiaryAge = age;
        startWeekNumber = week;
        dateOfBirth = dob;
        expectedDateOfDelivery = edd;
        location = locationResponse == null ? null : new Location(locationResponse.getDistrict(), locationResponse.getBlock(), locationResponse.getPanchayat());
    }
}
