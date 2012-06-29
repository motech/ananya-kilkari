package org.motechproject.ananya.kilkari.domain;

import org.apache.commons.lang.StringUtils;

public enum SubscriptionPack {
    FIFTEEN_MONTHS, TWELVE_MONTHS, SEVEN_MONTHS;

    public static SubscriptionPack from(String pack) {
        return SubscriptionPack.valueOf(StringUtils.trimToEmpty(pack).toUpperCase());
    }

    public static boolean isValid(String subscriptionPack) {
        try {
            from(subscriptionPack);
        } catch (Exception e) {
            return false;
        }
        return  true;
    }
}
