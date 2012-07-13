package org.motechproject.ananya.kilkari.obd.domain;

import org.apache.commons.lang.StringUtils;

public enum ServiceOption {
    HELP, UNSUBSCRIBE;

    public static ServiceOption getFor(String option) {
        final String standardizedOption = StringUtils.trimToEmpty(option).toUpperCase();
        return isValid(standardizedOption) ? ServiceOption.valueOf(standardizedOption) : null;
    }

    public static boolean isValid(String option) {
        return (option != null && ServiceOption.contains(option));
    }

    private static boolean contains(String value) {
        for (ServiceOption serviceOption : ServiceOption.values()) {
            if (serviceOption.name().equals(StringUtils.trimToEmpty(value).toUpperCase())) {
                return true;
            }
        }
        return false;
    }
}
