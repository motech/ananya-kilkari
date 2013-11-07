package org.motechproject.ananya.kilkari.request;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.obd.service.validator.Errors;
import org.motechproject.ananya.kilkari.request.validator.WebRequestValidator;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "subscription")
public class ChangeSubscriptionWebRequest {
    @JsonProperty
    @XmlElement
    private String changeType;
    @JsonProperty
    @XmlElement
    private String pack;
    @JsonIgnore
    @XmlTransient
    private String channel;
    @JsonIgnore
    @XmlTransient
    private DateTime createdAt;
    @JsonProperty
    @XmlElement
    private String expectedDateOfDelivery;
    @JsonProperty
    @XmlElement
    private String dateOfBirth;
    @JsonProperty
    @XmlElement
    private String reason;
    @JsonProperty
    @XmlElement
    private String referredBy;
    
    @JsonIgnore
    @XmlTransient
    private boolean referredByFLW;


	public ChangeSubscriptionWebRequest() {
        this.createdAt = DateTime.now();
    }

    @XmlTransient
    public String getPack() {
        return pack;
    }

    @XmlTransient
    public String getChannel() {
        return channel;
    }

    @XmlTransient
    public DateTime getCreatedAt() {
        return createdAt;
    }

    @XmlTransient
    public String getExpectedDateOfDelivery() {
        return expectedDateOfDelivery;
    }

    @XmlTransient
    public String getDateOfBirth() {
        return dateOfBirth;
    }

    @XmlTransient
    public String getChangeType() {
        return changeType;
    }

    @XmlTransient
    public String getReason() {
        return reason;
    }

    @XmlTransient
    public String getReferredBy() {
		return referredBy;
	}
    
    @XmlTransient
	public boolean isReferredByFLW() {
		return referredByFLW;
	}

	public void setReferredBy(String referredBy) {
		this.referredBy = referredBy;
	}
	
    public void setPack(String pack) {
        this.pack = pack;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public void setExpectedDateOfDelivery(String expectedDateOfDelivery) {
        this.expectedDateOfDelivery = expectedDateOfDelivery;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Errors validate() {
        WebRequestValidator webRequestValidator = new WebRequestValidator();
        webRequestValidator.validatePack(pack);
        webRequestValidator.validateChannel(channel);
        webRequestValidator.validateDOB(dateOfBirth, createdAt);
        webRequestValidator.validateEDD(expectedDateOfDelivery, createdAt);
        webRequestValidator.validateOnlyOneOfEDDOrDOBIsPresent(expectedDateOfDelivery, dateOfBirth);
        webRequestValidator.validateChangeType(changeType, expectedDateOfDelivery, dateOfBirth);
        webRequestValidator.validateReferredByMsisdn(referredBy);
        
        return webRequestValidator.getErrors();
    }
}
