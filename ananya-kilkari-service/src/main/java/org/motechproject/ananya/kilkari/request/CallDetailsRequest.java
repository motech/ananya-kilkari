package org.motechproject.ananya.kilkari.request;


import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.reporting.domain.CampaignMessageCallSource;
import org.motechproject.ananya.kilkari.subscription.domain.Channel;

import java.io.Serializable;

public class CallDetailsRequest implements Serializable {

    private String msisdn;
    private String campaignId;
    private CallDurationRequest callDurationRequest;
    private DateTime createdAt;
    private CampaignMessageCallSource callSource;
    private Channel channel;

    public CallDetailsRequest(CampaignMessageCallSource callSource, String msisdn, String campaignId, CallDurationRequest callDurationRequest, DateTime createdAt) {
        this.msisdn = msisdn;
        this.campaignId = campaignId;
        this.callDurationRequest = callDurationRequest;
        this.createdAt = createdAt;
        this.callSource = callSource;
        this.channel = Channel.IVR;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public CallDurationRequest getCallDurationRequest() {
        return callDurationRequest;
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
                .append(this.callDurationRequest, that.callDurationRequest)
                .append(this.createdAt, that.createdAt)
                .append(this.callSource, that.callSource)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(this.msisdn)
                .append(this.campaignId)
                .append(this.callDurationRequest)
                .append(this.createdAt)
                .append(this.callSource)
                .hashCode();
    }
}
