package org.motechproject.ananya.kilkari.obd.contract;

import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

public class CallDeliveryFailureRecordObject  implements Serializable {

    @JsonProperty
    private String subscriptionId;
    @JsonProperty
    private String msisdn;
    @JsonProperty
    private String campaignId;
    @JsonProperty
    private String statusCode;

    public CallDeliveryFailureRecordObject() {
    }

    public CallDeliveryFailureRecordObject(String subscriptionId, String msisdn, String campaignId, String statusCode) {
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
