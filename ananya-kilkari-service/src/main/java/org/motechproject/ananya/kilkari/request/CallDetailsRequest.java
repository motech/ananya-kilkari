package org.motechproject.ananya.kilkari.request;


import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.obd.domain.CallDetailRecord;
import org.motechproject.ananya.kilkari.reporting.domain.CampaignMessageCallSource;
import org.motechproject.ananya.kilkari.subscription.domain.Channel;

import java.io.Serializable;

public class CallDetailsRequest implements Serializable {

    @JsonProperty
    private String msisdn;
    @JsonProperty
    private String campaignId;
    @JsonProperty
    private CallDetailRecord callDetailRecord;

    @JsonIgnore
    private DateTime createdAt;
    @JsonIgnore
    private CampaignMessageCallSource callSource;
    @JsonIgnore
    private Channel channel;

    public CallDetailsRequest(CampaignMessageCallSource callSource) {
        createdAt = DateTime.now();
        this.callSource = callSource;
        channel = Channel.IVR;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
    }

    public void setCallDetailRecord(CallDetailRecord callDetailRecord) {
        this.callDetailRecord = callDetailRecord;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public CallDetailRecord getCallDetailRecord() {
        return callDetailRecord;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public CampaignMessageCallSource getCallSource() {
        return callSource;
    }

    public Channel getChannel() {
        return channel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CallDetailsRequest)) return false;

        CallDetailsRequest that = (CallDetailsRequest) o;

        return new EqualsBuilder()
                .append(this.msisdn, that.msisdn)
                .append(this.campaignId, that.campaignId)
                .append(this.callDetailRecord, that.callDetailRecord)
                .append(this.createdAt, that.createdAt)
                .append(this.callSource, that.callSource)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(this.msisdn)
                .append(this.campaignId)
                .append(this.callDetailRecord)
                .append(this.createdAt)
                .append(this.callSource)
                .hashCode();
    }
}
