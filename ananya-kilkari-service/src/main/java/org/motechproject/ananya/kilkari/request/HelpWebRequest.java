package org.motechproject.ananya.kilkari.request;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.obd.domain.Channel;
import org.motechproject.ananya.kilkari.obd.service.validator.Errors;
import org.motechproject.ananya.kilkari.subscription.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.subscription.validators.DateUtils;

public class HelpWebRequest {
    private String startDate;
    private String endDate;
    private String channel;

    public HelpWebRequest(String startDate, String endDate, String channel) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.channel = channel;
    }

    public DateTime getStartDate() {
        return DateUtils.parseDateTimeForCC(startDate);
    }

    public DateTime getEndDate() {
        return DateUtils.parseDateTimeForCC(endDate);
    }

    public String getChannel() {
        return channel;
    }

    public void validate() {
        Errors errors = new Errors();
        if (!DateUtils.isValidForCC(startDate))
            errors.add(String.format("Invalid start date : %s", startDate));
        if (!DateUtils.isValidForCC(endDate))
            errors.add(String.format("Invalid end date : %s", endDate));
        if (!Channel.isCallCenter(channel))
            errors.add(String.format("Invalid channel : %s", channel));
        throwExceptionOnValidationFailure(errors);
    }

    private void throwExceptionOnValidationFailure(Errors errors) {
        if (errors.hasErrors())
            throw new ValidationException(errors.allMessages());
    }

    @Override
    public boolean equals(Object other) {
        return EqualsBuilder.reflectionEquals(this, other);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
