package org.motechproject.ananya.kilkari.request;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.ananya.kilkari.obd.service.validator.Errors;
import org.motechproject.ananya.kilkari.obd.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.request.validator.WebRequestValidator;

import java.io.Serializable;

public class SubscriptionWebRequest implements Serializable {
    @JsonProperty
    private String msisdn;
    @JsonProperty
    private String pack;
    @JsonProperty
    private String beneficiaryName;
    @JsonProperty
    private String beneficiaryAge;
    @JsonProperty
    private String expectedDateOfDelivery;
    @JsonProperty
    private String dateOfBirth;
    @JsonProperty
    private String week;
    @JsonProperty
    private LocationRequest location;

    @JsonIgnore
    private String channel;
    @JsonIgnore
    private DateTime createdAt;

    public SubscriptionWebRequest() {
        this.location = new LocationRequest();
        this.createdAt = DateTime.now();
    }

    @JsonIgnore
    public String getMsisdn() {
        return msisdn;
    }

    @JsonIgnore
    public String getPack() {
        return pack;
    }

    @JsonIgnore
    public String getChannel() {
        return channel;
    }

    @JsonIgnore
    public DateTime getCreatedAt() {
        return createdAt;
    }

    @JsonIgnore
    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    @JsonIgnore
    public Integer getBeneficiaryAge() {
        return StringUtils.isNotEmpty(beneficiaryAge) ? Integer.parseInt(beneficiaryAge) : null;
    }

    @JsonIgnore
    public DateTime getExpectedDateOfDelivery() {
        return parseDateTime(expectedDateOfDelivery);
    }

    @JsonIgnore
    public DateTime getDateOfBirth() {
        return parseDateTime(dateOfBirth);
    }

    @JsonIgnore
    public String getDistrict() {
        return location == null ? null : location.getDistrict();
    }

    @JsonIgnore
    public String getBlock() {
        return location == null ? null : location.getBlock();
    }

    @JsonIgnore
    public String getPanchayat() {
        return location == null ? null : location.getPanchayat();
    }

    @JsonIgnore
    public LocationRequest getLocation() {
        return location;
    }

    @JsonIgnore
    public String getWeek() {
        return week;
    }

    public void setDistrict(String district) {
        location.setDistrict(district);
    }

    public void setBlock(String block) {
        location.setBlock(block);
    }

    public void setPanchayat(String panchayat) {
        location.setPanchayat(panchayat);
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

    public Errors validate() {
        WebRequestValidator webRequestValidator = new WebRequestValidator();
        webRequestValidator.validateMsisdn(msisdn);
        webRequestValidator.validatePack(pack);
        webRequestValidator.validateChannel(channel);
        if (!Channel.isIVR(channel)) {
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
