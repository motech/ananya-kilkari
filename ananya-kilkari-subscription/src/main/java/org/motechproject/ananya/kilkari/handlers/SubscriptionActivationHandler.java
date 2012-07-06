package org.motechproject.ananya.kilkari.handlers;

import org.motechproject.ananya.kilkari.domain.SubscriptionActivationRequest;
import org.motechproject.ananya.kilkari.domain.SubscriptionEventKeys;
import org.motechproject.ananya.kilkari.service.OnMobileSubscriptionService;
import org.motechproject.ananya.kilkari.service.SubscriptionService;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionActivationHandler {

    private OnMobileSubscriptionService onMobileSubscriptionService;

    private final static Logger logger = LoggerFactory.getLogger(SubscriptionActivationHandler.class);

    private SubscriptionService subscriptionService;

    @Autowired
    public SubscriptionActivationHandler(OnMobileSubscriptionService onMobileSubscriptionService, SubscriptionService subscriptionService) {
        this.onMobileSubscriptionService = onMobileSubscriptionService;
        this.subscriptionService = subscriptionService;
    }

    @MotechListener(subjects = {SubscriptionEventKeys.PROCESS_SUBSCRIPTION})
    public void handleProcessSubscription(MotechEvent event) {
        SubscriptionActivationRequest subscriptionActivationRequest = (SubscriptionActivationRequest) event.getParameters().get("0");
        logger.info(String.format("Handling process subscription event for msisdn: %s, pack: %s, channel: %s", subscriptionActivationRequest.getMsisdn(), subscriptionActivationRequest.getPack(), subscriptionActivationRequest.getChannel()));
        onMobileSubscriptionService.activateSubscription(subscriptionActivationRequest);
        subscriptionService.activationRequested(subscriptionActivationRequest.getSubscriptionId());
    }
}