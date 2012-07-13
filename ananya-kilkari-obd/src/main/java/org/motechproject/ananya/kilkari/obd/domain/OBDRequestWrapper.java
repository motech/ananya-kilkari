package org.motechproject.ananya.kilkari.obd.domain;

import org.joda.time.DateTime;

import java.io.Serializable;

public class OBDRequestWrapper implements Serializable {

    private OBDRequest obdRequest;
    private String subscriptionId;
    private DateTime createdAt;

    public OBDRequestWrapper(OBDRequest obdRequest, String subscriptionId, DateTime createdAt) {
        this.obdRequest = obdRequest;
        this.subscriptionId = subscriptionId;
        this.createdAt = createdAt;
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
}
