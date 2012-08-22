package org.motechproject.ananya.kilkari.obd.service;

import org.motechproject.ananya.kilkari.obd.domain.InvalidCallRecord;
import org.motechproject.ananya.kilkari.obd.service.request.InvalidOBDRequestEntries;
import org.motechproject.ananya.kilkari.obd.service.request.InvalidOBDRequestEntry;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.annotations.MotechListener;

import java.util.ArrayList;
import java.util.List;

public class InvalidOBDEntriesHandler {

    private InvalidOBDEntriesService invalidOBDEntriesService;

    public InvalidOBDEntriesHandler(InvalidOBDEntriesService invalidOBDEntriesService) {
        this.invalidOBDEntriesService = invalidOBDEntriesService;
    }

    @MotechListener(subjects = {CallDeliveryFailureEventKeys.PROCESS_INVALID_CALL_RECORDS_REQUEST_SUBJECT})
    public void handleInvalidCallRecordsRequest(MotechEvent event) {
        InvalidOBDRequestEntries invalidOBDRequestEntries = (InvalidOBDRequestEntries) event.getParameters().get("0");
        List<InvalidCallRecord> invalidCallRecords = mapToInvalidCallRecord(invalidOBDRequestEntries);
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
