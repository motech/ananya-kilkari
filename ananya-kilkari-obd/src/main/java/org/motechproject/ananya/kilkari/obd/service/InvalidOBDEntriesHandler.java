package org.motechproject.ananya.kilkari.obd.service;

import org.apache.log4j.Logger;
import org.motechproject.ananya.kilkari.obd.domain.InvalidCallRecord;
import org.motechproject.ananya.kilkari.obd.service.request.InvalidOBDRequestEntries;
import org.motechproject.ananya.kilkari.obd.service.request.InvalidOBDRequestEntry;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class InvalidOBDEntriesHandler {

    Logger logger = Logger.getLogger(InvalidOBDEntriesHandler.class);
    private InvalidOBDEntriesService invalidOBDEntriesService;

    @Autowired
    public InvalidOBDEntriesHandler(InvalidOBDEntriesService invalidOBDEntriesService) {
        this.invalidOBDEntriesService = invalidOBDEntriesService;
    }

    @MotechListener(subjects = {CallDeliveryFailureEventKeys.PROCESS_INVALID_CALL_RECORDS_REQUEST_SUBJECT})
    public void handleInvalidCallRecordsRequest(MotechEvent event) {
        InvalidOBDRequestEntries invalidOBDRequestEntries = (InvalidOBDRequestEntries) event.getParameters().get("0");
        List<InvalidCallRecord> invalidCallRecords = mapToInvalidCallRecord(invalidOBDRequestEntries);

        logger.info("Handling invalid obd request entries");
        invalidOBDEntriesService.processInvalidCallRecords(invalidCallRecords);
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
