package org.motechproject.ananya.kilkari.subscription.handlers;

import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionActivationRequest;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionEventKeys;
import org.motechproject.ananya.kilkari.subscription.gateway.OnMobileSubscriptionGateway;
import org.motechproject.ananya.kilkari.subscription.service.SubscriptionService;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionActivationHandler {

    private OnMobileSubscriptionGateway onMobileSubscriptionGateway;

    private final static Logger logger = LoggerFactory.getLogger(SubscriptionActivationHandler.class);

    private SubscriptionService subscriptionService;

    @Autowired
    public SubscriptionActivationHandler(OnMobileSubscriptionGateway onMobileSubscriptionGateway, SubscriptionService subscriptionService) {
        this.onMobileSubscriptionGateway = onMobileSubscriptionGateway;
        this.subscriptionService = subscriptionService;
    }

    @MotechListener(subjects = {SubscriptionEventKeys.PROCESS_SUBSCRIPTION})
    public void handleProcessSubscription(MotechEvent event) {
        SubscriptionActivationRequest subscriptionActivationRequest = (SubscriptionActivationRequest) event.getParameters().get("0");
        logger.info(String.format("Handling process subscription event for subscriptionid: %s, msisdn: %s, pack: %s, channel: %s", subscriptionActivationRequest.getSubscriptionId(), subscriptionActivationRequest.getMsisdn(), subscriptionActivationRequest.getPack(), subscriptionActivationRequest.getChannel()));
        onMobileSubscriptionGateway.activateSubscription(subscriptionActivationRequest);
        subscriptionService.activationRequested(subscriptionActivationRequest.getSubscriptionId());
    }
}