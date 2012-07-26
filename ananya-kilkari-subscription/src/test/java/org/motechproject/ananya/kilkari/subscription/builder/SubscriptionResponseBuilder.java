package org.motechproject.ananya.kilkari.subscription.builder;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.subscription.domain.Operator;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionStatus;
import org.motechproject.ananya.kilkari.subscription.service.response.SubscriptionResponse;

public class SubscriptionResponseBuilder {
    private String msisdn;
    private SubscriptionPack pack;
    private Operator operator;
    private SubscriptionStatus status;
    private DateTime creationDate;
    private String subscriptionId;
    private DateTime endDate;

    public SubscriptionResponseBuilder withDefaults() {
        return withMsisdn("9876543210")
                .withOperator(Operator.AIRTEL)
                .withStatus(SubscriptionStatus.ACTIVE)
                .withPack(SubscriptionPack.FIFTEEN_MONTHS)
                .withSubscriptionId("deadbeef-face-babe-cafe-babecafebabe")
                .withEndDate(DateTime.now().plusDays(4))
                .withCreationDate(DateTime.now());
    }

    public SubscriptionResponseBuilder withSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
        return this;
    }

    public SubscriptionResponseBuilder withEndDate(DateTime endDate) {
        this.endDate = endDate;
        return this;
    }

    public SubscriptionResponseBuilder withStatus(SubscriptionStatus status) {
        this.status = status;
        return this;
    }

    public SubscriptionResponseBuilder withOperator(Operator operator) {
        this.operator = operator;
        return this;
    }

    public SubscriptionResponseBuilder withMsisdn(String msisdn) {
        this.msisdn = msisdn;
        return this;
    }

    public SubscriptionResponseBuilder withPack(SubscriptionPack pack) {
        this.pack = pack;
        return this;
    }


    public SubscriptionResponseBuilder withCreationDate(DateTime creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public SubscriptionResponse build() {
        SubscriptionResponse subscriptionResponse = new SubscriptionResponse(msisdn, operator, subscriptionId, creationDate, endDate, status, pack);
        return subscriptionResponse;
    }
}
