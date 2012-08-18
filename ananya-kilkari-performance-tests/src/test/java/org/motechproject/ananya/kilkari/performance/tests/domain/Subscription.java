package org.motechproject.ananya.kilkari.performance.tests.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type === 'Subscription'")
public class Subscription extends MotechBaseDataObject {
    @JsonProperty
    private String msisdn;

    @JsonProperty
    private String subscriptionId;

    @JsonProperty
    private String status;

    @JsonProperty
    private String pack;

    @JsonProperty
    private DateTime creationDate;

    @JsonProperty
    private DateTime activationDate;

    @JsonProperty
    private DateTime startDate;

    public Subscription() {
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

    public String getStatus() {
        return status;
    }

    public String getPack() {
        return pack;
    }

    public DateTime getActivationDate() {
        return activationDate;
    }

    public DateTime getStartDate() {
        return startDate;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPack(String pack) {
        this.pack = pack;
    }

    public void setCreationDate(DateTime creationDate) {
        this.creationDate = creationDate;
    }

    public void setActivationDate(DateTime activationDate) {
        this.activationDate = activationDate;
    }

    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subscription)) return false;

        Subscription that = (Subscription) o;

        return new EqualsBuilder().append(this.msisdn, that.msisdn)
                .append(this.pack, that.pack)
                .append(this.subscriptionId, that.subscriptionId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(this.msisdn)
                .append(this.subscriptionId)
                .append(this.pack)
                .hashCode();
    }
}
