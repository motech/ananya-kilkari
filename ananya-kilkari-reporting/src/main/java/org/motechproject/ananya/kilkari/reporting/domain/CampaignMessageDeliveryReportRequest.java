package org.motechproject.ananya.kilkari.reporting.domain;

import java.io.Serializable;

public class CampaignMessageDeliveryReportRequest  implements Serializable {
    private String subscriptionId;
    private String msisdn;
    private String campaignId;
    private String retryCount;
    private String status;
    private CallDetailsReportRequest callDetailRecord;
    private String serviceOption;

    public CampaignMessageDeliveryReportRequest(String subscriptionId, String msisdn, String campaignId, String serviceOption, String retryCount, String status, CallDetailsReportRequest callDetailRecord) {
        this.subscriptionId = subscriptionId;
        this.msisdn = msisdn;
        this.campaignId = campaignId;
        this.retryCount = retryCount;
        this.status = status;
        this.callDetailRecord = callDetailRecord;
        this.serviceOption = serviceOption;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public String getRetryCount() {
        return retryCount;
    }

    public String getServiceOption() {
        return serviceOption;
    }

    public CallDetailsReportRequest getCallDetailRecord() {
        return callDetailRecord;
    }

    public String getStatus() {
        return status;
    }
}
