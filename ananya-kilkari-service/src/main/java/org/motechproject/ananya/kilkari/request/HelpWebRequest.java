package org.motechproject.ananya.kilkari.request;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.obd.domain.Channel;
import org.motechproject.ananya.kilkari.obd.service.validator.Errors;
import org.motechproject.ananya.kilkari.subscription.validators.DateUtils;

public class HelpWebRequest {
    private String startDatetime;
    private String endDatetime;
    private String channel;

    public HelpWebRequest(String startDatetime, String endDatetime, String channel) {
        this.startDatetime = startDatetime;
        this.endDatetime = endDatetime;
        this.channel = channel;
    }

    public DateTime getStartDatetime() {
        return DateUtils.parseDateTimeForCC(startDatetime);
    }

    public DateTime getEndDatetime() {
        return DateUtils.parseDateTimeForCC(endDatetime);
    }

    public String getChannel() {
        return channel;
    }

    public Errors validate() {
        Errors errors = new Errors();
        boolean isStartDateValid = DateUtils.isValidForCC(startDatetime);
        boolean isEndDateValid = DateUtils.isValidForCC(endDatetime);
        if (!isStartDateValid)
            errors.add(String.format("Invalid start datetime %s", startDatetime));
        if (!isEndDateValid)
            errors.add(String.format("Invalid end datetime %s", endDatetime));
        if (!Channel.isCallCenter(channel))
            errors.add(String.format("Invalid channel %s", channel));
        if (isStartDateValid && isEndDateValid && getEndDatetime().isBefore(getStartDatetime()))
            errors.add(String.format("Start datetime %s is after end datetime %s", startDatetime, endDatetime));
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
