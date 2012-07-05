package org.motechproject.ananya.kilkari.handlers;

import org.motechproject.ananya.kilkari.domain.SubscriptionEventKeys;
import org.motechproject.ananya.kilkari.domain.SubscriptionCreationReportRequest;
import org.motechproject.ananya.kilkari.domain.SubscriptionStateChangeReportRequest;
import org.motechproject.ananya.kilkari.service.IReportingService;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionReportHandler {

    private final static Logger logger = LoggerFactory.getLogger(SubscriptionReportHandler.class);
    private IReportingService reportingService;

    @Autowired
    public SubscriptionReportHandler(IReportingService reportingService) {
        this.reportingService = reportingService;
    }

    @MotechListener(subjects = {SubscriptionEventKeys.REPORT_SUBSCRIPTION_CREATION})
    public void handleSubscriptionCreation(MotechEvent event) {
        SubscriptionCreationReportRequest subscriptionCreationReportRequest = (SubscriptionCreationReportRequest) event.getParameters().get("0");
        logger.info(String.format("Handling report subscription creation event for msisdn: %s, pack: %s, channel: %s", subscriptionCreationReportRequest.getMsisdn(), subscriptionCreationReportRequest.getPack(), subscriptionCreationReportRequest.getChannel()));
        reportingService.createSubscription(subscriptionCreationReportRequest);
    }

    @MotechListener(subjects = {SubscriptionEventKeys.REPORT_SUBSCRIPTION_STATE_CHANGE})
    public void handleSubscriptionStateChange(MotechEvent event) {
        SubscriptionStateChangeReportRequest subscriptionStateChangeReportRequest = (SubscriptionStateChangeReportRequest) event.getParameters().get("0");
        logger.info(String.format("Handling report subscription state change event for subscripitonId: %s, subscriptionStatus: %s", subscriptionStateChangeReportRequest.getSubscriptionId(), subscriptionStateChangeReportRequest.getSubscriptionStatus()));
        reportingService.updateSubscriptionStateChange(subscriptionStateChangeReportRequest);
    }
}