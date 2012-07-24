package org.motechproject.ananya.kilkari.messagecampaign.domain;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.kilkari.messagecampaign.service.MessageCampaignService;

public enum SubscriptionPack {
    FIFTEEN_MONTHS(MessageCampaignService.FIFTEEN_MONTHS_CAMPAIGN_KEY),
    TWELVE_MONTHS(MessageCampaignService.TWELVE_MONTHS_CAMPAIGN_KEY),
    SEVEN_MONTHS(MessageCampaignService.SEVEN_MONTHS_CAMPAIGN_KEY);

    private String campaignName;

    SubscriptionPack(String campaignName) {
        this.campaignName = campaignName;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public static SubscriptionPack from(String pack) {
        return SubscriptionPack.valueOf(StringUtils.trimToEmpty(pack).toUpperCase());
    }

    public static boolean isValid(String subscriptionPack) {
        try {
            from(subscriptionPack);
        } catch (Exception e) {
            return false;
        }
        return  true;
    }
}
