package org.motechproject.ananya.kilkari.domain;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.validation.ValidationUtils;

import java.io.Serializable;

public class SubscriptionRequest implements Serializable {
    private String msisdn;
    private String pack;
    private String channel;
    private DateTime createdAt;
    private String beneficiaryName;
    private String beneficiaryAge;

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

    public SubscriptionRequest() {
        this.createdAt = DateTime.now();
    }

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

    private void validateChannel(String channel)   {
        if (!Channel.isValid(channel))
            throw new ValidationException(String.format("Invalid channel %s", channel));
    }

    private void validatePack(String subscriptionPack)   {
        if (!SubscriptionPack.isValid(subscriptionPack))
            throw new ValidationException(String.format("Invalid subscription pack %s", subscriptionPack));
    }

    private void validateMsisdn(String msisdn)   {
        if (!isValidMsisdn(msisdn))
            throw new ValidationException(String.format("Invalid msisdn %s", msisdn));
    }

    private boolean isValidMsisdn(String msisdn) {
        return (StringUtils.length(msisdn) >= 10 && StringUtils.isNumeric(msisdn));
    }

    public void validate() {
        validateMsisdn(msisdn);
        validatePack(pack);
        validateChannel(channel);
        validateAge();
        validateDOB();
        validateEDD();

    }

    private void validateEDD() {
        if(StringUtils.isNotEmpty(expectedDateOfDelivery)) {
            ValidationUtils.assertDateFormat(expectedDateOfDelivery, "dd-MM-yyyy", "Invalid expected date of delivery %s");
        }
    }

    private void validateDOB() {
        if(StringUtils.isNotEmpty(dateOfBirth)) {
            ValidationUtils.assertDateFormat(dateOfBirth, "dd-MM-yyyy", "Invalid date of birth %s");
        }
    }

    private void validateAge() {
        if(StringUtils.isNotEmpty(beneficiaryAge)) {
            ValidationUtils.assertNumeric(beneficiaryAge, "Invalid beneficiary age %s");
        }

    }
}
