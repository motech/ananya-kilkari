package org.motechproject.ananya.kilkari.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.model.MotechBaseDataObject;

import java.util.UUID;

@TypeDiscriminator("doc.type === 'Subscription'")
public class Subscription extends MotechBaseDataObject {
    @JsonProperty
    private String msisdn;

    @JsonProperty
    private String subscriptionId;

    @JsonProperty
    private DateTime subscriptionDate;

    @JsonProperty
    private SubscriptionStatus status;

    @JsonProperty
    private SubscriptionPack pack;

    public Subscription() {
    }

    public Subscription(SubscriptionPack pack, String msisdn) {
        this.pack = pack;
        this.msisdn = msisdn;
        this.subscriptionDate = DateTime.now();
        this.status = SubscriptionStatus.NEW;
        this.subscriptionId = UUID.randomUUID().toString();
    }

    public String getMsisdn() {
        return msisdn;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public DateTime getSubscriptionDate() {
        return subscriptionDate;
    }

    public SubscriptionStatus getStatus() {
        return status;
    }

    public SubscriptionPack getPack() {
        return pack;
    }
}
