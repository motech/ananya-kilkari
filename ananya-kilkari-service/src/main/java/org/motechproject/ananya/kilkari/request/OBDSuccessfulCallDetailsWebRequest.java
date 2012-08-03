package org.motechproject.ananya.kilkari.request;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.ananya.kilkari.obd.domain.ServiceOption;
import org.motechproject.ananya.kilkari.subscription.validators.Errors;

public class OBDSuccessfulCallDetailsWebRequest extends CallDetailsWebRequest {
    @JsonProperty
    private String serviceOption;

    @JsonIgnore
    private String subscriptionId;

    public String getServiceOption() {
        return serviceOption;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setServiceOption(String serviceOption) {
        this.serviceOption = serviceOption;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OBDSuccessfulCallDetailsWebRequest)) return false;

        OBDSuccessfulCallDetailsWebRequest that = (OBDSuccessfulCallDetailsWebRequest) o;

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

    @Override
    public Errors validate() {
        Errors errors = super.validate();
        validateServiceOption(errors);
        return errors;
    }

    private void validateServiceOption(Errors errors) {
        if (!StringUtils.isEmpty(serviceOption) && !ServiceOption.isValid(serviceOption))
            errors.add(String.format("Invalid service option %s", serviceOption));
    }
}
