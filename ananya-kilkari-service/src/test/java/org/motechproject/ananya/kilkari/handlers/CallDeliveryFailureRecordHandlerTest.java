package org.motechproject.ananya.kilkari.handlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.mapper.ValidCallDeliveryFailureRecordObjectMapper;
import org.motechproject.ananya.kilkari.obd.contract.*;
import org.motechproject.ananya.kilkari.obd.domain.OBDEventKeys;
import org.motechproject.ananya.kilkari.service.OBDRequestPublisher;
import org.motechproject.ananya.kilkari.validators.CallDeliveryFailureRecordValidator;
import org.motechproject.scheduler.domain.MotechEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class CallDeliveryFailureRecordHandlerTest {
    @Mock
    private CallDeliveryFailureRecordValidator callDeliveryFailureRecordValidator;
    @Mock
    private OBDRequestPublisher obdRequestPublisher;
    @Mock
    private ValidCallDeliveryFailureRecordObjectMapper validCallDeliveryFailureRecordObjectMapper;
    private CallDeliveryFailureRecordHandler callDeliveryFailureRecordHandler;

    @Before
    public void setUp() {
        initMocks(this);
        callDeliveryFailureRecordHandler = new CallDeliveryFailureRecordHandler(callDeliveryFailureRecordValidator, obdRequestPublisher, validCallDeliveryFailureRecordObjectMapper);
    }

    @Test
    public void shouldHandleCallDeliveryFailureRecord() {
        HashMap<String, Object> parameters = new HashMap<>();
        CallDeliveryFailureRecord callDeliveryFailureRecord = new CallDeliveryFailureRecord();

        ArrayList<CallDeliveryFailureRecordObject> callDeliveryFailureRecordObjects = new ArrayList<>();
        callDeliveryFailureRecordObjects.add(mock(CallDeliveryFailureRecordObject.class));
        callDeliveryFailureRecord.setCallDeliveryFailureRecordObjects(callDeliveryFailureRecordObjects);

        parameters.put("0", callDeliveryFailureRecord);

        callDeliveryFailureRecordHandler.handleCallDeliveryFailureRecord(new MotechEvent(OBDEventKeys.PROCESS_CALL_DELIVERY_FAILURE_REQUEST, parameters));

        verify(callDeliveryFailureRecordValidator, times(1)).validate(any(CallDeliveryFailureRecordObject.class));
    }

    @Test
    public void shouldPublishErroredOutCallDeliveryFailureRecords() {
        String msisdn = "12345";
        String subscriptionId = "abcd";
        HashMap<String, Object> parameters = new HashMap<>();
        CallDeliveryFailureRecord callDeliveryFailureRecord = new CallDeliveryFailureRecord();

        ArrayList<CallDeliveryFailureRecordObject> callDeliveryFailureRecordObjects = new ArrayList<>();
        CallDeliveryFailureRecordObject erroredOutCallDeliveryFailureRecordObject = mock(CallDeliveryFailureRecordObject.class);
        when(erroredOutCallDeliveryFailureRecordObject.getMsisdn()).thenReturn(msisdn);
        when(erroredOutCallDeliveryFailureRecordObject.getSubscriptionId()).thenReturn(subscriptionId);
        CallDeliveryFailureRecordObject successfulCallDeliveryFailureRecordObject = mock(CallDeliveryFailureRecordObject.class);
        callDeliveryFailureRecordObjects.add(erroredOutCallDeliveryFailureRecordObject);
        callDeliveryFailureRecordObjects.add(successfulCallDeliveryFailureRecordObject);
        callDeliveryFailureRecord.setCallDeliveryFailureRecordObjects(callDeliveryFailureRecordObjects);
        parameters.put("0", callDeliveryFailureRecord);

        when(callDeliveryFailureRecordValidator.validate(successfulCallDeliveryFailureRecordObject)).thenReturn(new ArrayList<String>());

        ArrayList<String> errors = new ArrayList<>();
        errors.add("Some error description");
        when(callDeliveryFailureRecordValidator.validate(erroredOutCallDeliveryFailureRecordObject)).thenReturn(errors);

        callDeliveryFailureRecordHandler.handleCallDeliveryFailureRecord(new MotechEvent(OBDEventKeys.PROCESS_CALL_DELIVERY_FAILURE_REQUEST, parameters));

        verify(callDeliveryFailureRecordValidator, times(2)).validate(any(CallDeliveryFailureRecordObject.class));

        ArgumentCaptor<InvalidCallDeliveryFailureRecord> invalidCallDeliveryFailureRecordArgumentCaptor = ArgumentCaptor.forClass(InvalidCallDeliveryFailureRecord.class);
        verify(obdRequestPublisher).publishInvalidCallDeliveryFailureRecord(invalidCallDeliveryFailureRecordArgumentCaptor.capture());
        InvalidCallDeliveryFailureRecord invalidCallDeliveryFailureRecord = invalidCallDeliveryFailureRecordArgumentCaptor.getValue();
        List<InvalidCallDeliveryFailureRecordObject> recordObjects = invalidCallDeliveryFailureRecord.getRecordObjects();

        assertEquals(1, recordObjects.size());
        assertEquals("Some error description", recordObjects.get(0).getDescription());
        assertEquals(msisdn, recordObjects.get(0).getMsisdn());
        assertEquals(subscriptionId, recordObjects.get(0).getSubscriptionId());
    }

    @Test
    public void shouldNotPublishToErrorQueueIfErroredOutCallDeliveryFailureRecordsAreEmpty() {
        HashMap<String, Object> parameters = new HashMap<>();
        CallDeliveryFailureRecord callDeliveryFailureRecord = new CallDeliveryFailureRecord();

        ArrayList<CallDeliveryFailureRecordObject> callDeliveryFailureRecordObjects = new ArrayList<>();
        CallDeliveryFailureRecordObject successfulCallDeliveryFailureRecordObject = mock(CallDeliveryFailureRecordObject.class);
        callDeliveryFailureRecordObjects.add(successfulCallDeliveryFailureRecordObject);
        callDeliveryFailureRecord.setCallDeliveryFailureRecordObjects(callDeliveryFailureRecordObjects);
        parameters.put("0", callDeliveryFailureRecord);

        when(callDeliveryFailureRecordValidator.validate(successfulCallDeliveryFailureRecordObject)).thenReturn(new ArrayList<String>());

        callDeliveryFailureRecordHandler.handleCallDeliveryFailureRecord(new MotechEvent(OBDEventKeys.PROCESS_CALL_DELIVERY_FAILURE_REQUEST, parameters));

        verify(callDeliveryFailureRecordValidator, times(1)).validate(any(CallDeliveryFailureRecordObject.class));
        verify(obdRequestPublisher, never()).publishInvalidCallDeliveryFailureRecord(any(InvalidCallDeliveryFailureRecord.class));
    }
    
    @Test
    public void shouldPublishSuccessfulCallDeliveryFailureRecords() {
        String msisdn = "12345";
        String subscriptionId = "abcd";
        HashMap<String, Object> parameters = new HashMap<>();
        CallDeliveryFailureRecord callDeliveryFailureRecord = new CallDeliveryFailureRecord();

        ArrayList<CallDeliveryFailureRecordObject> callDeliveryFailureRecordObjects = new ArrayList<>();
        CallDeliveryFailureRecordObject erroredOutCallDeliveryFailureRecordObject = mock(CallDeliveryFailureRecordObject.class);
        when(erroredOutCallDeliveryFailureRecordObject.getMsisdn()).thenReturn(msisdn);
        when(erroredOutCallDeliveryFailureRecordObject.getSubscriptionId()).thenReturn(subscriptionId);
        CallDeliveryFailureRecordObject successfulCallDeliveryFailureRecordObject1 = mock(CallDeliveryFailureRecordObject.class);
        CallDeliveryFailureRecordObject successfulCallDeliveryFailureRecordObject2 = mock(CallDeliveryFailureRecordObject.class);
        callDeliveryFailureRecordObjects.add(erroredOutCallDeliveryFailureRecordObject);
        callDeliveryFailureRecordObjects.add(successfulCallDeliveryFailureRecordObject1);
        callDeliveryFailureRecordObjects.add(successfulCallDeliveryFailureRecordObject2);
        callDeliveryFailureRecord.setCallDeliveryFailureRecordObjects(callDeliveryFailureRecordObjects);
        parameters.put("0", callDeliveryFailureRecord);

        when(callDeliveryFailureRecordValidator.validate(successfulCallDeliveryFailureRecordObject1)).thenReturn(new ArrayList<String>());
        when(callDeliveryFailureRecordValidator.validate(successfulCallDeliveryFailureRecordObject2)).thenReturn(new ArrayList<String>());

        ValidCallDeliveryFailureRecordObject validCallDeliveryFailureRecordObject1 = mock(ValidCallDeliveryFailureRecordObject.class);
        ValidCallDeliveryFailureRecordObject validCallDeliveryFailureRecordObject2 = mock(ValidCallDeliveryFailureRecordObject.class);
        when(validCallDeliveryFailureRecordObjectMapper.mapFrom(successfulCallDeliveryFailureRecordObject1, callDeliveryFailureRecord)).thenReturn(validCallDeliveryFailureRecordObject1);
        when(validCallDeliveryFailureRecordObjectMapper.mapFrom(successfulCallDeliveryFailureRecordObject2, callDeliveryFailureRecord)).thenReturn(validCallDeliveryFailureRecordObject2);

        ArrayList<String> errors = new ArrayList<>();
        errors.add("Some error description");
        when(callDeliveryFailureRecordValidator.validate(erroredOutCallDeliveryFailureRecordObject)).thenReturn(errors);

        callDeliveryFailureRecordHandler.handleCallDeliveryFailureRecord(new MotechEvent(OBDEventKeys.PROCESS_CALL_DELIVERY_FAILURE_REQUEST, parameters));

        verify(callDeliveryFailureRecordValidator, times(3)).validate(any(CallDeliveryFailureRecordObject.class));

        verify(obdRequestPublisher).publishValidCallDeliveryFailureRecord(validCallDeliveryFailureRecordObject1);
        verify(obdRequestPublisher).publishValidCallDeliveryFailureRecord(validCallDeliveryFailureRecordObject2);
    }
}
