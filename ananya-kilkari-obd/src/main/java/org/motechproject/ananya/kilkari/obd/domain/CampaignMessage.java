package org.motechproject.ananya.kilkari.obd.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type === 'CampaignMessage'")
public class CampaignMessage extends MotechBaseDataObject implements Comparable<CampaignMessage> {

    @JsonProperty
    private String subscriptionId;

    @JsonProperty
    private String messageId;

    @JsonProperty
    private DateTime weekEndingDate;

    @JsonProperty
    private String msisdn;

    @JsonProperty
    private String operator;

    @JsonProperty
    private boolean sent;

    @JsonProperty
    private CampaignMessageStatus status = CampaignMessageStatus.NEW;

    @JsonProperty
    private int NARetryCount;

    @JsonProperty
    private int NDRetryCount;

    @JsonProperty
    private int SORetryCount;

    public CampaignMessage() {
    }

    public CampaignMessage(String subscriptionId, String messageId, String msisdn, String operator, DateTime weekEndingDate) {
        this.subscriptionId = subscriptionId;
        this.messageId = messageId;
        this.weekEndingDate = weekEndingDate;
        this.msisdn = msisdn;
        this.operator = operator;
    }

    public DateTime getWeekEndingDate() {
        return weekEndingDate;
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

    @JsonIgnore
    public int getNARetryCount() {
        return NARetryCount;
    }

    @JsonIgnore
    public int getNDRetryCount() {
        return NDRetryCount;
    }

    @JsonIgnore
    public int getSORetryCount() {
        return SORetryCount;
    }

    public void markSent() {
        if (this.status == CampaignMessageStatus.NA)
            this.NARetryCount++;
        else if (this.status == CampaignMessageStatus.ND && weekEndingDate.isBeforeNow())
            this.NDRetryCount++;
        else if (this.status == CampaignMessageStatus.SO)
            this.SORetryCount++;

        this.sent = true;
    }

    public void setStatusCode(CampaignMessageStatus statusCode) {
        this.status = statusCode;
        this.sent = false;
    }

    public boolean hasFailed() {
        return CampaignMessageStatus.getFailedStatusCodes().contains(status);
    }

    @JsonIgnore
    public int getRetryCountForCurrentStatus() {
        if (status == CampaignMessageStatus.NA)
            return NARetryCount;
        else if (status == CampaignMessageStatus.ND)
            return NDRetryCount;
        else
            return SORetryCount;
    }

    @Override
    public boolean equals(Object that) {
        return EqualsBuilder.reflectionEquals(this, that);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public int compareTo(CampaignMessage that) {
        return this.status.getPriority().compareTo(that.status.getPriority());
    }
}

