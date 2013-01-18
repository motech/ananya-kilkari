package org.motechproject.ananya.kilkari.request;


import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.obd.domain.CampaignCode;
import org.motechproject.ananya.kilkari.obd.domain.PhoneNumber;
import org.motechproject.ananya.kilkari.obd.service.validator.Errors;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CallDetailsWebRequest implements Serializable {

    private static final long serialVersionUID = -5560971605975834166L;
    @JsonProperty
    private String msisdn;
    @JsonProperty
    private String campaignId;
    @JsonProperty("callDetailRecord")
    private CallDurationWebRequest callDurationWebRequest;
    @JsonIgnore
    private DateTime createdAt;

    public CallDetailsWebRequest(String msisdn, String campaignId, CallDurationWebRequest callDurationWebRequest) {
        this();
        this.msisdn = msisdn;
        this.campaignId = campaignId;
        this.callDurationWebRequest = callDurationWebRequest;
    }

    public CallDetailsWebRequest() {
        createdAt = DateTime.now();
    }

    public String getMsisdn() {
        return msisdn;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public CallDurationWebRequest getCallDurationWebRequest() {
        return callDurationWebRequest;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CallDetailsWebRequest)) return false;

        CallDetailsWebRequest that = (CallDetailsWebRequest) o;

        return new EqualsBuilder()
                .append(this.msisdn, that.msisdn)
                .append(this.campaignId, that.campaignId)
                .append(this.callDurationWebRequest, that.callDurationWebRequest)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(this.msisdn)
                .append(this.campaignId)
                .append(this.callDurationWebRequest)
                .hashCode();
    }

    public Errors validate() {
        Errors errors = new Errors();
        validateMsisdn(errors);
        validateCampaignId(errors);
        validateCallDuration(errors);
        return errors;
    }

    private void validateCallDuration(Errors errors) {
        if(callDurationWebRequest == null) {
            errors.add("Null call duration");
            return;
        }
        errors.addAll(callDurationWebRequest.validate());

    }

    private void validateCampaignId(Errors errors) {
        if (StringUtils.isEmpty(campaignId))
            errors.add(String.format("Invalid campaign id %s", campaignId));
        else {
            String campaignIdRegExPattern = "^([A-Z]*)([0-9]{1,2})$";
            Pattern pattern = Pattern.compile(campaignIdRegExPattern);
            Matcher matcher = pattern.matcher(campaignId);
            if (!matcher.find() || !CampaignCode.isValid(matcher.group(1)))
                errors.add(String.format("Invalid campaign id %s", campaignId));
        }
    }

    private void validateMsisdn(Errors errors) {
        if (PhoneNumber.isNotValid(msisdn))
            errors.add(String.format("Invalid msisdn %s", msisdn));
    }

}
