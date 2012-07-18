package org.motechproject.ananya.kilkari.obd.domain;

import org.apache.commons.lang.StringUtils;

public enum CampaignMessageStatus {
    NEW, DNP, DNC;

    public static CampaignMessageStatus getFor(String statusCode) {
        final String standardizedStatusCode = StringUtils.trimToEmpty(statusCode).toUpperCase();
        return isValid(standardizedStatusCode) ? CampaignMessageStatus.valueOf(standardizedStatusCode) : null;
    }

    public static boolean isValid(String statusCode) {
        return (statusCode != null && CampaignMessageStatus.contains(statusCode));
    }

    private static boolean contains(String value) {
        for (CampaignMessageStatus statusCode : CampaignMessageStatus.values()) {
            if (statusCode.name().equals(StringUtils.trimToEmpty(value).toUpperCase())) {
                return true;
            }
        }
        return false;
    }
}
