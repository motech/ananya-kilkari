package org.motechproject.ananya.kilkari.obd.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.obd.domain.InvalidCallRecord;
import org.motechproject.ananya.kilkari.obd.repository.AllInvalidCallRecords;

import java.util.ArrayList;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class CallRecordsServiceTest {
    @Mock
    private AllInvalidCallRecords allInvalidCallRecords;

    private CallRecordsService callRecordsService;

    @Before
    public void setUp() {
        initMocks(this);
        callRecordsService = new CallRecordsService(allInvalidCallRecords);
    }

    @Test
    public void shouldSaveInvalidCallRecords() {
        ArrayList<InvalidCallRecord> invalidCallRecords = new ArrayList<>();
        InvalidCallRecord invalidCallRecord1 = new InvalidCallRecord("msisdn1", "subscription1", "campaign1", "operator1", "description1");
        InvalidCallRecord invalidCallRecord2 = new InvalidCallRecord("msisdn2", "subscription2", "campaign2", "operator2", "description2");
        invalidCallRecords.add(invalidCallRecord1);
        invalidCallRecords.add(invalidCallRecord2);

        callRecordsService.processInvalidCallRecords(invalidCallRecords);

        verify(allInvalidCallRecords).add(invalidCallRecord1);
        verify(allInvalidCallRecords).add(invalidCallRecord2);
    }

}