package org.motechproject.ananya.kilkari.web.controller.requests;

import org.motechproject.ananya.kilkari.web.domain.CallbackAction;
import org.motechproject.ananya.kilkari.web.domain.CallbackStatus;

public class CallbackRequest {
    private String msisdn;
    private CallbackAction action;
    private CallbackStatus status;
    private String reason;
    private String operator;
    private String graceCount;

    public String getMsisdn() {
        return msisdn;
    }

    public CallbackAction getAction() {
        return action;
    }

    public CallbackStatus getStatus() {
        return status;
    }

    public String getReason() {
        return reason;
    }

    public String getOperator() {
        return operator;
    }

    public String getGraceCount() {
        return graceCount;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public void setAction(CallbackAction action) {
        this.action = action;
    }

    public void setStatus(CallbackStatus status) {
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
