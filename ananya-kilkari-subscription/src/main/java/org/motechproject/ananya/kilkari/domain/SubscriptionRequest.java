package org.motechproject.ananya.kilkari.domain;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.ananya.kilkari.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.service.ReportingService;
import org.motechproject.ananya.kilkari.validation.ValidationUtils;

import java.io.Serializable;

public class SubscriptionRequest implements Serializable {
    public static final String DATE_TIME_FORMAT = "dd-MM-yyyy";

    private String msisdn;
    private String pack;
    private String channel;
    private DateTime createdAt;
    private String beneficiaryName;
    private String beneficiaryAge;
    private String district;
    private String block;
    private String panchayat;

    public SubscriptionRequest() {
        this.createdAt = DateTime.now();
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getPanchayat() {
        return panchayat;
    }

    public void setPanchayat(String panchayat) {
        this.panchayat = panchayat;
    }

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public int getBeneficiaryAge() {
        return beneficiaryAge == null ? 0 : Integer.parseInt(beneficiaryAge);
    }

    public DateTime getExpectedDateOfDelivery() {
        return parseDateTime(expectedDateOfDelivery);
    }

    public DateTime getDateOfBirth() {
        return parseDateTime(dateOfBirth);
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

    private String expectedDateOfDelivery;

    private String dateOfBirth;

    public String getMsisdn() {
        return msisdn;
    }

    public String getPack() {
        return pack;
    }

    public String getChannel() {
        return channel;
    }

    public DateTime getCreatedAt() {
        return createdAt;
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

    private void validateChannel() {
        if (!Channel.isValid(channel))
            throw new ValidationException(String.format("Invalid channel %s", channel));
    }

    private void validatePack() {
        if (!SubscriptionPack.isValid(pack))
            throw new ValidationException(String.format("Invalid subscription pack %s", pack));
    }

    private void validateMsisdn() {
        if (!isValidMsisdn(msisdn))
            throw new ValidationException(String.format("Invalid msisdn %s", msisdn));
    }

    private boolean isValidMsisdn(String msisdn) {
        return (StringUtils.length(msisdn) >= 10 && StringUtils.isNumeric(msisdn));
    }

    public void validate(ReportingService reportingService) {
        validateMsisdn();
        validatePack();
        validateChannel();
        validateAge();
        validateDOB();
        validateEDD();
        validateLocation(reportingService);
    }

    private void validateLocation(ReportingService reportingService) {
        if (isLocationEmpty()) {
            return;
        }
        if (reportingService.getLocation(district, block, panchayat) == null) {
            throw new ValidationException(String.format("Invalid location with district: %s, block: %s, panchayat: %s", district, block, panchayat));
        }
    }

    private boolean isLocationEmpty() {
        return district == null || block == null || panchayat == null;
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
        return dateTime == null ? null : DateTimeFormat.forPattern(SubscriptionRequest.DATE_TIME_FORMAT).parseDateTime(dateTime);
    }
}
