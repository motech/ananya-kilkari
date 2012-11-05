package org.motechproject.ananya.kilkari.domain;

import org.apache.commons.lang.StringUtils;

public enum CallbackStatus {
    FAILURE("FAILURE"), SUCCESS("SUCCESS"), ERROR("ERROR"), BAL_LOW("BAL-LOW"), GRACE("GRACE"), SUS("SUS");
    private String status;

    CallbackStatus(String name) {
        this.status = name;
    }

    public String getStatus() {
        return status;
    }

    public static CallbackStatus getFor(String status) {
        final String standardizedStatus = StringUtils.trimToEmpty(status).toUpperCase();
        return get(standardizedStatus);
    }

    public static boolean isValid(String callbackStatus) {
        return (callbackStatus != null && CallbackStatus.contains(callbackStatus));
    }

    private static boolean contains(String value) {
        return get(value) != null;
    }

    private static CallbackStatus get(String value) {
        for (CallbackStatus callbackStatus : CallbackStatus.values()) {
            if (callbackStatus.getStatus().equals(StringUtils.trimToEmpty(value).toUpperCase())) {
                return callbackStatus;
            }
        }
        return null;
    }
}
