package org.motechproject.ananya.kilkari.obd.handlers;

import org.apache.log4j.Logger;
import org.motechproject.ananya.kilkari.obd.domain.ValidFailedCallReport;
import org.motechproject.ananya.kilkari.obd.domain.OBDEventKeys;
import org.motechproject.ananya.kilkari.obd.service.CampaignMessageService;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidCallDeliveryFailureRecordHandler {

    Logger logger = Logger.getLogger(ValidCallDeliveryFailureRecordHandler.class);
    private CampaignMessageService campaignMessageService;

    @Autowired
    public ValidCallDeliveryFailureRecordHandler(CampaignMessageService campaignMessageService) {
        this.campaignMessageService = campaignMessageService;
    }

    @MotechListener(subjects = {OBDEventKeys.PROCESS_VALID_CALL_DELIVERY_FAILURE_RECORD})
    public void handleValidCallDeliveryFailureRecord(MotechEvent motechEvent) {
        ValidFailedCallReport validFailedCallReport = (ValidFailedCallReport) motechEvent.getParameters().get("0");
        logger.info("Handling OBD invalid call delivery failure records");
        campaignMessageService.processValidCallDeliveryFailureRecords(validFailedCallReport);

    }
}