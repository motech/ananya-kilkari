package org.motechproject.ananya.kilkari.handlers;

import org.apache.log4j.Logger;
import org.motechproject.ananya.kilkari.obd.domain.InvalidCallRecord;
import org.motechproject.ananya.kilkari.obd.request.FailedCallReports;
import org.motechproject.ananya.kilkari.obd.request.InvalidOBDRequestEntries;
import org.motechproject.ananya.kilkari.obd.request.InvalidOBDRequestEntry;
import org.motechproject.ananya.kilkari.obd.service.CallRecordsService;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallDetailsWebRequest;
import org.motechproject.ananya.kilkari.service.CallDetailsEventKeys;
import org.motechproject.ananya.kilkari.service.KilkariCampaignService;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CallDetailsRequestHandler {

    Logger logger = Logger.getLogger(CallDetailsRequestHandler.class);
    private KilkariCampaignService kilkariCampaignService;
    private CallRecordsService callRecordsService;

    @Autowired
    public CallDetailsRequestHandler(KilkariCampaignService kilkariCampaignService, CallRecordsService callRecordsService) {
        this.kilkariCampaignService = kilkariCampaignService;
        this.callRecordsService = callRecordsService;
    }

    @MotechListener(subjects = {CallDetailsEventKeys.PROCESS_OBD_SUCCESSFUL_CALL_REQUEST_SUBJECT})
    public void handleOBDCallbackRequest(MotechEvent motechEvent) {
        OBDSuccessfulCallDetailsWebRequest obdSuccessfulCallDetailsRequest = (OBDSuccessfulCallDetailsWebRequest) motechEvent.getParameters().get("0");
        logger.info("Handling OBD callback for : " + obdSuccessfulCallDetailsRequest.getSubscriptionId());
        kilkariCampaignService.processSuccessfulMessageDelivery(obdSuccessfulCallDetailsRequest);
        logger.info("Completed handling OBD callback for : " + obdSuccessfulCallDetailsRequest.getSubscriptionId());
    }

    @MotechListener(subjects = {CallDetailsEventKeys.PROCESS_INVALID_CALL_RECORDS_REQUEST_SUBJECT})
    public void handleInvalidCallRecordsRequest(MotechEvent event) {
        InvalidOBDRequestEntries invalidOBDRequestEntries = (InvalidOBDRequestEntries) event.getParameters().get("0");
        List<InvalidCallRecord> invalidCallRecords = mapToInvalidCallRecord(invalidOBDRequestEntries);
        callRecordsService.processInvalidCallRecords(invalidCallRecords);
    }

    @MotechListener(subjects = {CallDetailsEventKeys.PROCESS_CALL_DELIVERY_FAILURE_REQUEST})
    public void handleCallDeliveryFailureRecord(MotechEvent motechEvent) {
        FailedCallReports failedCallReports = (FailedCallReports) motechEvent.getParameters().get("0");
        logger.info("Handling OBD call delivery failure record");
        kilkariCampaignService.processCallDeliveryFailureRecord(failedCallReports);
    }

    private List<InvalidCallRecord> mapToInvalidCallRecord(InvalidOBDRequestEntries invalidOBDRequestEntries) {
        List<InvalidOBDRequestEntry> requestOBDs = invalidOBDRequestEntries.getInvalidOBDRequestEntryList();
        List<InvalidCallRecord> invalidCallRecords = new ArrayList<>();
        for (InvalidOBDRequestEntry requestEntry : requestOBDs) {
            invalidCallRecords.add(new InvalidCallRecord(requestEntry.getMsisdn(), requestEntry.getSubscriptionId(),
                    requestEntry.getCampaignId(), requestEntry.getOperator(), requestEntry.getDescription()));
        }
        return invalidCallRecords;
    }
}
