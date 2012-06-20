package org.motechproject.ananya.kilkari.web.response;

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

        this.scheduleTimings = new ArrayList<Long>();
        for (DateTime dateTime : scheduleTimings) {
            this.scheduleTimings.add(dateTime.getMillis());
        }

//        this.scheduleTimings = CollectionUtils.transform(scheduleTimings, new Transformer() {
//            @Override
//            public Object transform(Object o) {
//                Date dateTime = (Date)o;
//                return dateTime.
//            }
//        });
    }
}
