package org.motechproject.ananya.kilkari.web.controller.requests;

import org.motechproject.ananya.kilkari.web.domain.CallBackAction;
import org.motechproject.ananya.kilkari.web.domain.CallBackStatus;

public class CallbackRequest {
    private String msisdn;
    private CallBackAction action;
    private CallBackStatus status;
    private String reason;
    private String operator;
    private String renewalAttempt;

    public String getMsisdn() {
        return msisdn;
    }

    public CallBackAction getAction() {
        return action;
    }

    public CallBackStatus getStatus() {
        return status;
    }

    public String getReason() {
        return reason;
    }

    public String getOperator() {
        return operator;
    }

    public String getRenewalAttempt() {
        return renewalAttempt;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public void setAction(CallBackAction action) {
        this.action = action;
    }

    public void setStatus(CallBackStatus status) {
        this.status = status;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public void setRenewalAttempt(String renewalAttempt) {
        this.renewalAttempt = renewalAttempt;
    }

    @Override
    public String toString() {
        return String.format("msisdn: %s; reason: %s; operator: %s; renewalAttempt: %s; action: %s; status: %s", msisdn, reason, operator, renewalAttempt, action, status);
    }
}
