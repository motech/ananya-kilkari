package org.motechproject.ananya.kilkari.handlers;

import org.apache.log4j.Logger;
import org.motechproject.ananya.kilkari.domain.SubscriptionEventKeys;
import org.motechproject.ananya.kilkari.domain.SubscriptionRequest;
import org.motechproject.ananya.kilkari.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.service.OnMobileSubscriptionService;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProcessSubscriptionHandler {

    private OnMobileSubscriptionService onMobileSubscriptionService;

    Logger logger = Logger.getLogger(ProcessSubscriptionHandler.class);

    @Autowired
    public ProcessSubscriptionHandler(OnMobileSubscriptionService onMobileSubscriptionService) {
        this.onMobileSubscriptionService = onMobileSubscriptionService;
    }

    @MotechListener(subjects = {SubscriptionEventKeys.PROCESS_SUBSCRIPTION})
    public void handleProcessSubscription(MotechEvent event) {
        SubscriptionRequest subscriptionRequest = (SubscriptionRequest) event.getParameters().get("0");
        try {
            onMobileSubscriptionService.activateSubscription(subscriptionRequest);
        }
        catch (RuntimeException e) {
            logger.error("Exception Occurred while sending subscription activation request", e);
            throw e;
        }
    }
}