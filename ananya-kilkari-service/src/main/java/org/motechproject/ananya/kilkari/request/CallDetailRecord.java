package org.motechproject.ananya.kilkari.request;

import org.codehaus.jackson.annotate.JsonIgnore;
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


    @JsonIgnore
    public String getStartTime() {
        return startTime;
    }

    @JsonIgnore
    public String getEndTime() {
        return endTime;
    }
}
