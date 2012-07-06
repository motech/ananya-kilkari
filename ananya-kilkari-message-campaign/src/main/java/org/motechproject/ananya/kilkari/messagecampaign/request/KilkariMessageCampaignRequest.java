package org.motechproject.ananya.kilkari.messagecampaign.request;

import org.joda.time.DateTime;

public class KilkariMessageCampaignRequest {

    private String externalId;

    private String campaignName;

    DateTime reminderTime;

    DateTime referenceDate;

    private Integer startOffset;

    public KilkariMessageCampaignRequest(String externalId, String campaignName, DateTime reminderTime,
                                         DateTime referenceDate, Integer startOffset) {
        this.externalId = externalId;
        this.campaignName = campaignName;
        this.reminderTime = reminderTime;
        this.referenceDate = referenceDate;
        this.startOffset = startOffset;
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

    public Integer getStartOffset() {
        return startOffset;
    }
}
