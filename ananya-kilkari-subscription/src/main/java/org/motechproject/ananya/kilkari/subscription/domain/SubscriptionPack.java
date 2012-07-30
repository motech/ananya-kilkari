package org.motechproject.ananya.kilkari.subscription.domain;

import org.apache.commons.lang.StringUtils;

public enum SubscriptionPack {
    FIFTEEN_MONTHS(0, 60), TWELVE_MONTHS(12, 48), SEVEN_MONTHS(32, 28);
    private int startWeek;
    private int totalWeeks;
    private static final int MAX_WEEK = 60;

    SubscriptionPack(int startWeek, int totalWeeks) {
        this.startWeek = startWeek;
        this.totalWeeks = totalWeeks;
    }

    public int getStartWeek() {
        return startWeek;
    }

    public int getTotalWeeks() {
        return totalWeeks;
    }

    public static SubscriptionPack from(String pack) {
        return SubscriptionPack.valueOf(StringUtils.trimToEmpty(pack).toUpperCase());
    }

    public boolean isWeekWithinPackRange(Integer week) {
        return week >= startWeek && week <= MAX_WEEK;
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
