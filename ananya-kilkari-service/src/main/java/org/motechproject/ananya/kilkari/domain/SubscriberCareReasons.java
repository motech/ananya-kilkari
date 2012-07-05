package org.motechproject.ananya.kilkari.domain;

import org.apache.commons.lang.StringUtils;

public enum SubscriberCareReasons {
    CHANGE_PACK;

    public static SubscriberCareReasons getFor(String action) {
        return SubscriberCareReasons.valueOf(StringUtils.trimToEmpty(action).toUpperCase());
    }

    public static boolean isValid(String subscriberCareReason) {
        return (subscriberCareReason != null && SubscriberCareReasons.contains(subscriberCareReason));
    }

    private static boolean contains(String value) {
        for (SubscriberCareReasons subscriberCareReason : SubscriberCareReasons.values()) {
            if (subscriberCareReason.name().equals(StringUtils.trimToEmpty(value).toUpperCase())) {
                return true;
            }
        }
        return false;
    }
}
