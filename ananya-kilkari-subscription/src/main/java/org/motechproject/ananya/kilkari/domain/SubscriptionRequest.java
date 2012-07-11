package org.motechproject.ananya.kilkari.domain;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.ananya.kilkari.validation.ValidationUtils;

import java.io.Serializable;

public class SubscriptionRequest implements Serializable {
    public static final String DATE_TIME_FORMAT = "dd-MM-yyyy";

    public static class Location implements Serializable {
        @JsonProperty
        private String district;
        @JsonProperty
        private String block;
        @JsonProperty
        private String panchayat;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Location)) return false;

            Location that = (Location) o;

            return new EqualsBuilder()
                    .append(this.district, that.district)
                    .append(this.block, that.block)
                    .append(this.panchayat, that.panchayat)
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder()
                    .append(this.district)
                    .append(this.block)
                    .append(this.panchayat)
                    .hashCode();
        }

    }

    @JsonProperty
    private String msisdn;
    @JsonProperty
    private String pack;
    @JsonProperty
    private String channel;
    @JsonIgnore
    private DateTime createdAt;
    @JsonProperty
    private String beneficiaryName;
    @JsonProperty
    private String beneficiaryAge;
    @JsonProperty
    private String expectedDateOfDelivery;
    @JsonProperty
    private String dateOfBirth;
    @JsonProperty
    private Location location;

    public SubscriptionRequest() {
        this.location = new Location();
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
    public int getBeneficiaryAge() {
        return StringUtils.isNotEmpty(beneficiaryAge) ? Integer.parseInt(beneficiaryAge) : 0;
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
        return location == null ? null : location.district;
    }

    @JsonIgnore
    public String getBlock() {
        return location == null ? null : location.block;
    }

    @JsonIgnore
    public String getPanchayat() {
        return location == null ? null : location.panchayat;
    }

    @JsonIgnore
    public Location getLocation() {
        return location;
    }


    public void setDistrict(String district) {
        this.location.district = district;
    }

    public void setBlock(String block) {
        this.location.block = block;
    }

    public void setPanchayat(String panchayat) {
        this.location.panchayat = panchayat;
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

    public void validate() {
        ValidationUtils.assertMsisdn(msisdn);
        ValidationUtils.assertPack(pack);
        validateChannel();
        if (!Channel.isIVR(channel)) {
            validateAge();
            validateDOB();
            validateEDD();
        }
    }

    public void validateChannel() {
        ValidationUtils.assertChannel(channel);
    }

    @JsonIgnore
    public boolean isLocationEmpty() {
        return getDistrict() == null && getBlock() == null && getPanchayat() == null;
    }

    private void validateEDD() {
        if (StringUtils.isNotEmpty(expectedDateOfDelivery)) {
            ValidationUtils.assertDateFormat(expectedDateOfDelivery, SubscriptionRequest.DATE_TIME_FORMAT, "Invalid expected date of delivery %s");
        }
    }

    private void validateDOB() {
        if (StringUtils.isNotEmpty(dateOfBirth)) {
            ValidationUtils.assertDateFormat(dateOfBirth, SubscriptionRequest.DATE_TIME_FORMAT, "Invalid date of birth %s");
        }
    }

    private void validateAge() {
        if (StringUtils.isNotEmpty(beneficiaryAge)) {
            ValidationUtils.assertNumeric(beneficiaryAge, "Invalid beneficiary age %s");
        }
    }

    private DateTime parseDateTime(String dateTime) {
        return StringUtils.isNotEmpty(dateTime) ? DateTimeFormat.forPattern(SubscriptionRequest.DATE_TIME_FORMAT).parseDateTime(dateTime) : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubscriptionRequest)) return false;

        SubscriptionRequest that = (SubscriptionRequest) o;

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
