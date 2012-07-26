package org.motechproject.ananya.kilkari.web.mapper;

import org.motechproject.ananya.kilkari.subscription.service.KilkariInboxService;
import org.motechproject.ananya.kilkari.subscription.service.response.ISubscription;
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

    public SubscriptionDetails mapFrom(ISubscription subscriptionResponse) {
        String messageId = kilkariInboxService.getMessageFor(subscriptionResponse.getSubscriptionId());
        return new SubscriptionDetails(subscriptionResponse.getSubscriptionId(), subscriptionResponse.getPack().name(), subscriptionResponse.getStatus().name(), messageId);
    }
}
