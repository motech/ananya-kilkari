package org.motechproject.ananya.kilkari.obd.service;


import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.obd.domain.ValidFailedCallReport;
import org.motechproject.ananya.kilkari.obd.repository.OnMobileOBDGateway;
import org.motechproject.ananya.kilkari.obd.service.request.FailedCallReports;
import org.motechproject.ananya.kilkari.obd.service.request.InvalidFailedCallReport;
import org.motechproject.ananya.kilkari.obd.service.request.InvalidFailedCallReports;
import org.motechproject.scheduler.domain.MotechEvent;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class CallDeliveryFailureRecordHandlerTest {
    @Mock
    private OnMobileOBDGateway onMobileOBDGateway;
    @Mock
    private CampaignMessageService campaignMessageService;
    @Mock
    private OBDCallDetailsService obdCallDetailsService;

    private CallDeliveryFailureRecordHandler callDeliveryFailureRecordHandler;

    @Before
    public void setUp() {
        initMocks(this);
        callDeliveryFailureRecordHandler = new CallDeliveryFailureRecordHandler(onMobileOBDGateway, campaignMessageService, obdCallDetailsService);
    }

    @Test
    public void shouldSendInvalidCallDeliveryFailureRecordsToObd() {
        HashMap<String, Object> parameters = new HashMap<>();
        InvalidFailedCallReports failureRecordFailed = new InvalidFailedCallReports();
        ArrayList<InvalidFailedCallReport> recordObjectFaileds = new ArrayList<>();
        recordObjectFaileds.add(new InvalidFailedCallReport("msisdn1", "subscriptionId1", "description1"));
        recordObjectFaileds.add(new InvalidFailedCallReport("msisdn2", "subscriptionId2", "description2"));
        failureRecordFailed.setRecordObjectFaileds(recordObjectFaileds);
        parameters.put("0", failureRecordFailed);

        callDeliveryFailureRecordHandler.handleInvalidCallDeliveryFailureRecord(new MotechEvent(CallDeliveryFailureEventKeys.PROCESS_INVALID_CALL_DELIVERY_FAILURE_RECORD, parameters));

        ArgumentCaptor<InvalidFailedCallReports> invalidCallDeliveryFailureRecordArgumentCaptor = ArgumentCaptor.forClass(InvalidFailedCallReports.class);
        verify(onMobileOBDGateway).sendInvalidFailureRecord(invalidCallDeliveryFailureRecordArgumentCaptor.capture());
        InvalidFailedCallReports invalidFailedCallReports = invalidCallDeliveryFailureRecordArgumentCaptor.getValue();

        assertEquals(2, invalidFailedCallReports.getRecordObjectFaileds().size());
        assertEquals("msisdn1", invalidFailedCallReports.getRecordObjectFaileds().get(0).getMsisdn());
        assertEquals("subscriptionId1", invalidFailedCallReports.getRecordObjectFaileds().get(0).getSubscriptionId());
        assertEquals("description1", invalidFailedCallReports.getRecordObjectFaileds().get(0).getDescription());
        assertEquals("msisdn2", invalidFailedCallReports.getRecordObjectFaileds().get(1).getMsisdn());
        assertEquals("subscriptionId2", invalidFailedCallReports.getRecordObjectFaileds().get(1).getSubscriptionId());
        assertEquals("description2", invalidFailedCallReports.getRecordObjectFaileds().get(1).getDescription());
    }

    @Test
    public void shouldInvokeServiceToProcessCallDeliveryFailureRecords() {
        HashMap<String, Object> parameters = new HashMap<>();
        ValidFailedCallReport failedCallReport = mock(ValidFailedCallReport.class);
        parameters.put("0", failedCallReport);

        callDeliveryFailureRecordHandler.handleValidCallDeliveryFailureRecord(new MotechEvent(CallDeliveryFailureEventKeys.PROCESS_VALID_CALL_DELIVERY_FAILURE_RECORD, parameters));

        verify(campaignMessageService).processValidCallDeliveryFailureRecords(failedCallReport);
    }

    @Test
    public void shouldProcessCallDeliveryFailureRecords() {
        HashMap<String, Object> parameters = new HashMap<>();
        FailedCallReports failureRecordFailed = mock(FailedCallReports.class);
        parameters.put("0", failureRecordFailed);

        callDeliveryFailureRecordHandler.handleCallDeliveryFailureRecord(new MotechEvent(CallDeliveryFailureEventKeys.PROCESS_CALL_DELIVERY_FAILURE_REQUEST, parameters));

        verify(obdCallDetailsService).processCallDeliveryFailureRecord(failureRecordFailed);
    }

}
