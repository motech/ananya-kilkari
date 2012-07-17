package org.motechproject.ananya.kilkari.request;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.subscription.domain.Channel;

import java.io.Serializable;

public class OBDRequestWrapper implements Serializable {

    private OBDRequest obdRequest;
    private String subscriptionId;
    private DateTime createdAt;
    private Channel channel;

    public OBDRequestWrapper(OBDRequest obdRequest, String subscriptionId, DateTime createdAt, Channel channel) {
        this.obdRequest = obdRequest;
        this.subscriptionId = subscriptionId;
        this.createdAt = createdAt;
        this.channel = channel;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public OBDRequest getObdRequest() {
        return obdRequest;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public String getCampaignId() {
        return obdRequest.getCampaignId();
    }

    public Channel getChannel() {
        return channel;
    }
}
