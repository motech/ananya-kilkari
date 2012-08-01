package org.motechproject.ananya.kilkari.message.domain;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type === 'CampaignMessageAlert'")
public class CampaignMessageAlert extends MotechBaseDataObject {
    @JsonProperty
    private String subscriptionId;

    @JsonProperty
    private String messageId;

    @JsonProperty
    private boolean renewed;

    @JsonProperty
    private DateTime messageExpiryDate;

    CampaignMessageAlert() {
    }

    public CampaignMessageAlert(String subscriptionId, String messageId, boolean renewed, DateTime messageExpiryDate) {
        this.messageExpiryDate = messageExpiryDate;
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

    public void updateWith(String messageId, boolean renewed, DateTime messageExpiryTime) {
        this.messageId = messageId;
        this.renewed = renewed;
        this.messageExpiryDate = messageExpiryTime;
    }

    public DateTime getMessageExpiryDate() {
        return messageExpiryDate;
    }

    public boolean canBeScheduled(boolean checkExpiry) {
        boolean alertRaisedAndRenewed = renewed && !StringUtils.isEmpty(messageId);

        if (checkExpiry) {
            return alertRaisedAndRenewed && hasNotExpired();
        }
        return alertRaisedAndRenewed;
    }

    private boolean hasNotExpired() {
        return messageExpiryDate.isAfterNow();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("subscriptionId", subscriptionId)
                .append("messageId", messageId)
                .append("renewed", renewed)
                .append("messageExpiryDate", messageExpiryDate)
                .toString();
    }

}
