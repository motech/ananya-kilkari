package org.motechproject.ananya.kilkari.request;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.subscription.validators.Errors;
import org.motechproject.ananya.kilkari.subscription.validators.WebRequestValidator;

public class ChangePackWebRequest {
    @JsonProperty
    private String subscriptionId;
    @JsonProperty
    private String msisdn;
    @JsonProperty
    private String pack;
    @JsonProperty
    private String channel;
    @JsonIgnore
    private DateTime createdAt;
    @JsonProperty
    private String expectedDateOfDelivery;
    @JsonProperty
    private String dateOfBirth;

    public ChangePackWebRequest() {
        this.createdAt = DateTime.now();
    }

    public String getSubscriptionId() {
        return subscriptionId;
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

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public Errors validate() {
        WebRequestValidator webRequestValidator = new WebRequestValidator();
        webRequestValidator.validateMsisdn(msisdn);
        webRequestValidator.validatePack(pack);
        webRequestValidator.validateChannel(channel);
        webRequestValidator.validateOnlyOneOfEDDOrDOBIsPresent(expectedDateOfDelivery, dateOfBirth);
        webRequestValidator.validateDOB(dateOfBirth, createdAt);
        webRequestValidator.validateEDD(expectedDateOfDelivery, createdAt);
        return webRequestValidator.getErrors();
    }
}
