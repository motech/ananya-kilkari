package org.motechproject.ananya.kilkari.obd.service.request;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;

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
    @JsonIgnore
    private DateTime createdAt;

    public FailedCallReport() {
        this.createdAt = DateTime.now();
    }

    public FailedCallReport(String subscriptionId, String msisdn, String campaignId, String statusCode) {
        this();
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

    public DateTime getCreatedAt() {
        return createdAt;
    }
}
