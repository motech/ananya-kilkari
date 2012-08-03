package org.motechproject.ananya.kilkari.request;


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
}
