package org.motechproject.ananya.kilkari.handlers;

import org.apache.log4j.Logger;
import org.motechproject.ananya.kilkari.obd.request.FailedCallReports;
import org.motechproject.ananya.kilkari.obd.domain.OBDEventKeys;
import org.motechproject.ananya.kilkari.service.KilkariCampaignService;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CallDeliveryFailureRecordHandler {

    Logger logger = Logger.getLogger(CallDeliveryFailureRecordHandler.class);
    private KilkariCampaignService kilkariCampaignService;

    @Autowired
    public CallDeliveryFailureRecordHandler(KilkariCampaignService kilkariCampaignService) {
        this.kilkariCampaignService = kilkariCampaignService;
    }

    @MotechListener(subjects = {OBDEventKeys.PROCESS_CALL_DELIVERY_FAILURE_REQUEST})
    public void handleCallDeliveryFailureRecord(MotechEvent motechEvent) {
        FailedCallReports failedCallReports = (FailedCallReports) motechEvent.getParameters().get("0");
        logger.info("Handling OBD call delivery failure record");
        kilkariCampaignService.processCallDeliveryFailureRecord(failedCallReports);
    }
}