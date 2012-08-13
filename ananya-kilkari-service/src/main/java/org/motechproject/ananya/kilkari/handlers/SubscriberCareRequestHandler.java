package org.motechproject.ananya.kilkari.handlers;

import org.apache.log4j.Logger;
import org.motechproject.ananya.kilkari.subscription.service.request.SubscriberCareRequest;
import org.motechproject.ananya.kilkari.service.KilkariSubscriberCareService;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionEventKeys;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SubscriberCareRequestHandler {

    Logger logger = Logger.getLogger(SubscriberCareRequestHandler.class);
    private KilkariSubscriberCareService kilkariSubscriberCareService;

    @Autowired
    public SubscriberCareRequestHandler(KilkariSubscriberCareService kilkariSubscriberCareService) {
        this.kilkariSubscriberCareService = kilkariSubscriberCareService;
    }

    @MotechListener(subjects = {SubscriptionEventKeys.PROCESS_SUBSCRIBER_CARE_REQUEST})
    public void handleSubscriberCareRequest(MotechEvent motechEvent) {
        SubscriberCareRequest subscriberCareRequest = (SubscriberCareRequest) motechEvent.getParameters().get("0");
        logger.info(String.format("Create subscriber care request event for msisdn: %s, reason: %s, channel:%s, createdAt: %s",
                subscriberCareRequest.getMsisdn(), subscriberCareRequest.getReason(),
                subscriberCareRequest.getChannel(), subscriberCareRequest.getCreatedAt()));
        kilkariSubscriberCareService.createSubscriberCareRequest(subscriberCareRequest);
    }
}
