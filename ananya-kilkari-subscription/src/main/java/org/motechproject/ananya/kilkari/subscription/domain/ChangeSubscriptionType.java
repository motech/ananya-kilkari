package org.motechproject.ananya.kilkari.subscription.domain;


import org.apache.commons.lang.StringUtils;

public enum ChangeSubscriptionType {
    CHANGE_PACK, CHANGE_SCHEDULE, CHANGE_REFERRED_BY;

    public static ChangeSubscriptionType from(String changeType) {
        return ChangeSubscriptionType.valueOf(StringUtils.trimToEmpty(changeType).toUpperCase());
    }

    public static boolean isValid(String changeType) {
        try {
            from(changeType);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean isChangePack(ChangeSubscriptionType type) {
        return ChangeSubscriptionType.CHANGE_PACK.equals(type);
    }

    public static boolean isChangeReferredBy(ChangeSubscriptionType type) {
        return ChangeSubscriptionType.CHANGE_REFERRED_BY.equals(type);
    }
}


