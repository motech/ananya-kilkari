package org.motechproject.ananya.kilkari.subscription.handlers;

import org.motechproject.ananya.kilkari.subscription.request.OMSubscriptionRequest;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
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
public class SubscriptionHandler {

    private OnMobileSubscriptionGateway onMobileSubscriptionGateway;

    private final static Logger logger = LoggerFactory.getLogger(SubscriptionHandler.class);

    private SubscriptionService subscriptionService;

    @Autowired
    public SubscriptionHandler(OnMobileSubscriptionGateway onMobileSubscriptionGateway, SubscriptionService subscriptionService) {
        this.onMobileSubscriptionGateway = onMobileSubscriptionGateway;
        this.subscriptionService = subscriptionService;
    }

    @MotechListener(subjects = {SubscriptionEventKeys.ACTIVATE_SUBSCRIPTION})
    public void handleSubscriptionActivation(MotechEvent event) {
        OMSubscriptionRequest OMSubscriptionRequest = (OMSubscriptionRequest) event.getParameters().get("0");
        logger.info(String.format("Handling subscription activation event for subscriptionid: %s, msisdn: %s, pack: %s, channel: %s", OMSubscriptionRequest.getSubscriptionId(), OMSubscriptionRequest.getMsisdn(), OMSubscriptionRequest.getPack(), OMSubscriptionRequest.getChannel()));
        onMobileSubscriptionGateway.activateSubscription(OMSubscriptionRequest);
        subscriptionService.activationRequested(OMSubscriptionRequest.getSubscriptionId());
    }

    @MotechListener(subjects = {SubscriptionEventKeys.DEACTIVATE_SUBSCRIPTION})
    public void handleSubscriptionDeactivation(MotechEvent event) {
        OMSubscriptionRequest OMSubscriptionRequest = (OMSubscriptionRequest) event.getParameters().get("0");
        logger.info(String.format("Handling subscription deactivation event for subscriptionid: %s, msisdn: %s, pack: %s, channel: %s", OMSubscriptionRequest.getSubscriptionId(), OMSubscriptionRequest.getMsisdn(), OMSubscriptionRequest.getPack(), OMSubscriptionRequest.getChannel()));
        onMobileSubscriptionGateway.deactivateSubscription(OMSubscriptionRequest);
        subscriptionService.deactivationRequested(OMSubscriptionRequest.getSubscriptionId());
    }

    @MotechListener(subjects = {SubscriptionEventKeys.SUBSCRIPTION_COMPLETE})
    public void handleSubscriptionComplete(MotechEvent event) {
        OMSubscriptionRequest OMSubscriptionRequest = (OMSubscriptionRequest) event.getParameters().get("0");
        logger.info(String.format("Handling subscription completion event for subscriptionid: %s, msisdn: %s, pack: %s", OMSubscriptionRequest.getSubscriptionId(), OMSubscriptionRequest.getMsisdn(), OMSubscriptionRequest.getPack()));

        Subscription subscription = subscriptionService.findBySubscriptionId(OMSubscriptionRequest.getSubscriptionId());
        if (subscription.isInDeactivatedState()) {
            logger.info(String.format("Cannot unsubscribe for subscriptionid: %s  msisdn: %s as it is already in the %s state", OMSubscriptionRequest.getSubscriptionId(), OMSubscriptionRequest.getMsisdn(), subscription.getStatus()));
            return;
        }

        onMobileSubscriptionGateway.deactivateSubscription(OMSubscriptionRequest);
        subscriptionService.subscriptionComplete(OMSubscriptionRequest.getSubscriptionId());
    }
}