package org.motechproject.ananya.kilkari.request;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.obd.domain.Channel;
import org.motechproject.ananya.kilkari.obd.service.validator.Errors;
import org.motechproject.ananya.kilkari.request.validator.WebRequestValidator;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.exceptions.ValidationException;

@XmlRootElement(name = "ReferredByFlw")
public class ReferredByFlwRequest implements Serializable{

	private static final long serialVersionUID = 5745308237830441487L;

	@JsonProperty
	@XmlElement
	private String msisdn;
	@JsonProperty
	@XmlElement
	private SubscriptionPack pack;
	@JsonProperty
	@XmlElement
	private boolean referredBy;

	@JsonIgnore
	@XmlElement
	private String channel;
	@JsonIgnore
	@XmlTransient
	private DateTime createdAt;

	public ReferredByFlwRequest() {
		this.createdAt = DateTime.now();
	}

	@JsonIgnore
	@XmlTransient
	public String getMsisdn() {
		return msisdn;
	}
	@JsonIgnore
	@XmlTransient
	public SubscriptionPack getPack() {
		return pack;
	}
	@JsonIgnore
	@XmlTransient
	public boolean isReferredBy() {
		return referredBy;
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


	public void setReferredBy(boolean referredBy) {
		this.referredBy = referredBy;
	}
	
	public void setCreatedAt(DateTime createdAt) {
		this.createdAt = createdAt;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	public void setPack(SubscriptionPack pack) {
		this.pack = pack;
	}
	
	public void setChannel(String channel) {
		this.channel = channel;
	}	

	public Errors validate() {
		WebRequestValidator webRequestValidator = new WebRequestValidator();
		webRequestValidator.validateMsisdn(msisdn);
		webRequestValidator.validatePack(pack);
		webRequestValidator.validateChannel(channel);
		//webRequestValidator.validateReferredByMsisdn(referredBy);
		return webRequestValidator.getErrors();
	}

	
	public void validateChannel() {
		if (!Channel.isValid(channel)) {
			throw new ValidationException(String.format("Invalid channel %s", channel));
		}
	}

	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((channel == null) ? 0 : channel.hashCode());
		result = prime * result
				+ ((createdAt == null) ? 0 : createdAt.hashCode());
		result = prime * result + ((msisdn == null) ? 0 : msisdn.hashCode());
		result = prime * result + ((pack == null) ? 0 : pack.hashCode());
		result = prime * result + (referredBy ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReferredByFlwRequest other = (ReferredByFlwRequest) obj;
		if (channel == null) {
			if (other.channel != null)
				return false;
		} else if (!channel.equals(other.channel))
			return false;
		if (createdAt == null) {
			if (other.createdAt != null)
				return false;
		} else if (!createdAt.equals(other.createdAt))
			return false;
		if (msisdn == null) {
			if (other.msisdn != null)
				return false;
		} else if (!msisdn.equals(other.msisdn))
			return false;
		if (pack != other.pack)
			return false;
		if (referredBy != other.referredBy)
			return false;
		return true;
	}


}
