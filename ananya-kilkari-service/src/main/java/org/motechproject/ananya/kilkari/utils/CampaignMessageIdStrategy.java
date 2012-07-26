package org.motechproject.ananya.kilkari.utils;

import org.joda.time.DateTime;
import org.joda.time.Weeks;
import org.motechproject.ananya.kilkari.messagecampaign.service.MessageCampaignService;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class CampaignMessageIdStrategy {

    private static final HashMap<String, String> CAMPAIGN_NAME_CODE_MAPPING = new HashMap<String, String>(){{
        put(MessageCampaignService.FIFTEEN_MONTHS_CAMPAIGN_KEY, "WEEK%s");
        put(MessageCampaignService.TWELVE_MONTHS_CAMPAIGN_KEY, "WEEK%s");
        put(MessageCampaignService.SEVEN_MONTHS_CAMPAIGN_KEY, "WEEK%s");
        put(MessageCampaignService.INFANT_DEATH_CAMPAIGN_KEY, "ID%s");
        put(MessageCampaignService.MISCARRIAGE_CAMPAIGN_KEY, "MC%s");
    }};

    public String createMessageId(String campaignName, DateTime campaignStartDate) {
        int weekNumber = getWeekNumber(campaignStartDate);
        return String.format(CampaignMessageIdStrategy.CAMPAIGN_NAME_CODE_MAPPING.get(campaignName), weekNumber);
    }

    public int getWeekNumber(DateTime campaignStartDate) {
        int weeksDifference = getWeeksElapsedAfterCampaignCreationDate(campaignStartDate);
        return weeksDifference + 1;
    }

    private int getWeeksElapsedAfterCampaignCreationDate(DateTime campaignStartDate) {
        return Weeks.weeksBetween(campaignStartDate, DateTime.now()).getWeeks();
    }
}
