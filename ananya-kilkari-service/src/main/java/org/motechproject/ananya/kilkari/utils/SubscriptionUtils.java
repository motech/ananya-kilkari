package org.motechproject.ananya.kilkari.utils;

import org.joda.time.DateTime;
import org.joda.time.Weeks;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;

public class SubscriptionUtils {
    public static boolean hasPackBeenCompleted(Subscription subscription) {
        int startWeek = subscription.getPack().getStartWeek();
        int totalWeeks = subscription.getPack().getTotalWeeks();
        int currentWeek = currentWeek(subscription);

        return currentWeek >= totalWeeks + startWeek;
    }

    public static int currentWeek(Subscription subscription) {
        int weeksDifference = Weeks.weeksBetween(subscription.getCreationDate(), DateTime.now()).getWeeks();
        weeksDifference = adjustPackStartWeek(weeksDifference, subscription);

        return weeksDifference + 1;
    }

    private static int adjustPackStartWeek(int weeksDifference, Subscription subscription) {
        return weeksDifference + subscription.getPack().getStartWeek();
    }
}
