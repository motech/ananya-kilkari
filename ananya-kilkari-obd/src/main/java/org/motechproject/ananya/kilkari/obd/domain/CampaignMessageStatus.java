package org.motechproject.ananya.kilkari.obd.domain;

import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;

public enum CampaignMessageStatus {
    NEW(4), NA(1), ND(3), SO(2), SUCCESS(Integer.MAX_VALUE);

    private Integer priority;

    CampaignMessageStatus(Integer priority) {
        this.priority = priority;
    }

    public Integer getPriority() {
        return priority;
    }

    public static CampaignMessageStatus getFor(String statusCode) {
        final String standardizedStatusCode = StringUtils.trimToEmpty(statusCode).toUpperCase();
        return isValid(standardizedStatusCode) ? CampaignMessageStatus.valueOf(standardizedStatusCode) : null;
    }

    public static boolean isValid(String statusCode) {
        return (statusCode != null && CampaignMessageStatus.contains(statusCode));
    }

    public static List<CampaignMessageStatus> getFailedStatusCodes() {
        return Arrays.asList(ND, NA, SO);
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
