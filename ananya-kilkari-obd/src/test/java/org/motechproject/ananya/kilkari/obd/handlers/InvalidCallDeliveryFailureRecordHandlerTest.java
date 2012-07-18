package org.motechproject.ananya.kilkari.obd.handlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.obd.contract.InvalidCallDeliveryFailureRecord;
import org.motechproject.ananya.kilkari.obd.contract.InvalidCallDeliveryFailureRecordObject;
import org.motechproject.ananya.kilkari.obd.domain.OBDEventKeys;
import org.motechproject.ananya.kilkari.obd.gateway.OnMobileOBDGateway;
import org.motechproject.scheduler.domain.MotechEvent;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class InvalidCallDeliveryFailureRecordHandlerTest {
    @Mock
    private OnMobileOBDGateway onMobileOBDGateway;

    private InvalidCallDeliveryFailureRecordHandler invalidCallDeliveryFailureRecordHandler;

    @Before
    public void setUp() {
        initMocks(this);
        invalidCallDeliveryFailureRecordHandler = new InvalidCallDeliveryFailureRecordHandler(onMobileOBDGateway);
    }

    @Test
    public void shouldSendInvalidCallDeliveryFailureRecordsToObd() {
        HashMap<String, Object> parameters = new HashMap<>();
        InvalidCallDeliveryFailureRecord failureRecord = new InvalidCallDeliveryFailureRecord();
        ArrayList<InvalidCallDeliveryFailureRecordObject> recordObjects = new ArrayList<>();
        recordObjects.add(new InvalidCallDeliveryFailureRecordObject("msisdn1", "subscriptionId1", "description1"));
        recordObjects.add(new InvalidCallDeliveryFailureRecordObject("msisdn2", "subscriptionId2", "description2"));
        failureRecord.setRecordObjects(recordObjects);
        parameters.put("0", failureRecord);

        invalidCallDeliveryFailureRecordHandler.handleInvalidCallDeliveryFailureRecord(new MotechEvent(OBDEventKeys.PROCESS_INVALID_CALL_DELIVERY_FAILURE_RECORD, parameters));

        ArgumentCaptor<InvalidCallDeliveryFailureRecord> invalidCallDeliveryFailureRecordArgumentCaptor = ArgumentCaptor.forClass(InvalidCallDeliveryFailureRecord.class);
        verify(onMobileOBDGateway).sendInvalidFailureRecord(invalidCallDeliveryFailureRecordArgumentCaptor.capture());
        InvalidCallDeliveryFailureRecord invalidCallDeliveryFailureRecord = invalidCallDeliveryFailureRecordArgumentCaptor.getValue();

        assertEquals(2, invalidCallDeliveryFailureRecord.getRecordObjects().size());
        assertEquals("msisdn1", invalidCallDeliveryFailureRecord.getRecordObjects().get(0).getMsisdn());
        assertEquals("subscriptionId1", invalidCallDeliveryFailureRecord.getRecordObjects().get(0).getSubscriptionId());
        assertEquals("description1", invalidCallDeliveryFailureRecord.getRecordObjects().get(0).getDescription());
        assertEquals("msisdn2", invalidCallDeliveryFailureRecord.getRecordObjects().get(1).getMsisdn());
        assertEquals("subscriptionId2", invalidCallDeliveryFailureRecord.getRecordObjects().get(1).getSubscriptionId());
        assertEquals("description2", invalidCallDeliveryFailureRecord.getRecordObjects().get(1).getDescription());
    }
}
