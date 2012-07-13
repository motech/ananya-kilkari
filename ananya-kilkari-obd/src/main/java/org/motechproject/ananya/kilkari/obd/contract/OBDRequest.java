package org.motechproject.ananya.kilkari.obd.contract;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.ananya.kilkari.obd.domain.CallDetailRecord;

public class OBDRequest {
    @JsonProperty
    private String msisdn;
    @JsonProperty
    private String campaignId;
    @JsonProperty
    private String serviceOption;
    @JsonProperty
    private CallDetailRecord callDetailRecord;

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
    }

    public void setServiceOption(String serviceOption) {
        this.serviceOption = serviceOption;
    }

    public void setCallDetailRecord(CallDetailRecord callDetailRecord) {
        this.callDetailRecord = callDetailRecord;
    }

    @JsonIgnore
    public String getMsisdn() {
        return msisdn;
    }

    @JsonIgnore
    public String getServiceOption() {
        return serviceOption;
    }

    @JsonIgnore
    public String getCampaignId() {
        return campaignId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OBDRequest)) return false;

        OBDRequest that = (OBDRequest) o;

        return new EqualsBuilder()
                .append(this.msisdn, that.msisdn)
                .append(this.campaignId, that.campaignId)
                .append(this.serviceOption, that.serviceOption)
                .append(this.callDetailRecord, that.callDetailRecord)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(this.msisdn)
                .append(this.campaignId)
                .append(this.serviceOption)
                .append(this.callDetailRecord)
                .hashCode();
    }
}
