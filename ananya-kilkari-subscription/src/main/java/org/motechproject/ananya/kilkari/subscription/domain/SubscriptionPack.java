package org.motechproject.ananya.kilkari.subscription.domain;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Weeks;

public enum SubscriptionPack {
    FIFTEEN_MONTHS(0, 60) {
        @Override
        public DateTime adjustStartDate(DateTime dob) {
            Weeks weeks = Weeks.weeksBetween(dob.minusMonths(3), dob);
            return dob.minusWeeks(weeks.getWeeks());
        }
    }, TWELVE_MONTHS(12, 48) {
        @Override
        public DateTime adjustStartDate(DateTime dob) {
            return dob;
        }
    }, SEVEN_MONTHS(32, 28) {
        @Override
        public DateTime adjustStartDate(DateTime dob) {
            Weeks weeks = Weeks.weeksBetween(dob, dob.plusMonths(5));
            return dob.plusWeeks(weeks.getWeeks());
        }
    };
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

    public static boolean isValid(String subscriptionPack) {
        try {
            from(subscriptionPack);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public abstract DateTime adjustStartDate(DateTime dob);

    public DateTime adjustStartDate(DateTime creationDate, Integer weekNumber) {
        return creationDate.minusWeeks(weekNumber - 1);
    }
}
