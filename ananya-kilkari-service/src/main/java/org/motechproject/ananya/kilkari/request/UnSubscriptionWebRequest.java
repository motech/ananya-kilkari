package org.motechproject.ananya.kilkari.request;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.obd.service.validator.Errors;
import org.motechproject.ananya.kilkari.request.validator.WebRequestValidator;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;

@XmlRootElement(name = "subscription")
public class UnSubscriptionWebRequest implements Serializable {
    private static final long serialVersionUID = -3928159115917536538L;
    @JsonIgnore
    @XmlTransient
    private String channel;
    @JsonProperty
    @XmlElement
    private String reason;
    @JsonIgnore
    @XmlTransient
    private DateTime createdAt;

    public UnSubscriptionWebRequest() {
        this.createdAt = DateTime.now();
    }

    @JsonIgnore
    @XmlTransient
    public String getChannel() {
        return channel;
    }

    @JsonIgnore
    @XmlTransient
    public String getReason() {
        return reason;
    }

    @JsonIgnore
    @XmlTransient
    public DateTime getCreatedAt() {
        return createdAt;
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

    public Errors validate() {
        WebRequestValidator webRequestValidator = new WebRequestValidator();
        webRequestValidator.validateChannel(channel);
        return webRequestValidator.getErrors();
    }
}