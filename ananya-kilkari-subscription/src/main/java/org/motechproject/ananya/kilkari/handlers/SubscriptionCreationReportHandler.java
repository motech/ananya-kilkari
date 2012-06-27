package org.motechproject.ananya.kilkari.handlers;

import org.apache.log4j.Logger;
import org.motechproject.ananya.kilkari.domain.SubscriptionEventKeys;
import org.motechproject.ananya.kilkari.domain.SubscriptionReportRequest;
import org.motechproject.ananya.kilkari.service.ReportingService;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionCreationReportHandler {

    Logger logger = Logger.getLogger(SubscriptionCreationReportHandler.class);
    private ReportingService reportingService;

    @Autowired
    public SubscriptionCreationReportHandler(ReportingService reportingService) {
        this.reportingService = reportingService;
    }

    @MotechListener(subjects = {SubscriptionEventKeys.REPORT_SUBSCRIPTION_CREATION})
    public void handleReportSubscription(MotechEvent event) {
        SubscriptionReportRequest subscriptionReportRequest = (SubscriptionReportRequest) event.getParameters().get("0");
        logger.info(String.format("Handling report subscription creation event for msisdn: %s, pack: %s, channel: %s", subscriptionReportRequest.getMsisdn(), subscriptionReportRequest.getPack(), subscriptionReportRequest.getChannel()));
        try {
            reportingService.createSubscription(subscriptionReportRequest);
        } catch (RuntimeException e) {
            logger.error("Exception Occurred while reporting subscription creation", e);
            throw e;
        }
    }

}