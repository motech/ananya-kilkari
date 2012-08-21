package org.motechproject.ananya.kilkari.request;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.obd.service.validator.Errors;
import org.motechproject.ananya.kilkari.request.validator.WebRequestValidator;

public class ChangeSubscriptionWebRequest {
    @JsonProperty
    private String changeType;
    @JsonProperty
    private String msisdn;
    @JsonProperty
    private String pack;
    @JsonIgnore
    private String channel;
    @JsonIgnore
    private DateTime createdAt;
    @JsonProperty
    private String expectedDateOfDelivery;
    @JsonProperty
    private String dateOfBirth;
    @JsonProperty
    private String reason;

    public ChangeSubscriptionWebRequest() {
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

    public String getExpectedDateOfDelivery() {
        return expectedDateOfDelivery;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getChangeType() {
        return changeType;
    }

    public String getReason() {
        return reason;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public void setPack(String pack) {
        this.pack = pack;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public void setExpectedDateOfDelivery(String expectedDateOfDelivery) {
        this.expectedDateOfDelivery = expectedDateOfDelivery;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Errors validate() {
        WebRequestValidator webRequestValidator = new WebRequestValidator();
        webRequestValidator.validateMsisdn(msisdn);
        webRequestValidator.validatePack(pack);
        webRequestValidator.validateChannel(channel);
        webRequestValidator.validateDOB(dateOfBirth, createdAt);
        webRequestValidator.validateEDD(expectedDateOfDelivery, createdAt);
        webRequestValidator.validateOnlyOneOfEDDOrDOBIsPresent(expectedDateOfDelivery, dateOfBirth);
        webRequestValidator.validateChangeType(changeType, expectedDateOfDelivery, dateOfBirth);
        return webRequestValidator.getErrors();
    }

}
