package org.motechproject.ananya.kilkari.service;

import org.motechproject.ananya.kilkari.obd.service.request.InvalidOBDRequestEntries;
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

    public void publishInboxCallDetailsRequest(InboxCallDetailsWebRequest inboxCallDetailsWebRequest) {
        eventContext.send(CallDetailsEventKeys.PROCESS_INBOX_CALL_REQUEST_SUBJECT, inboxCallDetailsWebRequest);
    }
}
