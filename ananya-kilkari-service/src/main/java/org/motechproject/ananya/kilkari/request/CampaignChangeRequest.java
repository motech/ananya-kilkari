package org.motechproject.ananya.kilkari.request;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.obd.service.validator.Errors;
import org.motechproject.ananya.kilkari.request.validator.WebRequestValidator;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;

@XmlRootElement(name = "changeCampaign")
public class CampaignChangeRequest implements Serializable {

    private static final long serialVersionUID = 2969953054510171008L;
    @JsonProperty
    @XmlElement
    private String reason;
    @JsonIgnore
    @XmlTransient
    private String channel;
    @JsonIgnore
    @XmlTransient
    private DateTime createdAt;

    public CampaignChangeRequest() {
        this.createdAt = DateTime.now();
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

    @JsonIgnore
    @XmlTransient
    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Errors validate() {
        WebRequestValidator webRequestValidator = new WebRequestValidator();
        webRequestValidator.validateChannel(channel);
        webRequestValidator.validateCampaignChangeReason(reason);
        return webRequestValidator.getErrors();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CampaignChangeRequest)) return false;

        CampaignChangeRequest that = (CampaignChangeRequest) o;

        return new EqualsBuilder()
                .append(this.reason, that.reason)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(this.reason)
                .hashCode();
    }
}