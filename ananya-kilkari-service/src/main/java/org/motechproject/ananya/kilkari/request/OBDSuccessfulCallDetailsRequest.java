package org.motechproject.ananya.kilkari.request;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.obd.domain.ServiceOption;
import org.motechproject.ananya.kilkari.reporting.domain.CampaignMessageCallSource;

public class OBDSuccessfulCallDetailsRequest extends CallDetailsRequest {
    private ServiceOption serviceOption;
    private String subscriptionId;

    public OBDSuccessfulCallDetailsRequest(String subscriptionId, ServiceOption serviceOption, String msisdn, String campaingId, CallDurationRequest callDurationRequest, DateTime createdAt) {
        super(CampaignMessageCallSource.OBD, msisdn, campaingId, callDurationRequest, createdAt);
        this.subscriptionId = subscriptionId;
        this.serviceOption = serviceOption;
    }

    public ServiceOption getServiceOption() {
        return serviceOption;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OBDSuccessfulCallDetailsRequest)) return false;

        OBDSuccessfulCallDetailsRequest that = (OBDSuccessfulCallDetailsRequest) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(this.serviceOption, that.serviceOption)
                .append(this.subscriptionId, that.subscriptionId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .appendSuper(super.hashCode())
                .append(this.serviceOption)
                .append(this.subscriptionId)
                .hashCode();
    }
}
