package org.motechproject.ananya.kilkari.obd.service.request;

import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

public class InvalidOBDRequestEntry implements Serializable {
    private static final long serialVersionUID = 4645056348955036120L;
    @JsonProperty
    private String msisdn;
    @JsonProperty
    private String subscriptionId;
    @JsonProperty
    private String operator;
    @JsonProperty
    private String campaignId;
    @JsonProperty
    private String description;

    public String getMsisdn() {
        return msisdn;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public String getOperator() {
        return operator;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public String getDescription() {
        return description;
    }
}
