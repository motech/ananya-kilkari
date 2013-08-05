package org.motechproject.ananya.kilkari.request;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.obd.service.validator.Errors;
import org.motechproject.ananya.kilkari.request.validator.WebRequestValidator;
import org.motechproject.ananya.kilkari.subscription.service.request.Location;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;

import static org.apache.commons.lang.StringUtils.isEmpty;

@XmlRootElement(name = "subscriber")
public class SubscriberWebRequest implements Serializable {
    private static final long serialVersionUID = 3755618556691805936L;
    @JsonIgnore
    @XmlTransient
    private String channel;
    @JsonIgnore
    @XmlTransient
    private DateTime createdAt;
    @JsonProperty
    @XmlElement
    private String beneficiaryName;
    @JsonProperty
    @XmlElement
    private String beneficiaryAge;
    @JsonProperty
    @XmlElement
    private LocationRequest location;

    public SubscriberWebRequest() {
        this.createdAt = DateTime.now();
    }

    @JsonIgnore
    @XmlTransient
    public String getChannel() {
        return channel;
    }

    @JsonIgnore
    @XmlTransient
    public DateTime getCreatedAt() {
        return createdAt;
    }

    @JsonIgnore
    @XmlTransient
    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    @JsonIgnore
    @XmlTransient
    public String getBeneficiaryAge() {
        return beneficiaryAge;
    }

    @JsonIgnore
    @XmlTransient
    public Location getLocation() {
        return location == null ? null : new Location(location.getState(), location.getDistrict(), location.getBlock(), location.getPanchayat());
    }

    public void setBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
    }

    public void setBeneficiaryAge(String beneficiaryAge) {
        this.beneficiaryAge = beneficiaryAge;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public void setLocation(LocationRequest location) {
        this.location = location;
    }

    public void defaultState(String defaultState) {
        if(this.location != null && isEmpty(this.location.getState()))
            this.location.setState(defaultState);
    }

    public Errors validate() {
        WebRequestValidator webRequestValidator = new WebRequestValidator();
        webRequestValidator.validateAge(beneficiaryAge);
        webRequestValidator.validateChannel(channel);
        validateLocation(webRequestValidator);
        return webRequestValidator.getErrors();
    }

    private void validateLocation(WebRequestValidator webRequestValidator) {
        webRequestValidator.validateLocation(location);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubscriberWebRequest)) return false;

        SubscriberWebRequest that = (SubscriberWebRequest) o;

        return new EqualsBuilder()
                .append(this.channel, that.channel)
                .append(this.beneficiaryAge, that.beneficiaryAge)
                .append(this.beneficiaryName, that.beneficiaryName)
                .append(this.location, that.location)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(this.channel)
                .append(this.beneficiaryAge)
                .append(this.beneficiaryName)
                .append(this.location)
                .hashCode();
    }
}
