package org.motechproject.ananya.kilkari.obd.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.motechproject.ananya.kilkari.obd.domain.InvalidCallRecord;
import org.motechproject.ananya.kilkari.obd.service.request.InvalidOBDRequestEntries;
import org.motechproject.ananya.kilkari.obd.service.request.InvalidOBDRequestEntry;
import org.motechproject.scheduler.domain.MotechEvent;

import java.util.ArrayList;
import java.util.HashMap;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class InvalidOBDEntriesHandlerTest {

    @Mock
    private InvalidOBDEntriesService invalidOBDEntriesService;

    private InvalidOBDEntriesHandler invalidOBDEntriesHandler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        invalidOBDEntriesHandler = new InvalidOBDEntriesHandler(invalidOBDEntriesService);
    }

    @Test
    public void shouldProcessInvalidCallRecords() {
        String msisdn1 = "msisdn1";
        String campaign1 = "campaign1";
        String desc1 = "desc1";
        String operator1 = "operator1";
        String sub1 = "sub1";

        InvalidOBDRequestEntries invalidOBDRequestEntries = Mockito.mock(InvalidOBDRequestEntries.class);
        ArrayList<InvalidOBDRequestEntry> invalidCallRecordRequestObjects = new ArrayList<InvalidOBDRequestEntry>();
        InvalidOBDRequestEntry invalidOBDRequestEntry1 = Mockito.mock(InvalidOBDRequestEntry.class);
        when(invalidOBDRequestEntry1.getMsisdn()).thenReturn(msisdn1);
        when(invalidOBDRequestEntry1.getCampaignId()).thenReturn(campaign1);
        when(invalidOBDRequestEntry1.getDescription()).thenReturn(desc1);
        when(invalidOBDRequestEntry1.getOperator()).thenReturn(operator1);
        when(invalidOBDRequestEntry1.getSubscriptionId()).thenReturn(sub1);

        InvalidOBDRequestEntry invalidOBDRequestEntry2 = Mockito.mock(InvalidOBDRequestEntry.class);
        invalidCallRecordRequestObjects.add(invalidOBDRequestEntry1);
        invalidCallRecordRequestObjects.add(invalidOBDRequestEntry2);


        when(invalidOBDRequestEntries.getInvalidOBDRequestEntryList()).thenReturn(invalidCallRecordRequestObjects);

        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("0", invalidOBDRequestEntries);

        invalidOBDEntriesHandler.handleInvalidCallRecordsRequest(new MotechEvent(CallDeliveryFailureEventKeys.PROCESS_INVALID_CALL_RECORDS_REQUEST_SUBJECT, parameters));

        ArgumentCaptor<ArrayList> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(invalidOBDEntriesService).processInvalidCallRecords(captor.capture());

        ArrayList actualInvalidCallRecords = captor.getValue();
        assertEquals(2, actualInvalidCallRecords.size());
        InvalidCallRecord actualInvalidCallRecord1 = (InvalidCallRecord) actualInvalidCallRecords.get(0);
        assertEquals(campaign1, actualInvalidCallRecord1.getCampaignId());
        assertEquals(sub1, actualInvalidCallRecord1.getSubscriptionId());
        assertEquals(msisdn1, actualInvalidCallRecord1.getMsisdn());
        assertEquals(operator1, actualInvalidCallRecord1.getOperator());
        assertEquals(desc1, actualInvalidCallRecord1.getDescription());
    }
}
