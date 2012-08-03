package org.motechproject.ananya.kilkari.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.kilkari.obd.domain.ValidFailedCallReport;
import org.motechproject.ananya.kilkari.obd.request.FailedCallReports;
import org.motechproject.ananya.kilkari.obd.request.InvalidFailedCallReports;
import org.motechproject.ananya.kilkari.obd.request.InvalidOBDRequestEntries;
import org.motechproject.ananya.kilkari.obd.service.CallDeliveryFailureEventKeys;
import org.motechproject.ananya.kilkari.request.InboxCallDetailsWebRequest;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallDetailsWebRequest;
import org.motechproject.scheduler.context.EventContext;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CallDetailsRequestPublisherTest {

    @Mock
    private EventContext eventContext;

    private CallDetailsRequestPublisher callDetailsRequestPublisher;

    @Before
    public void setUp() {
        callDetailsRequestPublisher = new CallDetailsRequestPublisher(eventContext);
    }

    @Test
    public void shouldPublishCallBackRequests() {
        OBDSuccessfulCallDetailsWebRequest obdSuccessfulCallDetailsRequest = new OBDSuccessfulCallDetailsWebRequest();

        callDetailsRequestPublisher.publishSuccessfulCallRequest(obdSuccessfulCallDetailsRequest);

        verify(eventContext).send(CallDetailsEventKeys.PROCESS_OBD_SUCCESSFUL_CALL_REQUEST_SUBJECT, obdSuccessfulCallDetailsRequest);
    }

    @Test
    public void shouldPublishInvalidCallRecordsRequest() {
        InvalidOBDRequestEntries invalidOBDRequestEntries = new InvalidOBDRequestEntries();
        callDetailsRequestPublisher.publishInvalidCallRecordsRequest(invalidOBDRequestEntries);

        verify(eventContext).send(CallDetailsEventKeys.PROCESS_INVALID_CALL_RECORDS_REQUEST_SUBJECT, invalidOBDRequestEntries);
    }

    @Test
    public void shouldPublishCallDeliveryFailureRecord() {
        FailedCallReports failedCallReports = mock(FailedCallReports.class);
        callDetailsRequestPublisher.publishCallDeliveryFailureRecord(failedCallReports);

        verify(eventContext).send(CallDetailsEventKeys.PROCESS_CALL_DELIVERY_FAILURE_REQUEST, failedCallReports);
    }

    @Test
    public void shouldPublishInvalidCallDeliveryFailureRecord() {
        InvalidFailedCallReports invalidFailedCallReports = mock(InvalidFailedCallReports.class);
        callDetailsRequestPublisher.publishInvalidCallDeliveryFailureRecord(invalidFailedCallReports);

        verify(eventContext).send(CallDeliveryFailureEventKeys.PROCESS_INVALID_CALL_DELIVERY_FAILURE_RECORD, invalidFailedCallReports);
    }

    @Test
    public void shouldPublishValidCallDeliveryFailureRecord() {
        ValidFailedCallReport validFailedCallReport = mock(ValidFailedCallReport.class);
        callDetailsRequestPublisher.publishValidCallDeliveryFailureRecord(validFailedCallReport);

        verify(eventContext).send(CallDeliveryFailureEventKeys.PROCESS_VALID_CALL_DELIVERY_FAILURE_RECORD, validFailedCallReport);
    }

    @Test
    public void shouldPublishInboxCallDetailsRequest() {
        InboxCallDetailsWebRequest inboxCallDetailsWebRequest = new InboxCallDetailsWebRequest();

        callDetailsRequestPublisher.publishInboxCallDetailsRequest(inboxCallDetailsWebRequest);

        verify(eventContext).send(CallDetailsEventKeys.PROCESS_INBOX_CALL_REQUEST_SUBJECT, inboxCallDetailsWebRequest);
    }
}
