package org.motechproject.ananya.kilkari.request;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.joda.time.DateTime;

import java.io.Serializable;

public class CallDurationRequest implements Serializable {
    private DateTime startTime;

    private DateTime endTime;

    public CallDurationRequest(DateTime startTime, DateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public DateTime getStartTime() {
        return startTime;
    }

    public DateTime getEndTime() {
        return endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CallDurationRequest)) return false;

        CallDurationRequest that = (CallDurationRequest) o;

        return new EqualsBuilder()
                .append(this.startTime, that.startTime)
                .append(this.endTime, that.endTime)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(this.startTime)
                .append(this.endTime)
                .hashCode();
    }
}
