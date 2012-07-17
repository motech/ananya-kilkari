package org.motechproject.ananya.kilkari.web.response;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserCampaignSchedule {

    @JsonProperty("externalId")
    private String msisdn;

    @JsonProperty("schedules")
    private List<CampaignSchedule> campaignScheduleList;

    public UserCampaignSchedule(String msisdn) {
        this.msisdn = msisdn;
        this.campaignScheduleList = new ArrayList<>();
    }

    public void addCampaignSchedule(CampaignSchedule campaignSchedule) {
        this.campaignScheduleList.add(campaignSchedule);
    }

    @JsonProperty("startDate")
    public Long earliestCampaignDateTimeInMillis() {

        List<Long> allDateTimes = new ArrayList<>();

        for (CampaignSchedule campaignSchedule : campaignScheduleList) {
            allDateTimes.addAll(campaignSchedule.getScheduleTimings());
        }
        Collections.sort(allDateTimes);
        return allDateTimes.isEmpty() ? null : allDateTimes.get(0);
    }
}
