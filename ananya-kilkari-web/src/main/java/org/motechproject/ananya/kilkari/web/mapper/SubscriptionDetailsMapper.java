package org.motechproject.ananya.kilkari.web.mapper;

import org.motechproject.ananya.kilkari.subscription.service.KilkariInboxService;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.web.response.SubscriptionDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionDetailsMapper {
    private KilkariInboxService kilkariInboxService;

    @Autowired
    public SubscriptionDetailsMapper(KilkariInboxService kilkariInboxService) {
        this.kilkariInboxService = kilkariInboxService;
    }

    public SubscriptionDetails mapFrom(Subscription subscription) {
        String messageId = kilkariInboxService.getMessageFor(subscription.getSubscriptionId());
        return new SubscriptionDetails(subscription.getSubscriptionId(), subscription.getPack().name(), subscription.getStatus().name(), messageId);
    }
}
