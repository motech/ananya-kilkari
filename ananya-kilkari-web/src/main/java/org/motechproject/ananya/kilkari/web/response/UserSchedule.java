package org.motechproject.ananya.kilkari.web.response;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserSchedule {

    @JsonProperty("externalId")
    private String msisdn;

    @JsonProperty("campaignSchedules")
    private List<Schedule> campaignScheduleList;

    @JsonProperty("subscriptionSchedules")
    private List<Schedule> subscriptionScheduleList;

    public UserSchedule(String msisdn) {
        this.msisdn = msisdn;
        this.campaignScheduleList = new ArrayList<>();
        this.subscriptionScheduleList = new ArrayList<>();
    }

    public void addCampaignSchedule(Schedule schedule) {
        this.campaignScheduleList.add(schedule);
    }

    public void addSubscriptionSchedule(Schedule schedule){
        this.subscriptionScheduleList.add(schedule);
    }

    @JsonProperty("startDate")
    public Long earliestCampaignDateTimeInMillis() {

        List<Long> allDateTimes = new ArrayList<>();

        for (Schedule schedule : campaignScheduleList) {
            allDateTimes.addAll(schedule.getScheduleTimings());
        }
        Collections.sort(allDateTimes);
        return allDateTimes.isEmpty() ? null : allDateTimes.get(0);
    }
}
