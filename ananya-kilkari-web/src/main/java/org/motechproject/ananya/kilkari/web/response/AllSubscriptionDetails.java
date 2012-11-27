package org.motechproject.ananya.kilkari.web.response;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

//TODO: should get better class name

public class AllSubscriptionDetails extends SubscriptionDetails {
    @JsonProperty
    private String beneficiaryName;

    @JsonProperty
    private String beneficiaryAge;

    @JsonProperty
    private String weekNumber;

    @JsonProperty
    private String expectedDateOfDelivery;

    @JsonProperty
    private String dateOfBirth;

    @JsonProperty
    private LocationResponse location;

    public AllSubscriptionDetails() {
    }

    public AllSubscriptionDetails(String subscriptionId, String pack, String status, String lastCampaignId,
                                  String beneficiaryName, String beneficiaryAge, String weekNumber, String expectedDateOfDelivery, String dateOfBirth, LocationResponse location) {
        super(subscriptionId, pack, status, lastCampaignId);
        this.beneficiaryName = beneficiaryName;
        this.beneficiaryAge = beneficiaryAge;
        this.weekNumber = weekNumber;
        this.expectedDateOfDelivery = expectedDateOfDelivery;
        this.dateOfBirth = dateOfBirth;
        this.location = location;
    }

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public String getBeneficiaryAge() {
        return beneficiaryAge;
    }

    public String getWeekNumber() {
        return weekNumber;
    }

    public String getExpectedDateOfDelivery() {
        return expectedDateOfDelivery;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public LocationResponse getLocation() {
        return location;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
