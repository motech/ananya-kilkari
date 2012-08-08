package org.motechproject.ananya.kilkari.web.mapper;

import org.motechproject.ananya.kilkari.message.service.InboxService;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.web.response.SubscriptionDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionDetailsMapper {
    private InboxService inboxService;

    @Autowired
    public SubscriptionDetailsMapper(InboxService inboxService) {
        this.inboxService = inboxService;
    }

    public SubscriptionDetails mapFrom(Subscription subscription) {
        String messageId = inboxService.getMessageFor(subscription.getSubscriptionId());
        return new SubscriptionDetails(subscription.getSubscriptionId(), subscription.getPack().name(), subscription.getStatus().getDisplayString(), messageId);
    }
}
