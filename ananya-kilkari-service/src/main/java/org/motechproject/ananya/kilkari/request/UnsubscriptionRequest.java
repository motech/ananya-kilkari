package org.motechproject.ananya.kilkari.request;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

public class UnsubscriptionRequest implements Serializable {
    @JsonProperty
    private String channel;
    @JsonProperty
    private String reason;

    @JsonIgnore
    public String getChannel() {
        return channel;
    }

    @JsonIgnore
    public String getReason() {
        return reason;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return String.format("reason: %s; channel: %s", reason, channel);
    }
}