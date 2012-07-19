package org.motechproject.ananya.kilkari.utils;

import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.springframework.stereotype.Component;

@Component
public class CampaignMessageIdStrategy {

    private static final String MESSAGE_ID_FORMAT = "WEEK%s";

    public String createMessageId(Subscription subscription) {

        return String.format(CampaignMessageIdStrategy.MESSAGE_ID_FORMAT, subscription.currentWeek());
    }
}
