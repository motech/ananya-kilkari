package org.motechproject.ananya.kilkari.domain;

import org.apache.commons.lang.StringUtils;

public enum CallbackStatus {
    FAILURE, SUCCESS, ERROR, BAL_LOW, GRACE, SUS;

    public static CallbackStatus getFor(String status) {
        final String standardizedStatus = StringUtils.trimToEmpty(status).toUpperCase();
        return isValid(standardizedStatus) ? CallbackStatus.valueOf(standardizedStatus) : null;
    }

    public static boolean isValid(String callbackStatus) {
        return (callbackStatus != null && CallbackStatus.contains(callbackStatus));
    }

    private static boolean contains(String value) {
        for (CallbackStatus callbackStatus : CallbackStatus.values()) {
            if (callbackStatus.name().equals(StringUtils.trimToEmpty(value).toUpperCase())) {
                return true;
            }
        }
        return false;
    }
}
