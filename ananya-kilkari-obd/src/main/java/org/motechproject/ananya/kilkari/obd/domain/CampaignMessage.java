package org.motechproject.ananya.kilkari.obd.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.motechproject.common.domain.PhoneNumber;
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
    private int dnpRetryCount;

    @JsonProperty
    private int dncRetryCount;

    public CampaignMessage() {
    }

    public CampaignMessage(String subscriptionId, String messageId, String msisdn, String operator, DateTime weekEndingDate) {
        this.subscriptionId = subscriptionId;
        this.messageId = messageId;
        this.weekEndingDate = weekEndingDate;
        this.msisdn = PhoneNumber.formatPhoneNumberTo10Digits(msisdn).toString();
        this.operator = operator;
    }

    public DateTime getWeekEndingDate() {
        return weekEndingDate.withZone(DateTimeZone.getDefault());
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

    public int getDnpRetryCount() {
        return dnpRetryCount;
    }

    public int getDncRetryCount() {
        return dncRetryCount;
    }

    public void markSent() {
        if (this.status == CampaignMessageStatus.DNP)
            this.dnpRetryCount++;
        else if (this.status == CampaignMessageStatus.DNC && weekEndingDate.isBeforeNow())
            this.dncRetryCount++;

        this.sent = true;
    }

    public void setStatusCode(CampaignMessageStatus statusCode) {
        this.status = statusCode;
        this.sent = false;
    }

    @Override
    public int compareTo(CampaignMessage message) {
        return (this.dnpRetryCount > message.dnpRetryCount ? -1 : (this.dnpRetryCount == message.dnpRetryCount ? 0 : 1));
    }
}