package org.motechproject.ananya.kilkari.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;
import org.motechproject.util.StringUtil;

@TypeDiscriminator("doc.type === 'CampaignMessageAlert'")
public class CampaignMessageAlert extends MotechBaseDataObject {

    @JsonProperty
    private String subscriptionId;

    @JsonProperty
    private String messageId;

    @JsonProperty
    private boolean renewed;

    public CampaignMessageAlert() {
    }

    public CampaignMessageAlert(String subscriptionId, String messageId) {
        this.subscriptionId = subscriptionId;
        this.messageId = messageId;
    }

    public CampaignMessageAlert(String subscriptionId, String messageId, boolean renewed) {
        this.subscriptionId = subscriptionId;
        this.messageId = messageId;
        this.renewed = renewed;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public String getMessageId() {
        return messageId;
    }

    public boolean isRenewed() {
        return renewed;
    }

    public boolean canBeScheduled() {
        return renewed && !StringUtil.isNullOrEmpty(messageId);
    }

    public void updateWith(String messageId, boolean renewed) {
        this.messageId = messageId;
        this.renewed = renewed;
    }
}
