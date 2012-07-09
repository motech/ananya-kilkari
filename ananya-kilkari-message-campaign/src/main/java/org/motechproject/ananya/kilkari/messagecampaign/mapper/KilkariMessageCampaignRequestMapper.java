package org.motechproject.ananya.kilkari.messagecampaign.mapper;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.ananya.kilkari.messagecampaign.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.messagecampaign.request.KilkariMessageCampaignRequest;
import org.motechproject.ananya.kilkari.messagecampaign.service.KilkariMessageCampaignService;
import org.motechproject.model.Time;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;

public class KilkariMessageCampaignRequestMapper {

    public static CampaignRequest newRequestFrom(KilkariMessageCampaignRequest kilkariMessageCampaignRequest) {
        String campaignName = SubscriptionPack.from(kilkariMessageCampaignRequest.getSubscriptionPack()).getCampaignName();
        Time reminderTime = new Time(kilkariMessageCampaignRequest.getSubscriptionCreationDate().plusMinutes(KilkariMessageCampaignService.campaignScheduleDeltaMinutes).toLocalTime());
        DateTime referenceDate = kilkariMessageCampaignRequest.getSubscriptionCreationDate();

        LocalDate referenceDateWithDelta = referenceDate.plusDays(KilkariMessageCampaignService.campaignScheduleDeltaDays).toLocalDate();
        CampaignRequest campaignRequest = new CampaignRequest(kilkariMessageCampaignRequest.getExternalId(), campaignName, reminderTime, referenceDateWithDelta);
        campaignRequest.setDeliverTime(reminderTime);
        return campaignRequest;
    }
}
