package org.motechproject.ananya.kilkari.web.response;

import org.codehaus.jackson.annotate.JsonProperty;

public class SubscriptionDetailsForCC extends SubscriptionDetails{
    @JsonProperty
    private String subscriptionStatus;

    @JsonProperty
    private String benefeciaryName;

    @JsonProperty
    private String benefeciaryAge;

    @JsonProperty
    private String weekNumber;

    @JsonProperty
    private String expectedDateOfDelivery;

    @JsonProperty
    private String dateOfBirth;

    @JsonProperty
    private LocationResponse Location;

    public String getSubscriptionStatus() {
        return subscriptionStatus;
    }

    public String getBenefeciaryName() {
        return benefeciaryName;
    }

    public String getBenefeciaryAge() {
        return benefeciaryAge;
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
        return Location;
    }
}
