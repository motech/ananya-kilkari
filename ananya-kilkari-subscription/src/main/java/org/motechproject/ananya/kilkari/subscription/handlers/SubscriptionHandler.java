package org.motechproject.ananya.kilkari.subscription.handlers;

import org.motechproject.ananya.kilkari.subscription.domain.ProcessSubscriptionRequest;
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
        ProcessSubscriptionRequest processSubscriptionRequest = (ProcessSubscriptionRequest) event.getParameters().get("0");
        logger.info(String.format("Handling subscription activation event for subscriptionid: %s, msisdn: %s, pack: %s, channel: %s", processSubscriptionRequest.getSubscriptionId(), processSubscriptionRequest.getMsisdn(), processSubscriptionRequest.getPack(), processSubscriptionRequest.getChannel()));
        onMobileSubscriptionGateway.activateSubscription(processSubscriptionRequest);
        subscriptionService.activationRequested(processSubscriptionRequest.getSubscriptionId());
    }

    @MotechListener(subjects = {SubscriptionEventKeys.DEACTIVATE_SUBSCRIPTION})
    public void handleSubscriptionDeactivation(MotechEvent event) {
        ProcessSubscriptionRequest processSubscriptionRequest = (ProcessSubscriptionRequest) event.getParameters().get("0");
        logger.info(String.format("Handling subscription deactivation event for subscriptionid: %s, msisdn: %s, pack: %s, channel: %s", processSubscriptionRequest.getSubscriptionId(), processSubscriptionRequest.getMsisdn(), processSubscriptionRequest.getPack(), processSubscriptionRequest.getChannel()));
        onMobileSubscriptionGateway.deactivateSubscription(processSubscriptionRequest);
        subscriptionService.deactivationRequested(processSubscriptionRequest.getSubscriptionId());
    }

    @MotechListener(subjects = {SubscriptionEventKeys.SUBSCRIPTION_COMPLETE})
    public void handleSubscriptionComplete(MotechEvent event) {
        ProcessSubscriptionRequest processSubscriptionRequest = (ProcessSubscriptionRequest) event.getParameters().get("0");
        logger.info(String.format("Handling subscription completion event for subscriptionid: %s, msisdn: %s, pack: %s", processSubscriptionRequest.getSubscriptionId(), processSubscriptionRequest.getMsisdn(), processSubscriptionRequest.getPack()));
        onMobileSubscriptionGateway.deactivateSubscription(processSubscriptionRequest);
        subscriptionService.subscriptionComplete(processSubscriptionRequest.getSubscriptionId());
    }

    @MotechListener(subjects = {SubscriptionEventKeys.DELETE_INBOX})
    public void handleInboxDeletion(MotechEvent event) {
        ProcessSubscriptionRequest processSubscriptionRequest = (ProcessSubscriptionRequest) event.getParameters().get("0");
        logger.info(String.format("Handling inbox deletion event for subscriptionid: %s, msisdn: %s, pack: %s", processSubscriptionRequest.getSubscriptionId(), processSubscriptionRequest.getMsisdn(), processSubscriptionRequest.getPack()));
        subscriptionService.deleteInbox(processSubscriptionRequest.getSubscriptionId());
    }
}