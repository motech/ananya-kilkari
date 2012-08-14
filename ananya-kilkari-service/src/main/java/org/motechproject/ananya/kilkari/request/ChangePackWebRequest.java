package org.motechproject.ananya.kilkari.request;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.request.validator.WebRequestValidator;
import org.motechproject.ananya.kilkari.subscription.validators.Errors;

public class ChangePackWebRequest extends BaseWebRequest {
    @JsonProperty
    private String msisdn;
    @JsonProperty
    private String pack;
    @JsonIgnore
    private DateTime createdAt;
    @JsonProperty
    private String expectedDateOfDelivery;
    @JsonProperty
    private String dateOfBirth;

    public ChangePackWebRequest() {
        this.createdAt = DateTime.now();
    }

    public String getMsisdn() {
        return msisdn;
    }

    public String getPack() {
        return pack;
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

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public void setPack(String pack) {
        this.pack = pack;
    }

    public void setExpectedDateOfDelivery(String expectedDateOfDelivery) {
        this.expectedDateOfDelivery = expectedDateOfDelivery;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Errors validate() {
        WebRequestValidator webRequestValidator = new WebRequestValidator();
        webRequestValidator.validateMsisdn(msisdn);
        webRequestValidator.validatePack(pack);
        webRequestValidator.validateChannel(channel);
        webRequestValidator.validateDOB(dateOfBirth, createdAt);
        webRequestValidator.validateEDD(expectedDateOfDelivery, createdAt);
        return webRequestValidator.getErrors();
    }
}
