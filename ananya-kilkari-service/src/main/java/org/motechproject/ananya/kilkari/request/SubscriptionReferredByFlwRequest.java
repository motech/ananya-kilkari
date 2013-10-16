package org.motechproject.ananya.kilkari.request;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.obd.domain.Channel;
import org.motechproject.ananya.kilkari.obd.service.validator.Errors;
import org.motechproject.ananya.kilkari.subscription.validators.DateUtils;

public class SubscriptionReferredByFlwRequest {
    private String startTime;
    private String endTime;
    private String channel;

    public SubscriptionReferredByFlwRequest(String startTime, String endTime, String channel) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.channel = channel;
    }

    public DateTime getStartTime() {
        return DateUtils.parseDateTimeForCC(startTime);
    }

    public DateTime getEndTime() {
        return DateUtils.parseDateTimeForCC(endTime);
    }

    public String getChannel() {
        return channel;
    }

    public Errors validate() {
        Errors errors = new Errors();
        boolean isStartDateValid = DateUtils.isValidForCC(startTime);
        boolean isEndDateValid = DateUtils.isValidForCC(endTime);
        if (!isStartDateValid)
            errors.add(String.format("Invalid start time %s", startTime));
        if (!isEndDateValid)
            errors.add(String.format("Invalid end time %s", endTime));
        if (!Channel.isCallCenter(channel))
            errors.add(String.format("Invalid channel %s", channel));
        if (isStartDateValid && isEndDateValid && getEndTime().isBefore(getStartTime()))
            errors.add(String.format("Start time %s is after end time %s", startTime, endTime));
        return errors;
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
