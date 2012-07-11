package org.motechproject.ananya.kilkari.builder;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.domain.Operator;
import org.motechproject.ananya.kilkari.domain.Subscription;
import org.motechproject.ananya.kilkari.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.domain.SubscriptionStatus;

public class SubscriptionBuilder {
    private String msisdn;
    private SubscriptionPack pack;
    private Operator operator;
    private SubscriptionStatus status;

    public SubscriptionBuilder withDefaults() {
        return withMsisdn("9876543210").withOperator(Operator.AIRTEL)
                .withStatus(SubscriptionStatus.ACTIVE).withPack(SubscriptionPack.FIFTEEN_MONTHS);
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


    public Subscription build() {
        Subscription subscription = new Subscription(msisdn, pack, DateTime.now());
        subscription.setStatus(status);
        subscription.setOperator(operator.name());
        
        return subscription;
    }

}
