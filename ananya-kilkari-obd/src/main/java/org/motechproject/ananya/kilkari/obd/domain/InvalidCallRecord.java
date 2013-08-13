package org.motechproject.ananya.kilkari.obd.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type === 'InvalidCallRecord'")
public class InvalidCallRecord extends MotechBaseDataObject {
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

    public InvalidCallRecord() {

    }

    public InvalidCallRecord(String msisdn, String subscriptionId, String campaignId, String operator, String description) {
        this.msisdn = msisdn;
        this.subscriptionId = subscriptionId;
        this.campaignId = campaignId;
        this.operator = operator;
        this.description = description;
    }

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
