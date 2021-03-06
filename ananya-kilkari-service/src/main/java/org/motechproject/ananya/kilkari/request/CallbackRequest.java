package org.motechproject.ananya.kilkari.request;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;

import java.io.Serializable;

public class CallbackRequest implements Serializable {
    private static final long serialVersionUID = 4147977585542611809L;
    @JsonProperty
    private String msisdn;
    @JsonProperty
    private String action;
    @JsonProperty
    private String status;
    @JsonProperty
    private String reason;
    @JsonProperty
    private String operator;
    @JsonProperty
    private SubscriptionPack pack;
    @JsonProperty
    private String graceCount;
    @JsonProperty
    private String mode;

    @JsonIgnore
    public String getMsisdn() {
        return msisdn;
    }

    @JsonIgnore
    public String getAction() {
        return action;
    }

    @JsonIgnore
    public String getStatus() {
        return status;
    }

    @JsonIgnore
    public String getReason() {
        return reason;
    }

    @JsonIgnore
    public String getOperator() {
        return operator;
    }

    @JsonIgnore
    public String getGraceCount() {
        return graceCount;
    }

    @JsonIgnore
    public SubscriptionPack getPack() {
		return pack;
	}
    
    @JsonIgnore
	public String getMode() {
		return mode;
	}

	public void setPack(SubscriptionPack pack) {
		this.pack = pack;
	}

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public void setGraceCount(String graceCount) {
        this.graceCount = graceCount;
    }

	@Override
	public String toString() {
		return "CallbackRequest [msisdn=" + msisdn + ", action=" + action
				+ ", status=" + status + ", reason=" + reason + ", operator="
				+ operator + ", pack=" + pack + ", graceCount=" + graceCount
				+ ", mode=" + mode + "]";
	}

   
}
