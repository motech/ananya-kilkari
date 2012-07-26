package org.motechproject.ananya.kilkari.messagecampaign.contract.mapper;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.ananya.kilkari.messagecampaign.contract.MessageCampaignRequest;
import org.motechproject.ananya.kilkari.messagecampaign.domain.MessageCampaignPack;
import org.motechproject.ananya.kilkari.messagecampaign.utils.KilkariPropertiesData;
import org.motechproject.model.Time;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;

public class MessageCampaignRequestMapper {

    public static CampaignRequest newRequestFrom(MessageCampaignRequest messageCampaignRequest, KilkariPropertiesData kilkariProperties) {
        String campaignName = MessageCampaignPack.from(messageCampaignRequest.getSubscriptionPack()).getCampaignName();
        Time reminderTime = new Time(messageCampaignRequest.getSubscriptionCreationDate().plusMinutes(kilkariProperties.getCampaignScheduleDeltaMinutes()).toLocalTime());
        DateTime referenceDate = messageCampaignRequest.getSubscriptionCreationDate();

        LocalDate referenceDateWithDelta = referenceDate.plusDays(kilkariProperties.getCampaignScheduleDeltaDays()).toLocalDate();
        CampaignRequest campaignRequest = new CampaignRequest(messageCampaignRequest.getExternalId(), campaignName, reminderTime, referenceDateWithDelta);
        campaignRequest.setDeliverTime(reminderTime);
        return campaignRequest;
    }
}
