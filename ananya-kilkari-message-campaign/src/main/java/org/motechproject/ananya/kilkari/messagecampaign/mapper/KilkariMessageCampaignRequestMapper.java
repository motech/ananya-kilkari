package org.motechproject.ananya.kilkari.messagecampaign.mapper;

import org.motechproject.ananya.kilkari.messagecampaign.request.KilkariMessageCampaignRequest;
import org.motechproject.model.Time;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;

public class KilkariMessageCampaignRequestMapper {

    public static CampaignRequest newRequestFrom(KilkariMessageCampaignRequest kilkariMessageCampaignRequest) {
        return new CampaignRequest(
                kilkariMessageCampaignRequest.getExternalId(),
                kilkariMessageCampaignRequest.getCampaignName(),
                //TODO katta/sush pass the reminder time from the service itself
//                new Time(kilkariMessageCampaignRequest.getReminderTime().toLocalTime()),
                null,
                kilkariMessageCampaignRequest.getReferenceDate().toLocalDate(),
                kilkariMessageCampaignRequest.getStartOffset());
    }

}
