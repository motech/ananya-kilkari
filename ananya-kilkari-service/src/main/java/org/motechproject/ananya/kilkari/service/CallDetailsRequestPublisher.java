package org.motechproject.ananya.kilkari.service;

import org.motechproject.ananya.kilkari.obd.domain.ValidFailedCallReport;
import org.motechproject.ananya.kilkari.obd.request.FailedCallReports;
import org.motechproject.ananya.kilkari.obd.request.InvalidFailedCallReports;
import org.motechproject.ananya.kilkari.obd.request.InvalidOBDRequestEntries;
import org.motechproject.ananya.kilkari.obd.service.CallDeliveryFailureEventKeys;
import org.motechproject.ananya.kilkari.request.InboxCallDetailsWebRequest;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallDetailsWebRequest;
import org.motechproject.scheduler.context.EventContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class CallDetailsRequestPublisher {
    private EventContext eventContext;

    @Autowired
    public CallDetailsRequestPublisher(@Qualifier("eventContext") EventContext eventContext) {
        this.eventContext = eventContext;
    }

    public void publishSuccessfulCallRequest(OBDSuccessfulCallDetailsWebRequest obdSuccessfulCallDetailsRequest) {
        eventContext.send(CallDetailsEventKeys.PROCESS_OBD_SUCCESSFUL_CALL_REQUEST_SUBJECT, obdSuccessfulCallDetailsRequest);
    }

    public void publishInvalidCallRecordsRequest(InvalidOBDRequestEntries invalidOBDRequestEntries) {
        eventContext.send(CallDetailsEventKeys.PROCESS_INVALID_CALL_RECORDS_REQUEST_SUBJECT, invalidOBDRequestEntries);
    }

    public void publishCallDeliveryFailureRecord(FailedCallReports failedCallReports) {
        eventContext.send(CallDetailsEventKeys.PROCESS_CALL_DELIVERY_FAILURE_REQUEST, failedCallReports);
    }

    public void publishInvalidCallDeliveryFailureRecord(InvalidFailedCallReports invalidFailedCallReports) {
        eventContext.send(CallDeliveryFailureEventKeys.PROCESS_INVALID_CALL_DELIVERY_FAILURE_RECORD, invalidFailedCallReports);
    }

    public void publishValidCallDeliveryFailureRecord(ValidFailedCallReport validFailedCallReport) {
        eventContext.send(CallDeliveryFailureEventKeys.PROCESS_VALID_CALL_DELIVERY_FAILURE_RECORD, validFailedCallReport);
    }

    public void publishInboxCallDetailsRequest(InboxCallDetailsWebRequest inboxCallDetailsWebRequest) {
        eventContext.send(CallDetailsEventKeys.PROCESS_INBOX_CALL_REQUEST_SUBJECT, inboxCallDetailsWebRequest);
    }

}
