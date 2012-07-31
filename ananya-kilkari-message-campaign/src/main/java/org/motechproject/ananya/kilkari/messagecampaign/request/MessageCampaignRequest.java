package org.motechproject.ananya.kilkari.messagecampaign.request;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.joda.time.DateTime;

public class MessageCampaignRequest {

    private String externalId;
    private String subscriptionPack;
    private DateTime subscriptionCreationDate;

    public MessageCampaignRequest(String externalId, String subscriptionPack, DateTime subscriptionCreationDate) {
        this.externalId = externalId;
        this.subscriptionPack = subscriptionPack;
        this.subscriptionCreationDate = subscriptionCreationDate;
    }

    public String getExternalId() {
        return externalId;
    }

    public String getSubscriptionPack() {
        return subscriptionPack;
    }

    public DateTime getSubscriptionCreationDate() {
        return subscriptionCreationDate;
    }

    @Override
    public boolean equals(Object that) {
        return EqualsBuilder.reflectionEquals(this,that);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
