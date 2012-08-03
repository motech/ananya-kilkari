package org.motechproject.ananya.kilkari.obd.handlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.obd.domain.OBDEventKeys;
import org.motechproject.ananya.kilkari.obd.domain.ValidFailedCallReport;
import org.motechproject.ananya.kilkari.obd.service.CampaignMessageService;
import org.motechproject.scheduler.domain.MotechEvent;

import java.util.HashMap;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class ValidCallDeliveryFailureRecordHandlerTest {
    @Mock
    private CampaignMessageService campaignMessageService;
    private ValidCallDeliveryFailureRecordHandler validCallDeliveryFailureRecordHandler;

    @Before
    public void setUp() {
        initMocks(this);
        validCallDeliveryFailureRecordHandler = new ValidCallDeliveryFailureRecordHandler(campaignMessageService);
    }

    @Test
    public void shouldInvokeServiceToProcessCallDeliveryFailureRecords() {
        HashMap<String, Object> parameters = new HashMap<>();
        ValidFailedCallReport failedCallReport = mock(ValidFailedCallReport.class);
        parameters.put("0", failedCallReport);

        validCallDeliveryFailureRecordHandler.handleValidCallDeliveryFailureRecord(new MotechEvent(OBDEventKeys.PROCESS_VALID_CALL_DELIVERY_FAILURE_RECORD, parameters));

        verify(campaignMessageService).processValidCallDeliveryFailureRecords(failedCallReport);
    }
}
