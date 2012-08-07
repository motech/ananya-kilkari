package org.motechproject.ananya.kilkari.messagecampaign.request;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.ananya.kilkari.messagecampaign.domain.MessageCampaignPack;
import org.motechproject.model.Time;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;

public class MessageCampaignRequestMapper {

    public static CampaignRequest newRequestFrom(MessageCampaignRequest messageCampaignRequest, Integer campaignScheduleDeltaDays, Integer campaignScheduleDeltaMinutes) {
        Time reminderTime = new Time(messageCampaignRequest.getSubscriptionStartDate().plusMinutes(campaignScheduleDeltaMinutes).toLocalTime());
        DateTime referenceDate = messageCampaignRequest.getSubscriptionStartDate();

        LocalDate referenceDateWithDelta = referenceDate.plusDays(campaignScheduleDeltaDays).toLocalDate();
        CampaignRequest campaignRequest = new CampaignRequest(messageCampaignRequest.getExternalId(), messageCampaignRequest.getCampaignName(), referenceDateWithDelta, reminderTime);
        campaignRequest.setStartTime(reminderTime);
        return campaignRequest;
    }
}
