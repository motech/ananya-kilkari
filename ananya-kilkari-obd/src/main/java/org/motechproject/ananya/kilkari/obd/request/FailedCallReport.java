package org.motechproject.ananya.kilkari.obd.request;

import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

public class FailedCallReport implements Serializable {

    @JsonProperty
    private String subscriptionId;
    @JsonProperty
    private String msisdn;
    @JsonProperty
    private String campaignId;
    @JsonProperty
    private String statusCode;

    public FailedCallReport() {
    }

    public FailedCallReport(String subscriptionId, String msisdn, String campaignId, String statusCode) {
        this.subscriptionId = subscriptionId;
        this.msisdn = msisdn;
        this.campaignId = campaignId;
        this.statusCode = statusCode;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public String getStatusCode() {
        return statusCode;
    }
}
