package org.motechproject.ananya.kilkari.request;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.ananya.kilkari.obd.domain.Channel;
import org.motechproject.ananya.kilkari.obd.service.validator.Errors;
import org.motechproject.ananya.kilkari.request.validator.WebRequestValidator;
import org.motechproject.ananya.kilkari.subscription.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.subscription.service.request.Location;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;

@XmlRootElement(name = "subscription")
public class SubscriptionWebRequest implements Serializable {
    private static final long serialVersionUID = -6320440975475940990L;
    @JsonProperty
    @XmlElement
    private String msisdn;
    @JsonProperty
    @XmlElement
    private String pack;
    @JsonProperty
    @XmlElement
    private String beneficiaryName;
    @JsonProperty
    @XmlElement
    private String beneficiaryAge;
    @JsonProperty
    @XmlElement
    private String expectedDateOfDelivery;
    @JsonProperty
    @XmlElement
    private String dateOfBirth;
    @JsonProperty
    @XmlElement
    private String week;
    @JsonProperty
    @XmlElement
    private LocationRequest location;
    @JsonProperty
    @XmlElement
    private String referredBy;
	@JsonIgnore
    @XmlTransient
    private String channel;
    @JsonIgnore
    @XmlTransient
    private DateTime createdAt;

    public SubscriptionWebRequest() {
        this.createdAt = DateTime.now();
    }

    @JsonIgnore
    @XmlTransient
    public String getMsisdn() {
        return msisdn;
    }

    @JsonIgnore
    @XmlTransient
    public String getPack() {
        return pack;
    }

    @JsonIgnore
    @XmlTransient
    public String getChannel() {
        return channel;
    }

    @JsonIgnore
    @XmlTransient
    public DateTime getCreatedAt() {
        return createdAt;
    }

    @JsonIgnore
    @XmlTransient
    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    @JsonIgnore
    @XmlTransient
    public Integer getBeneficiaryAge() {
        return StringUtils.isNotEmpty(beneficiaryAge) ? Integer.parseInt(beneficiaryAge) : null;
    }

    @JsonIgnore
    @XmlTransient
    public DateTime getExpectedDateOfDelivery() {
        return parseDateTime(expectedDateOfDelivery);
    }

    @JsonIgnore
    @XmlTransient
    public DateTime getDateOfBirth() {
        return parseDateTime(dateOfBirth);
    }

    @JsonIgnore
    @XmlTransient
    public Location getLocation() {
        return location == null ? null : new Location(location.getDistrict(), location.getBlock(), location.getPanchayat());
    }

    @JsonIgnore
    @XmlTransient
    public String getWeek() {
        return week;
    }
    
    @JsonIgnore
    @XmlTransient
    public String getReferredBy() {
		return referredBy;
	}

	public void setReferredBy(String referredBy) {
		this.referredBy = referredBy;
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

    public void setWeek(String week) {
        this.week = week;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setPack(String pack) {
        this.pack = pack;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public void setLocation(LocationRequest location) {
        this.location = location;
    }

    public Errors validate() {
        WebRequestValidator webRequestValidator = new WebRequestValidator();
        webRequestValidator.validateMsisdn(msisdn);
        webRequestValidator.validatePack(pack);
        webRequestValidator.validateChannel(channel);
        webRequestValidator.validateReferredByMsisdn(referredBy);
        if (!Channel.isIVR(channel)) {
            webRequestValidator.validateLocation(location);
            webRequestValidator.validateName(beneficiaryName);
            webRequestValidator.validateAge(beneficiaryAge);
            webRequestValidator.validateOnlyOneOfEDDOrDOBOrWeekNumberPresent(expectedDateOfDelivery, dateOfBirth, week);
            webRequestValidator.validateDOB(dateOfBirth, createdAt);
            webRequestValidator.validateEDD(expectedDateOfDelivery, createdAt);
            webRequestValidator.validateWeekNumber(week);
        }
        return webRequestValidator.getErrors();
    }

    public void validateChannel() {
        if (!Channel.isValid(channel)) {
            throw new ValidationException(String.format("Invalid channel %s", channel));
        }
    }

    private DateTime parseDateTime(String dateTime) {
        return StringUtils.isNotEmpty(dateTime) ? DateTimeFormat.forPattern("dd-MM-yyyy").parseDateTime(dateTime) : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubscriptionWebRequest)) return false;

        SubscriptionWebRequest that = (SubscriptionWebRequest) o;

        return new EqualsBuilder()
                .append(this.msisdn, that.msisdn)
                .append(this.pack, that.pack)
                .append(this.channel, that.channel)
                .append(this.channel, that.channel)
                .append(this.beneficiaryAge, that.beneficiaryAge)
                .append(this.beneficiaryName, that.beneficiaryName)
                .append(this.dateOfBirth, that.dateOfBirth)
                .append(this.expectedDateOfDelivery, that.expectedDateOfDelivery)
                .append(this.location, that.location)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(this.msisdn)
                .append(this.pack)
                .append(this.channel)
                .append(this.channel)
                .append(this.beneficiaryAge)
                .append(this.beneficiaryName)
                .append(this.dateOfBirth)
                .append(this.expectedDateOfDelivery)
                .append(this.location)
                .hashCode();
    }
}
