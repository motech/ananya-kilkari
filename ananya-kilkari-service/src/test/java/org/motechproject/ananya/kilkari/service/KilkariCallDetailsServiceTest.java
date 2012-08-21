package org.motechproject.ananya.kilkari.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.motechproject.ananya.kilkari.obd.service.OBDService;
import org.motechproject.ananya.kilkari.obd.service.request.FailedCallReports;
import org.motechproject.ananya.kilkari.obd.service.request.InvalidOBDRequestEntries;

import static org.mockito.Mockito.verify;

public class KilkariCallDetailsServiceTest {

    @Mock
    private OBDService obdService;

    private KilkariCallDetailsService kilkariCallDetailsService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        kilkariCallDetailsService = new KilkariCallDetailsService(obdService);
    }

    @Test
    public void shouldProcessInvalidCallRecords() {
        InvalidOBDRequestEntries invalidOBDRequestEntries = new InvalidOBDRequestEntries();

        kilkariCallDetailsService.processInvalidOBDRequestEntries(invalidOBDRequestEntries);

        verify(obdService).processInvalidOBDRequestEntries(invalidOBDRequestEntries);
    }

    @Test
    public void shouldPublishCallDeliveryFailureRecords() {
        FailedCallReports failedCallReports = Mockito.mock(FailedCallReports.class);

        kilkariCallDetailsService.processCallDeliveryFailureRequest(failedCallReports);

        verify(obdService).processCallDeliveryFailure(failedCallReports);
    }
}
