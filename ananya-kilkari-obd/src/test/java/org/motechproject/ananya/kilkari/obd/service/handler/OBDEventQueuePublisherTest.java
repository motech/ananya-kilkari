package org.motechproject.ananya.kilkari.obd.service.handler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.ananya.kilkari.obd.domain.ValidFailedCallReport;
import org.motechproject.ananya.kilkari.obd.service.request.FailedCallReports;
import org.motechproject.ananya.kilkari.obd.service.request.InvalidFailedCallReports;
import org.motechproject.ananya.kilkari.obd.service.CallDeliveryFailureEventKeys;
import org.motechproject.scheduler.context.EventContext;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class OBDEventQueuePublisherTest {

    private OBDEventQueuePublisher obdEventQueuePublisher;

    @Mock
    private EventContext eventContext;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        obdEventQueuePublisher = new OBDEventQueuePublisher(eventContext);
    }

    @Test
    public void shouldPublishCallDeliveryFailureRecord() {
        FailedCallReports failedCallReports = mock(FailedCallReports.class);
        obdEventQueuePublisher.publishCallDeliveryFailureRecord(failedCallReports);

        verify(eventContext).send(CallDeliveryFailureEventKeys.PROCESS_CALL_DELIVERY_FAILURE_REQUEST, failedCallReports);
    }

    @Test
    public void shouldPublishInvalidCallDeliveryFailureRecord() {
        InvalidFailedCallReports invalidFailedCallReports = mock(InvalidFailedCallReports.class);
        obdEventQueuePublisher.publishInvalidCallDeliveryFailureRecord(invalidFailedCallReports);

        verify(eventContext).send(CallDeliveryFailureEventKeys.PROCESS_INVALID_CALL_DELIVERY_FAILURE_RECORD, invalidFailedCallReports);
    }

    @Test
    public void shouldPublishValidCallDeliveryFailureRecord() {
        ValidFailedCallReport validFailedCallReport = mock(ValidFailedCallReport.class);
        obdEventQueuePublisher.publishValidCallDeliveryFailureRecord(validFailedCallReport);

        verify(eventContext).send(CallDeliveryFailureEventKeys.PROCESS_VALID_CALL_DELIVERY_FAILURE_RECORD, validFailedCallReport);
    }
}
