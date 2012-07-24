package org.motechproject.ananya.kilkari.obd.domain;

import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;

public enum CampaignMessageStatus {
    NEW, DNP, DNC, SUCCESS;

    public static CampaignMessageStatus getFor(String statusCode) {
        final String standardizedStatusCode = StringUtils.trimToEmpty(statusCode).toUpperCase();
        return isValid(standardizedStatusCode) ? CampaignMessageStatus.valueOf(standardizedStatusCode) : null;
    }

    public static boolean isValid(String statusCode) {
        return (statusCode != null && CampaignMessageStatus.contains(statusCode));
    }
    
    public static List<CampaignMessageStatus> getFailedStatusCodes(){
        return Arrays.asList(DNC,DNP);

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
