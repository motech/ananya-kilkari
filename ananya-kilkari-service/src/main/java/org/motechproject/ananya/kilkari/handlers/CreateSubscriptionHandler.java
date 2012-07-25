package org.motechproject.ananya.kilkari.handlers;

import org.motechproject.ananya.kilkari.service.KilkariSubscriptionService;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionEventKeys;
import org.motechproject.ananya.kilkari.request.SubscriptionRequest;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class CreateSubscriptionHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(CreateSubscriptionHandler.class);

    @Autowired
    private KilkariSubscriptionService kilkariSubscriptionService;

    @Autowired
    public CreateSubscriptionHandler(KilkariSubscriptionService kilkariSubscriptionService) {
        this.kilkariSubscriptionService = kilkariSubscriptionService;
    }

    @MotechListener(subjects = {SubscriptionEventKeys.CREATE_SUBSCRIPTION})
    public void handleCreateSubscription(MotechEvent event) {
        SubscriptionRequest subscriptionRequest = (SubscriptionRequest) event.getParameters().get("0");
        LOGGER.info(String.format("Create subscription event for msisdn: %s, pack: %s, channel: %s",
                subscriptionRequest.getMsisdn(), subscriptionRequest.getPack(), subscriptionRequest.getChannel()));

        kilkariSubscriptionService.createSubscription(subscriptionRequest);
    }
}
