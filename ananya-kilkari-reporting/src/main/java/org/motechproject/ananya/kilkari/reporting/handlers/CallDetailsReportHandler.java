package org.motechproject.ananya.kilkari.reporting.handlers;

import org.motechproject.ananya.kilkari.reporting.domain.CampaignMessageDeliveryReportRequest;
import org.motechproject.ananya.kilkari.reporting.domain.ReportingEventKeys;
import org.motechproject.ananya.kilkari.reporting.repository.ReportingGateway;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CallDetailsReportHandler {

    private final static Logger logger = LoggerFactory.getLogger(CallDetailsReportHandler.class);
    private ReportingGateway reportingGateway;

    @Autowired
    public CallDetailsReportHandler(ReportingGateway reportingGateway) {
        this.reportingGateway = reportingGateway;
    }

    @MotechListener(subjects = {ReportingEventKeys.REPORT_CAMPAIGN_MESSAGE_DELIVERY_STATUS})
    public void handleCampaignMessageDelivery(MotechEvent event) {
        CampaignMessageDeliveryReportRequest campaignMessageDeliveryReportRequest = (CampaignMessageDeliveryReportRequest) event.getParameters().get("0");
        logger.info(String.format("Handling report sucessful campaign message delivery event for subscriptionId: %s, msisdn: %s, campaignId: %s, retryCount: %s, status: %s",
                campaignMessageDeliveryReportRequest.getSubscriptionId(), campaignMessageDeliveryReportRequest.getMsisdn(),
                campaignMessageDeliveryReportRequest.getCampaignId(), campaignMessageDeliveryReportRequest.getRetryCount(), campaignMessageDeliveryReportRequest.getStatus()));
        reportingGateway.reportCampaignMessageDelivery(campaignMessageDeliveryReportRequest);
    }
}