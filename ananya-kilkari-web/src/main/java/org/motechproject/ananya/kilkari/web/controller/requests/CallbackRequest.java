package org.motechproject.ananya.kilkari.web.controller.requests;

import org.motechproject.ananya.kilkari.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.web.domain.CallBackAction;
import org.motechproject.ananya.kilkari.web.domain.CallBackStatus;

public class CallbackRequest {
    private String msisdn;
    private SubscriptionPack srvKey;
    private String refId;
    private CallBackAction action;
    private CallBackStatus status;
    private String reason;
    private String operator;
    private String graceCount;

    public String getMsisdn() {
        return msisdn;
    }

    public SubscriptionPack getSrvKey() {
        return srvKey;
    }

    public String getRefId() {
        return refId;
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

    public String getGraceCount() {
        return graceCount;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public void setSrvKey(SubscriptionPack srvKey) {
        this.srvKey = srvKey;
    }

    public void setRefId(String refId) {
        this.refId = refId;
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

    public void setGraceCount(String graceCount) {
        this.graceCount = graceCount;
    }

    @Override
    public String toString() {
        return String.format("msisdn: %s; pack: %s; refid: %s; reason: %s; operator: %s; graceCount: %s; action: %s; status: %s", msisdn, srvKey, refId, reason, operator, graceCount, action, status);
    }
}
