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
    private DateTime creationDate;

    @JsonProperty
    private SubscriptionStatus status;

    @JsonProperty
    private SubscriptionPack pack;

    public Subscription() {
    }

    public Subscription(String msisdn, SubscriptionPack pack) {
        this.pack = pack;
        this.msisdn = msisdn;
        this.creationDate = DateTime.now();
        this.status = SubscriptionStatus.NEW;
        this.subscriptionId = UUID.randomUUID().toString();
    }

    public String getMsisdn() {
        return msisdn;
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

    public void setStatus(SubscriptionStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Subscription that = (Subscription) o;

        if (msisdn != null ? !msisdn.equals(that.msisdn) : that.msisdn != null) return false;
        if (pack != that.pack) return false;
        if (status != that.status) return false;
        if (subscriptionId != null ? !subscriptionId.equals(that.subscriptionId) : that.subscriptionId != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = msisdn != null ? msisdn.hashCode() : 0;
        result = 31 * result + (subscriptionId != null ? subscriptionId.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (pack != null ? pack.hashCode() : 0);
        return result;
    }
}
