package org.motechproject.ananya.kilkari.web.contract.response;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class CampaignSchedule {

    @JsonProperty("mid")
    private String subscriptionId;

    @JsonProperty("messages")
    private List<Long> scheduleTimings;

    public CampaignSchedule(String subscriptionId, List<DateTime> scheduleTimings) {
        this.subscriptionId = subscriptionId;

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
