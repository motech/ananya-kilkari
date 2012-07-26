package org.motechproject.ananya.kilkari.subscription.service.response;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.subscription.domain.Operator;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionStatus;

public class SubscriptionResponse {
    private String msisdn;
    private Operator operator;
    private String subscriptionId;
    private DateTime creationDate;
    private DateTime endDate;
    private SubscriptionStatus status;
    private SubscriptionPack pack;

    public SubscriptionResponse(String msisdn, Operator operator, String subscriptionId, DateTime creationDate, DateTime endDate, SubscriptionStatus status, SubscriptionPack pack) {
        this.msisdn = msisdn;
        this.operator = operator;
        this.subscriptionId = subscriptionId;
        this.creationDate = creationDate;
        this.endDate = endDate;
        this.status = status;
        this.pack = pack;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public Operator getOperator() {
        return operator;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public DateTime getCreationDate() {
        return creationDate;
    }

    public SubscriptionStatus getStatus() {
        return status;
    }

    public SubscriptionPack getPack() {
        return pack;
    }

    public DateTime getEndDate() {
        return endDate;
    }
}
