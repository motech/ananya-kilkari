package org.motechproject.ananya.kilkari.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.kilkari.obd.contract.CallDeliveryFailureRecord;
import org.motechproject.ananya.kilkari.obd.contract.InvalidCallDeliveryFailureRecord;
import org.motechproject.ananya.kilkari.obd.contract.InvalidCallRecordsRequest;
import org.motechproject.ananya.kilkari.obd.domain.OBDEventKeys;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallRequest;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallRequestWrapper;
import org.motechproject.ananya.kilkari.subscription.domain.Channel;
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
        OBDSuccessfulCallRequestWrapper successfulCallRequestWrapper = new OBDSuccessfulCallRequestWrapper(new OBDSuccessfulCallRequest(), "subscriptionID", DateTime.now(), Channel.IVR);
        obdRequestPublisher.publishSuccessfulCallRequest(successfulCallRequestWrapper);

        verify(eventContext).send(OBDEventKeys.PROCESS_SUCCESSFUL_CALL_REQUEST_SUBJECT, successfulCallRequestWrapper);
    }

    @Test
    public void shouldPublishInvalidCallRecordsRequest() {
        InvalidCallRecordsRequest invalidCallRecordsRequest = new InvalidCallRecordsRequest();
        obdRequestPublisher.publishInvalidCallRecordsRequest(invalidCallRecordsRequest);

        verify(eventContext).send(OBDEventKeys.PROCESS_INVALID_CALL_RECORDS_REQUEST_SUBJECT, invalidCallRecordsRequest);
    }

    @Test
    public void shouldPublishCallDeliveryFailureRecord() {
        CallDeliveryFailureRecord callDeliveryFailureRecord = mock(CallDeliveryFailureRecord.class);
        obdRequestPublisher.publishCallDeliveryFailureRecord(callDeliveryFailureRecord);

        verify(eventContext).send(OBDEventKeys.PROCESS_CALL_DELIVERY_FAILURE_REQUEST, callDeliveryFailureRecord);
    }

    @Test
    public void shouldPublishInvalidCallDeliveryFailureRecord() {
        InvalidCallDeliveryFailureRecord invalidCallDeliveryFailureRecord = mock(InvalidCallDeliveryFailureRecord.class);
        obdRequestPublisher.publishInvalidCallDeliveryFailureRecord(invalidCallDeliveryFailureRecord);

        verify(eventContext).send(OBDEventKeys.PROCESS_INVALID_CALL_DELIVERY_FAILURE_RECORD, invalidCallDeliveryFailureRecord);
    }
}
