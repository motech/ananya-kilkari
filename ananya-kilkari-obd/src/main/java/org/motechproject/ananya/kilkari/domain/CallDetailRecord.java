package org.motechproject.ananya.kilkari.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

public class CallDetailRecord {
    @JsonProperty
    private String startTime;
    @JsonProperty
    private String endTime;

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CallDetailRecord)) return false;

        CallDetailRecord that = (CallDetailRecord) o;

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
