package org.motechproject.ananya.kilkari.subscription.builder;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.subscription.domain.Operator;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionStatus;

public class SubscriptionBuilder {
    private String msisdn;
    private SubscriptionPack pack;
    private Operator operator;
    private SubscriptionStatus status;
    private DateTime creationDate;
    private DateTime startDate;
    private DateTime scheduleStartDate;
    private DateTime activationDate;

    public SubscriptionBuilder withDefaults() {
        DateTime now = DateTime.now();
        return withMsisdn("9876543210")
                .withOperator(Operator.AIRTEL)
                .withStatus(SubscriptionStatus.ACTIVE)
                .withPack(SubscriptionPack.BARI_KILKARI)
                .withCreationDate(now)
                .withStartDate(now)
                .withActivationDate(now)
                .withScheduleStartDate(now);
    }

    public SubscriptionBuilder withScheduleStartDate(DateTime now) {
        this.scheduleStartDate = now;
        return this;
    }

    public SubscriptionBuilder withActivationDate(DateTime now) {
        this.activationDate = now;
        return this;
    }

    public SubscriptionBuilder withStartDate(DateTime now) {
        this.startDate = now;
        return this;
    }

    public SubscriptionBuilder withStatus(SubscriptionStatus status) {
        this.status = status;
        return this;
    }

    public SubscriptionBuilder withOperator(Operator operator) {
        this.operator = operator;
        return this;
    }

    public SubscriptionBuilder withMsisdn(String msisdn) {
        this.msisdn = msisdn;
        return this;
    }

    public SubscriptionBuilder withPack(SubscriptionPack pack) {
        this.pack = pack;
        return this;
    }


    public SubscriptionBuilder withCreationDate(DateTime creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public Subscription build() {
        Subscription subscription = new Subscription(msisdn, pack, creationDate, startDate);
        subscription.activate(operator.name(), scheduleStartDate, activationDate);
        subscription.setStatus(status);
        subscription.setOperator(operator);
        return subscription;
    }
}
