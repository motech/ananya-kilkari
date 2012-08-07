package org.motechproject.ananya.kilkari.messagecampaign.domain;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.kilkari.messagecampaign.service.MessageCampaignService;

public enum MessageCampaignPack {
    BARI_KILKARI(MessageCampaignService.FIFTEEN_MONTHS_CAMPAIGN_KEY),
    CHOTI_KILKARI(MessageCampaignService.TWELVE_MONTHS_CAMPAIGN_KEY),
    NANHI_KILKARI(MessageCampaignService.SEVEN_MONTHS_CAMPAIGN_KEY),
    INFANT_DEATH(MessageCampaignService.INFANT_DEATH_CAMPAIGN_KEY),
    MISCARRIAGE(MessageCampaignService.MISCARRIAGE_CAMPAIGN_KEY);

    private String campaignName;

    MessageCampaignPack(String campaignName) {
        this.campaignName = campaignName;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public static MessageCampaignPack from(String pack) {
        return MessageCampaignPack.valueOf(StringUtils.trimToEmpty(pack).toUpperCase());
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
