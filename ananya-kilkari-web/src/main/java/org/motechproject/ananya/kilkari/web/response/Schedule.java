package org.motechproject.ananya.kilkari.web.response;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class Schedule {

    @JsonProperty("mid")
    private String scheduleKey;

    @JsonProperty("messages")
    private List<Long> scheduleTimings;

    public Schedule(String scheduleKey, List<DateTime> scheduleTimings) {
        this.scheduleKey = scheduleKey;

        this.scheduleTimings = new ArrayList<>();
        for (DateTime dateTime : scheduleTimings) {
            this.scheduleTimings.add(dateTime.getMillis());
        }
    }

    @JsonIgnore
    public List<Long> getScheduleTimings() {
        return scheduleTimings;
    }
}
