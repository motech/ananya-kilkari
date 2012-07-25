package org.motechproject.ananya.kilkari.subscription.domain;

import org.apache.commons.lang.StringUtils;

public enum CampaignChangeReason {
    ID, MC;

    public static CampaignChangeReason from(String reason) {
        return CampaignChangeReason.valueOf(StringUtils.trimToEmpty(reason).toUpperCase());
    }

    public static boolean isValid(String reason) {
        try {
            from(reason);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
