package org.motechproject.ananya.kilkari.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
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

    @JsonProperty
    private DateTime messageExpiryTime;

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

    public CampaignMessageAlert(String subscriptionId, String messageId, boolean renew, DateTime messageExpiryTime) {
        this(subscriptionId, messageId, renew);
        this.messageExpiryTime = messageExpiryTime;
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
        return renewed && !StringUtil.isNullOrEmpty(messageId) && (messageExpiryTime == null || messageExpiryTime.isAfter(DateTime.now()));
    }

    public void updateWith(String messageId, boolean renewed, DateTime messageExpiryTime) {
        this.messageId = messageId;
        this.renewed = renewed;
        this.messageExpiryTime = messageExpiryTime;
    }

    public DateTime getMessageExpiryTime() {
        return messageExpiryTime;
    }
}
