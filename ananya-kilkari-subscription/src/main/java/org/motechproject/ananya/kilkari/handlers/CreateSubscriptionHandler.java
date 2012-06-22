package org.motechproject.ananya.kilkari.handlers;

import org.motechproject.ananya.kilkari.domain.SubscriptionEventKeys;
import org.motechproject.ananya.kilkari.domain.SubscriptionRequest;
import org.motechproject.ananya.kilkari.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.service.SubscriptionService;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CreateSubscriptionHandler {
    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    public CreateSubscriptionHandler(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @MotechListener(subjects = {SubscriptionEventKeys.CREATE_SUBSCRIPTION})
    public void handleCreateSubscription(MotechEvent event) throws ValidationException {
        SubscriptionRequest subscriptionRequest = (SubscriptionRequest) event.getParameters().get("0");
        try {
            subscriptionService.createSubscription(subscriptionRequest);
        } catch (ValidationException e) {
            // log here
            throw e;
        }
    }
}
