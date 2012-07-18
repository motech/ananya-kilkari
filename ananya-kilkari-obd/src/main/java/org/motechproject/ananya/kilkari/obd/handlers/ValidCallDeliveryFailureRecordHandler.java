package org.motechproject.ananya.kilkari.obd.handlers;

import org.apache.log4j.Logger;
import org.motechproject.ananya.kilkari.obd.contract.ValidCallDeliveryFailureRecordObject;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessage;
import org.motechproject.ananya.kilkari.obd.domain.OBDEventKeys;
import org.motechproject.ananya.kilkari.obd.gateway.OBDEndPoints;
import org.motechproject.ananya.kilkari.obd.service.CampaignMessageService;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidCallDeliveryFailureRecordHandler {

    Logger logger = Logger.getLogger(ValidCallDeliveryFailureRecordHandler.class);
    private CampaignMessageService campaignMessageService;
    private OBDEndPoints obdEndPoints;

    @Autowired
    public ValidCallDeliveryFailureRecordHandler(CampaignMessageService campaignMessageService, OBDEndPoints obdEndPoints) {
        this.campaignMessageService = campaignMessageService;
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
    }

    private void updateCampaignMessageStatus(CampaignMessage campaignMessage) {
        if(campaignMessage.getRetryCount() == obdEndPoints.getMaximumRetryCount())
            campaignMessageService.deleteCampaignMessage(campaignMessage);
        else {
            campaignMessage.markDidNotPickup();
            campaignMessageService.update(campaignMessage);
        }
    }
}