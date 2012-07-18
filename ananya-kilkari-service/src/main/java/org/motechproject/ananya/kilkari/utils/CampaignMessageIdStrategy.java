package org.motechproject.ananya.kilkari.utils;

import org.joda.time.DateTime;
import org.joda.time.Weeks;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.springframework.stereotype.Component;

@Component
public class CampaignMessageIdStrategy {

    private static final String MESSAGE_ID_FORMAT = "WEEK%s";

    public String createMessageId(Subscription subscription) {
        return String.format(CampaignMessageIdStrategy.MESSAGE_ID_FORMAT, currentWeek(subscription));
    }

    public boolean hasPackBeenCompleted(Subscription subscription) {
        int startWeek = subscription.getPack().getStartWeek();
        int totalWeeks = subscription.getPack().getTotalWeeks();
        int currentWeek = currentWeek(subscription);

        return currentWeek >= totalWeeks + startWeek;
    }

    private int currentWeek(Subscription subscription) {
        int weeksDifference = Weeks.weeksBetween(subscription.getCreationDate(), DateTime.now()).getWeeks();
        weeksDifference = adjustPackStartWeek(weeksDifference, subscription);

        return weeksDifference + 1;
    }

    private int adjustPackStartWeek(int weeksDifference, Subscription subscription) {
        return weeksDifference + subscription.getPack().getStartWeek();
    }
}
