package org.motechproject.ananya.kilkari.messagecampaign.mapper;

import org.motechproject.ananya.kilkari.messagecampaign.request.KilkariMessageCampaignEnrollmentRecord;
import org.motechproject.server.messagecampaign.service.CampaignEnrollmentRecord;

public class KilkariMessageCampaignEnrollmentRecordMapper {

    public static KilkariMessageCampaignEnrollmentRecord map(CampaignEnrollmentRecord campaignEnrollmentRecord) {
        return new KilkariMessageCampaignEnrollmentRecord(campaignEnrollmentRecord.getExternalId(),
                campaignEnrollmentRecord.getCampaignName(), campaignEnrollmentRecord.getStartDate(),
                campaignEnrollmentRecord.getStatus());
    }

}
