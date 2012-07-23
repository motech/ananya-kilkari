package org.motechproject.ananya.kilkari.request;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

public class UnsubscriptionRequest implements Serializable {
    @JsonProperty
    private String msisdn;
    @JsonProperty
    private String reason;
    @JsonProperty
    private String subscriptionId;
    @JsonProperty
    private String pack;

    @JsonIgnore
    public String getMsisdn() {
        return msisdn;
    }

    @JsonIgnore
    public String getReason() {
        return reason;
    }

    @JsonIgnore
    public String getSubscriptionId() {
        return subscriptionId;
    }

    @JsonIgnore
    public String getPack() {
        return pack;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public void setPack(String pack) {
        this.pack = pack;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return String.format("msisdn: %s; subscriptionId: %s; pack: %s; reason: %s", msisdn, subscriptionId, pack, reason);
    }
}
