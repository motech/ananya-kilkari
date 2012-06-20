package org.motechproject.ananya.kilkari.messagecampaign.mapper;

import org.motechproject.ananya.kilkari.messagecampaign.request.KilkariMessageCampaignRequest;
import org.motechproject.model.Time;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;

public class KilkariMessageCampaignRequestMapper {

    public static CampaignRequest map(KilkariMessageCampaignRequest kilkariMessageCampaignRequest) {
        return new CampaignRequest(
                kilkariMessageCampaignRequest.getExternalId(),
                kilkariMessageCampaignRequest.getCampaignName(),
                new Time(kilkariMessageCampaignRequest.getReminderTime().toLocalTime()),
                kilkariMessageCampaignRequest.getReferenceDate().toLocalDate());
    }

}
