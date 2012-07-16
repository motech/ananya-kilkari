package org.motechproject.ananya.kilkari.subscription.mappers;

import org.motechproject.ananya.kilkari.subscription.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.domain.ProcessSubscriptionRequest;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;

public class SubscriptionMapper {
    public static ProcessSubscriptionRequest mapFrom(Subscription subscription, Channel channel) {
        return new ProcessSubscriptionRequest(subscription.getMsisdn(), subscription.getPack(), channel, subscription.getSubscriptionId());
    }
}
