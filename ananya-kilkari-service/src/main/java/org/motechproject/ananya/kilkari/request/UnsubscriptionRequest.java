package org.motechproject.ananya.kilkari.request;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;

import java.io.Serializable;

public class UnsubscriptionRequest extends BaseWebRequest implements Serializable {
    @JsonProperty
    private String reason;
    @JsonIgnore
    private DateTime createdAt;

    public UnsubscriptionRequest() {
        this.createdAt = DateTime.now();
    }

    @JsonIgnore
    public String getReason() {
        return reason;
    }

    @JsonIgnore
    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return String.format("reason: %s; channel: %s", reason, channel);
    }
}