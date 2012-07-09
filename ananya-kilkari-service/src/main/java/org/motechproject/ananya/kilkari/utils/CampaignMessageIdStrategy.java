package org.motechproject.ananya.kilkari.utils;

import org.joda.time.DateTime;
import org.joda.time.Weeks;
import org.motechproject.ananya.kilkari.domain.Subscription;
import org.springframework.stereotype.Component;

@Component
public class CampaignMessageIdStrategy {

    private static final String MESSAGE_ID_FORMAT = "Week%s";

    public String createMessageId(Subscription subscription) {
        int weeksDifference = Weeks.weeksBetween(subscription.getCreationDate(), DateTime.now()).getWeeks();
        weeksDifference = adjustPackStartWeek(weeksDifference, subscription);

        return String.format(CampaignMessageIdStrategy.MESSAGE_ID_FORMAT, weeksDifference + 1);
    }

    private int adjustPackStartWeek(int weeksDifference, Subscription subscription) {
        return weeksDifference + subscription.getPack().getStartWeek();
    }
}
