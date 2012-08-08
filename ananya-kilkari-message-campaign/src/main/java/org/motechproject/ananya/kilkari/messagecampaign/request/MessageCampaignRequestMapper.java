package org.motechproject.ananya.kilkari.messagecampaign.request;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;

import java.util.TimeZone;

public class MessageCampaignRequestMapper {

    public static CampaignRequest newRequestFrom(MessageCampaignRequest messageCampaignRequest, Integer campaignScheduleDeltaDays, Integer campaignScheduleDeltaMinutes) {
        DateTime scheduleDateTimeWithDelta = messageCampaignRequest.getSubscriptionStartDate()
                .plusDays(campaignScheduleDeltaDays)
                .plusMinutes(campaignScheduleDeltaMinutes);

        DateTime scheduleDateTimeLocal = scheduleDateTimeWithDelta.toDateTime(DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));

        Time reminderTime = new Time(scheduleDateTimeLocal.toLocalTime());
        LocalDate referenceDateWithDelta = scheduleDateTimeLocal.toLocalDate();

        CampaignRequest campaignRequest = new CampaignRequest(messageCampaignRequest.getExternalId(),
                messageCampaignRequest.getCampaignName(), referenceDateWithDelta, reminderTime);
        return campaignRequest;
    }
}
