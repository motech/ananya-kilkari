package org.motechproject.ananya.kilkari.messagecampaign.request;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.joda.time.DateTime;

public class MessageCampaignRequest {

    private String externalId;
    private String campaignName;
    private DateTime scheduleStartDate;

    public MessageCampaignRequest(String externalId, String campaignName, DateTime scheduleStartDate) {
        this.externalId = externalId;
        this.campaignName = campaignName;
        this.scheduleStartDate = scheduleStartDate;
    }

    public String getExternalId() {
        return externalId;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public DateTime getScheduleStartDate() {
        return scheduleStartDate;
    }

    @Override
    public boolean equals(Object that) {
        return EqualsBuilder.reflectionEquals(this,that);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
