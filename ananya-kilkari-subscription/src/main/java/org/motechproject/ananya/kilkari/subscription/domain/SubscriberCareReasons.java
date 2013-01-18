package org.motechproject.ananya.kilkari.subscription.domain;

import org.apache.commons.lang.StringUtils;

public enum SubscriberCareReasons {
    HELP, NEW_SUBSCRIPTION;

    public static SubscriberCareReasons getFor(String reason) {
        final String standardizedReason = StringUtils.trimToEmpty(reason).toUpperCase();
        return isValid(standardizedReason) ? SubscriberCareReasons.valueOf(standardizedReason) : null;
    }

    public static boolean isValid(String subscriberCareReason) {
        return SubscriberCareReasons.contains(subscriberCareReason);
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
