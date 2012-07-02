package org.motechproject.ananya.kilkari.messagecampaign.request;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollmentStatus;

public class KilkariMessageCampaignEnrollmentRecord {

    private String externalId;
    private String campaignName;
    private DateTime startDate;
    private String status;

    public KilkariMessageCampaignEnrollmentRecord(String externalId, String campaignName,
                                                  LocalDate startDate, CampaignEnrollmentStatus status) {

        this.externalId = externalId;
        this.campaignName = campaignName;
        this.startDate = new DateTime(startDate.toDate().getTime());
        this.status = status.toString();
    }

    public String getExternalId() {
        return externalId;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public DateTime getStartDate() {
        return startDate;
    }

    public String getStatus() {
        return status;
    }
}
