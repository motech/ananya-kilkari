package org.motechproject.ananya.kilkari.request;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import java.io.Serializable;

public class CallbackRequestWrapper implements Serializable {

    private CallbackRequest callbackRequest;
    private String subscriptionId;
    private DateTime createdAt;

    public CallbackRequestWrapper(CallbackRequest callbackRequest, String subscriptionId, DateTime createdAt) {
        this.callbackRequest = callbackRequest;
        this.subscriptionId = subscriptionId;
        this.createdAt = createdAt;
    }

    public String getMsisdn() {
        return callbackRequest.getMsisdn();
    }

    public String getAction() {
        return callbackRequest.getAction();
    }

    public String getStatus() {
        return callbackRequest.getStatus();
    }

    public String getReason() {
        return callbackRequest.getReason();
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public String getOperator() {
        return callbackRequest.getOperator();
    }

    public Integer getGraceCount() {
        return (StringUtils.isNotBlank(callbackRequest.getGraceCount()) && StringUtils.isNumeric(callbackRequest.getGraceCount()))
                ? Integer.valueOf(callbackRequest.getGraceCount())
                : null;
    }
}
