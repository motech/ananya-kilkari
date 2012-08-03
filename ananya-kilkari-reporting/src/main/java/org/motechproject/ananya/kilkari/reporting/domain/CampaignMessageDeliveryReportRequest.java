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
    private String callSource;

    public CampaignMessageDeliveryReportRequest(String subscriptionId, String msisdn, String campaignId, String serviceOption, String retryCount, String status, CallDetailsReportRequest callDetailRecord, String callSource) {
        this.subscriptionId = subscriptionId;
        this.msisdn = msisdn;
        this.campaignId = campaignId;
        this.retryCount = retryCount;
        this.status = status;
        this.callDetailRecord = callDetailRecord;
        this.serviceOption = serviceOption;
        this.callSource = callSource;
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

    public String getCallSource() {
        return callSource;
    }
}
