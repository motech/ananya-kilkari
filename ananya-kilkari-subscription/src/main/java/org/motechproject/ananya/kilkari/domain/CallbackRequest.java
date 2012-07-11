package org.motechproject.ananya.kilkari.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

public class CallbackRequest implements Serializable {
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
    private String graceCount;

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
        return String.format("msisdn: %s; reason: %s; operator: %s; graceCount: %s; action: %s; status: %s", msisdn, reason, operator, graceCount, action, status);
    }
}
