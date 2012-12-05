package org.motechproject.ananya.kilkari.web.response;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AllSubscriptionDetails extends SubscriptionDetails {
    @JsonProperty
    @XmlElement
    private String beneficiaryName;

    @JsonProperty
    @XmlElement
    private Integer beneficiaryAge;

    @JsonProperty
    @XmlElement(name = "week")
    private Integer weekNumber;

    @JsonProperty
    @XmlElement
    private String expectedDateOfDelivery;

    @JsonProperty
    @XmlElement
    private String dateOfBirth;

    @JsonProperty
    @XmlElement
    private LocationResponse location;

    public AllSubscriptionDetails() {
    }

    public AllSubscriptionDetails(String subscriptionId, String pack, String status, String lastCampaignId,
                                  String beneficiaryName, Integer beneficiaryAge, Integer weekNumber, String expectedDateOfDelivery, String dateOfBirth, LocationResponse location) {
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

    public Integer getBeneficiaryAge() {
        return beneficiaryAge;
    }

    public Integer getWeekNumber() {
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
