package org.motechproject.ananya.kilkari.obd.domain;

import org.apache.commons.lang.StringUtils;

public enum CampaignCode {
    WEEK, MC, ID;

    public static CampaignCode getFor(String code) {
        final String standardizedCode = StringUtils.trimToEmpty(code).toUpperCase();
        return isValid(standardizedCode) ? CampaignCode.valueOf(standardizedCode) : null;
    }

    public static boolean isValid(String code) {
        return (code != null && CampaignCode.contains(code));
    }

    private static boolean contains(String value) {
        for (CampaignCode campaignCode : CampaignCode.values()) {
            if (campaignCode.name().equals(StringUtils.trimToEmpty(value).toUpperCase())) {
                return true;
            }
        }
        return false;
    }
}
