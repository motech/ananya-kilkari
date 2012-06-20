package org.motechproject.ananya.kilkari.messagecampaign.request;

import org.joda.time.DateTime;

public class KilkariMessageCampaignRequest {

    private String externalId;

    private String campaignName;

    DateTime reminderTime;

    DateTime referenceDate;

    public KilkariMessageCampaignRequest(String externalId, String campaignName, DateTime reminderTime, DateTime referenceDate) {
        this.externalId = externalId;
        this.campaignName = campaignName;
        this.reminderTime = reminderTime;
        this.referenceDate = referenceDate;
    }

    public String getExternalId() {
        return externalId;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public DateTime getReminderTime() {
        return reminderTime;
    }

    public DateTime getReferenceDate() {
        return referenceDate;
    }
}
