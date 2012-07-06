package org.motechproject.ananya.kilkari.messagecampaign.domain;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.kilkari.messagecampaign.service.KilkariMessageCampaignService;

public enum SubscriptionPack {
    FIFTEEN_MONTHS(KilkariMessageCampaignService.FIFTEEN_MONTHS),
    TWELVE_MONTHS(KilkariMessageCampaignService.TWELVE_MONTHS),
    SEVEN_MONTHS(KilkariMessageCampaignService.SEVEN_MONTHS);

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
