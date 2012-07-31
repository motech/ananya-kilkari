package org.motechproject.ananya.kilkari.reporting.handlers;

import org.motechproject.ananya.kilkari.reporting.domain.ReportingEventKeys;
import org.motechproject.ananya.kilkari.reporting.domain.SubscriberReportRequest;
import org.motechproject.ananya.kilkari.reporting.domain.SubscriptionCreationReportRequest;
import org.motechproject.ananya.kilkari.reporting.domain.SubscriptionStateChangeReportRequest;
import org.motechproject.ananya.kilkari.reporting.repository.ReportingGateway;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionReportHandler {

    private final static Logger logger = LoggerFactory.getLogger(SubscriptionReportHandler.class);
    private ReportingGateway reportingGateway;

    @Autowired
    public SubscriptionReportHandler(ReportingGateway reportingGateway) {
        this.reportingGateway = reportingGateway;
    }

    @MotechListener(subjects = {ReportingEventKeys.REPORT_SUBSCRIPTION_CREATION})
    public void handleSubscriptionCreation(MotechEvent event) {
        SubscriptionCreationReportRequest subscriptionCreationReportRequest = (SubscriptionCreationReportRequest) event.getParameters().get("0");
        logger.info(String.format("Handling report subscription creation event for msisdn: %s, pack: %s, channel: %s", subscriptionCreationReportRequest.getMsisdn(), subscriptionCreationReportRequest.getPack(), subscriptionCreationReportRequest.getChannel()));
        reportingGateway.createSubscription(subscriptionCreationReportRequest);
    }

    @MotechListener(subjects = {ReportingEventKeys.REPORT_SUBSCRIPTION_STATE_CHANGE})
    public void handleSubscriptionStateChange(MotechEvent event) {
        SubscriptionStateChangeReportRequest subscriptionStateChangeReportRequest = (SubscriptionStateChangeReportRequest) event.getParameters().get("0");
        logger.info(String.format("Handling report subscription state change event for subscripitonId: %s, subscriptionStatus: %s", subscriptionStateChangeReportRequest.getSubscriptionId(), subscriptionStateChangeReportRequest.getSubscriptionStatus()));
        reportingGateway.updateSubscriptionStateChange(subscriptionStateChangeReportRequest);
    }

    @MotechListener(subjects = {ReportingEventKeys.REPORT_SUBSCRIBER_DETAILS_UPDATE})
    public void updateSubscriberDetails(MotechEvent event) {
        SubscriberReportRequest subscriberReportRequest = (SubscriberReportRequest) event.getParameters().get("0");
        logger.info(String.format("Handling report subscriber details update event for subscripitonId: %s", subscriberReportRequest.getSubscriptionId()));
        reportingGateway.updateSubscriberDetails(subscriberReportRequest);
    }
}