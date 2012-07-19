package org.motechproject.ananya.kilkari.obd.handlers;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.motechproject.ananya.kilkari.obd.contract.ValidCallDeliveryFailureRecordObject;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessage;
import org.motechproject.ananya.kilkari.obd.domain.OBDEventKeys;
import org.motechproject.ananya.kilkari.obd.gateway.OBDEndPoints;
import org.motechproject.ananya.kilkari.obd.service.CampaignMessageService;
import org.motechproject.ananya.kilkari.reporting.domain.CallDetailsReportRequest;
import org.motechproject.ananya.kilkari.reporting.domain.CampaignMessageDeliveryReportRequest;
import org.motechproject.ananya.kilkari.reporting.service.ReportingService;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidCallDeliveryFailureRecordHandler {

    Logger logger = Logger.getLogger(ValidCallDeliveryFailureRecordHandler.class);
    private CampaignMessageService campaignMessageService;
    private ReportingService reportingService;
    private OBDEndPoints obdEndPoints;

    @Autowired
    public ValidCallDeliveryFailureRecordHandler(CampaignMessageService campaignMessageService, ReportingService reportingService, OBDEndPoints obdEndPoints) {
        this.campaignMessageService = campaignMessageService;
        this.reportingService = reportingService;
        this.obdEndPoints = obdEndPoints;
    }

    @MotechListener(subjects = {OBDEventKeys.PROCESS_VALID_CALL_DELIVERY_FAILURE_RECORD})
    public void handleValidCallDeliveryFailureRecord(MotechEvent motechEvent) {
        ValidCallDeliveryFailureRecordObject validCallDeliveryFailureRecordObject = (ValidCallDeliveryFailureRecordObject) motechEvent.getParameters().get("0");
        logger.info("Handling OBD invalid call delivery failure records");
        CampaignMessage campaignMessage = campaignMessageService.find(validCallDeliveryFailureRecordObject.getSubscriptionId(), validCallDeliveryFailureRecordObject.getCampaignId());
        if(campaignMessage == null) {
            logger.error(String.format("Campaign Message not present for subscriptionId[%s] and campaignId[%s].",
                    validCallDeliveryFailureRecordObject.getSubscriptionId(), validCallDeliveryFailureRecordObject.getCampaignId()));
            return;
        }
        updateCampaignMessageStatus(campaignMessage);
        reportCampaignMessageStatus(validCallDeliveryFailureRecordObject, campaignMessage);
    }

    private void reportCampaignMessageStatus(ValidCallDeliveryFailureRecordObject recordObject, CampaignMessage campaignMessage) {
        String retryCount = ((Integer) campaignMessage.getRetryCount()).toString();
        CallDetailsReportRequest callDetailRecord = new CallDetailsReportRequest(format(recordObject.getCreatedAt()), format(recordObject.getCreatedAt()));
        CampaignMessageDeliveryReportRequest campaignMessageDeliveryReportRequest = new CampaignMessageDeliveryReportRequest(recordObject.getSubscriptionId(), recordObject.getMsisdn(), recordObject.getCampaignId(), null, retryCount, callDetailRecord);

        reportingService.reportCampaignMessageDeliveryStatus(campaignMessageDeliveryReportRequest);
    }

    private void updateCampaignMessageStatus(CampaignMessage campaignMessage) {
        if(campaignMessage.getRetryCount() == obdEndPoints.getMaximumRetryCount())
            campaignMessageService.deleteCampaignMessage(campaignMessage);
        else {
            campaignMessage.markDidNotPickup();
            campaignMessageService.update(campaignMessage);
        }
    }

    private String format(DateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MM-yyyy HH:mm:ss");
        return formatter.print(dateTime);
    }
}