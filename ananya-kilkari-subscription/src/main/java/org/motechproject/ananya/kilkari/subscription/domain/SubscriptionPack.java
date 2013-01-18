package org.motechproject.ananya.kilkari.subscription.domain;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

public enum SubscriptionPack {
    BARI_KILKARI(1, 64) {
        @Override
        public DateTime getStartDate(DateTime dob) {
            return dob.minusWeeks(16);
        }

    }, NAVJAAT_KILKARI(17, 48) {
        @Override
        public DateTime getStartDate(DateTime dob) {
            return dob;
        }
    }, NANHI_KILKARI(37, 28) {
        @Override
        public DateTime getStartDate(DateTime dob) {
            return dob.plusWeeks(20);
        }
    };

    private static final int NUMBER_OF_WEEKS_IN_12_MONTHS = 48;
    private int startWeek;
    private int totalWeeks;

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

    public boolean isValidWeekNumber(Integer week) {
        return week >= 1 && week <= totalWeeks;
    }

    public boolean startsBefore(SubscriptionPack other) {
        return this.getStartWeek() < other.getStartWeek();
    }

    public static boolean isValid(String subscriptionPack) {
        try {
            from(subscriptionPack);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public abstract DateTime getStartDate(DateTime dob);

    public DateTime getStartDateForWeek(DateTime startDate, Integer weekNumber) {
        return startDate.minusWeeks(weekNumber - 1);
    }

    public boolean isValidDateOfBirth(DateTime dateOfBirth, DateTime createdAt) {
        return createdAt.minusWeeks(NUMBER_OF_WEEKS_IN_12_MONTHS).isBefore(dateOfBirth);
    }
}
