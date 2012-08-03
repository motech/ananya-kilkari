package org.motechproject.ananya.kilkari.handlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.obd.domain.OBDEventKeys;
import org.motechproject.ananya.kilkari.obd.request.FailedCallReports;
import org.motechproject.ananya.kilkari.service.KilkariCampaignService;
import org.motechproject.scheduler.domain.MotechEvent;

import java.util.HashMap;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class CallDeliveryFailureRecordHandlerTest {
    @Mock
    private KilkariCampaignService kilkariCampaignService;

    private CallDeliveryFailureRecordHandler callDeliveryFailureRecordHandler;

    @Before
    public void setUp() {
        initMocks(this);
        callDeliveryFailureRecordHandler = new CallDeliveryFailureRecordHandler(kilkariCampaignService);
    }

    @Test
    public void shouldInvokeKilkariCampaignServiceToProcessCallDeliveryFailureRecords() {
        HashMap<String, Object> parameters = new HashMap<>();
        FailedCallReports failureRecordFailed = mock(FailedCallReports.class);
        parameters.put("0", failureRecordFailed);

        callDeliveryFailureRecordHandler.handleCallDeliveryFailureRecord(new MotechEvent(OBDEventKeys.PROCESS_CALL_DELIVERY_FAILURE_REQUEST, parameters));

        verify(kilkariCampaignService).processCallDeliveryFailureRecord(failureRecordFailed);
    }
}
