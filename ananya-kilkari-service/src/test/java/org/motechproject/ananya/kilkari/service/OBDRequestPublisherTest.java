package org.motechproject.ananya.kilkari.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.kilkari.obd.domain.OBDEventKeys;
import org.motechproject.ananya.kilkari.obd.domain.ValidFailedCallReport;
import org.motechproject.ananya.kilkari.obd.request.FailedCallReports;
import org.motechproject.ananya.kilkari.obd.request.InvalidFailedCallReports;
import org.motechproject.ananya.kilkari.obd.request.InvalidOBDRequestEntries;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallDetailsWebRequest;
import org.motechproject.scheduler.context.EventContext;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class OBDRequestPublisherTest {

    @Mock
    private EventContext eventContext;

    private OBDRequestPublisher obdRequestPublisher;

    @Before
    public void setUp() {
        obdRequestPublisher = new OBDRequestPublisher(eventContext);
    }

    @Test
    public void shouldPublishCallBackRequests() {
        OBDSuccessfulCallDetailsWebRequest obdSuccessfulCallDetailsRequest = new OBDSuccessfulCallDetailsWebRequest();

        obdRequestPublisher.publishSuccessfulCallRequest(obdSuccessfulCallDetailsRequest);

        verify(eventContext).send(OBDEventKeys.PROCESS_SUCCESSFUL_CALL_REQUEST_SUBJECT, obdSuccessfulCallDetailsRequest);
    }

    @Test
    public void shouldPublishInvalidCallRecordsRequest() {
        InvalidOBDRequestEntries invalidOBDRequestEntries = new InvalidOBDRequestEntries();
        obdRequestPublisher.publishInvalidCallRecordsRequest(invalidOBDRequestEntries);

        verify(eventContext).send(OBDEventKeys.PROCESS_INVALID_CALL_RECORDS_REQUEST_SUBJECT, invalidOBDRequestEntries);
    }

    @Test
    public void shouldPublishCallDeliveryFailureRecord() {
        FailedCallReports failedCallReports = mock(FailedCallReports.class);
        obdRequestPublisher.publishCallDeliveryFailureRecord(failedCallReports);

        verify(eventContext).send(OBDEventKeys.PROCESS_CALL_DELIVERY_FAILURE_REQUEST, failedCallReports);
    }

    @Test
    public void shouldPublishInvalidCallDeliveryFailureRecord() {
        InvalidFailedCallReports invalidFailedCallReports = mock(InvalidFailedCallReports.class);
        obdRequestPublisher.publishInvalidCallDeliveryFailureRecord(invalidFailedCallReports);

        verify(eventContext).send(OBDEventKeys.PROCESS_INVALID_CALL_DELIVERY_FAILURE_RECORD, invalidFailedCallReports);
    }

    @Test
    public void shouldPublishValidCallDeliveryFailureRecord() {
        ValidFailedCallReport validFailedCallReport = mock(ValidFailedCallReport.class);
        obdRequestPublisher.publishValidCallDeliveryFailureRecord(validFailedCallReport);

        verify(eventContext).send(OBDEventKeys.PROCESS_VALID_CALL_DELIVERY_FAILURE_RECORD, validFailedCallReport);
    }
}
