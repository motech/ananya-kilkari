package org.motechproject.ananya.kilkari.messagecampaign.request;

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
}
