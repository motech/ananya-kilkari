package org.motechproject.ananya.kilkari.request;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.ananya.kilkari.obd.service.validator.Errors;
import org.motechproject.ananya.kilkari.request.validator.WebRequestValidator;

public class InboxCallDetailsWebRequest extends CallDetailsWebRequest {
    @JsonProperty
    private String pack;

    @JsonProperty
    private String subscriptionId;

    public InboxCallDetailsWebRequest(String msisdn, String campaignId, CallDurationWebRequest callDurationWebRequest, String pack, String subscriptionId) {
        super(msisdn, campaignId, callDurationWebRequest);
        this.pack = pack;
        this.subscriptionId = subscriptionId;
    }

    public InboxCallDetailsWebRequest() {

    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public String getPack() {
        return pack;
    }

    @Override
    public Errors validate() {
        Errors errors = super.validate();
        validatePack(errors);
        return errors;
    }

    private void validatePack(Errors errors) {
        WebRequestValidator validator = new WebRequestValidator();
        validator.validatePack(pack);
        errors.addAll(validator.getErrors());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InboxCallDetailsWebRequest)) return false;

        InboxCallDetailsWebRequest that = (InboxCallDetailsWebRequest) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(this.pack, that.pack)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .appendSuper(super.hashCode())
                .append(this.pack)
                .hashCode();
    }
}
