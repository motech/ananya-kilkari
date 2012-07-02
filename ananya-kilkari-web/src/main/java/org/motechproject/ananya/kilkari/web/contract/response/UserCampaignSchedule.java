package org.motechproject.ananya.kilkari.web.contract.response;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class UserCampaignSchedule {

    @JsonProperty("externalId")
    private String msisdn;

    @JsonProperty("schedules")
    private List<CampaignSchedule> campaignScheduleList;

    public UserCampaignSchedule(String msisdn) {
        this.msisdn = msisdn;
        this.campaignScheduleList = new ArrayList<CampaignSchedule>();
    }

    public void addCampaignSchedule(CampaignSchedule campaignSchedule) {
        this.campaignScheduleList.add(campaignSchedule);
    }
}
