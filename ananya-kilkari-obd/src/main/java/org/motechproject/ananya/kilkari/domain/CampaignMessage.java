package org.motechproject.ananya.kilkari.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type === 'CampaignMessage'")
public class CampaignMessage extends MotechBaseDataObject {

    @JsonProperty
    private String subscriptionId;

    @JsonProperty
    private String messageId;

    @JsonProperty
    private String msisdn;

    @JsonProperty
    private String operator;

    @JsonProperty
    private boolean sent;

    @JsonProperty
    private CampaignMessageStatus status = CampaignMessageStatus.NEW;

    @JsonProperty
    private int retryCount;

    public CampaignMessage() {
    }

    public CampaignMessage(String subscriptionId, String messageId, String msisdn, String operator) {
        this.subscriptionId = subscriptionId;
        this.messageId = messageId;
        this.msisdn = msisdn;
        this.operator = operator;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public String getOperator() {
        return operator;
    }

    public boolean isSent() {
        return sent;
    }

    public CampaignMessageStatus getStatus() {
        return status;
    }

    public void markSent() {
        if(this.status == CampaignMessageStatus.DNP)
            this.retryCount++;
        this.sent = true;
    }

    public void markDidNotPickup() {
        this.status = CampaignMessageStatus.DNP;
        this.sent = false;
    }

    public void markDidNotCall() {
        this.status = CampaignMessageStatus.DNC;
        this.sent = false;
    }

    public int getRetryCount() {
        return retryCount;
    }
}