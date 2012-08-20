package org.motechproject.ananya.kilkari.obd.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessageStatus;
import org.motechproject.ananya.kilkari.obd.domain.ValidFailedCallReport;
import org.motechproject.ananya.kilkari.obd.service.request.FailedCallReport;
import org.motechproject.ananya.kilkari.obd.service.request.FailedCallReports;
import org.motechproject.ananya.kilkari.obd.service.request.InvalidFailedCallReport;
import org.motechproject.ananya.kilkari.obd.service.request.InvalidFailedCallReports;
import org.motechproject.ananya.kilkari.obd.service.handler.OBDEventQueuePublisher;
import org.motechproject.ananya.kilkari.obd.service.validator.CallDeliveryFailureRecordValidator;
import org.motechproject.ananya.kilkari.obd.service.validator.Errors;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class OBDCallDetailsServiceTest {

    @Mock
    private CallDeliveryFailureRecordValidator callDeliveryFailureRecordValidator;
    @Mock
    private ValidCallDeliveryFailureRecordObjectMapper validCallDeliveryFailureRecordObjectMapper;
    @Mock
    private OBDEventQueuePublisher obdEventQueuePublisher;
    private OBDCallDetailsService obdCallDetailsService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        obdCallDetailsService = new OBDCallDetailsService(callDeliveryFailureRecordValidator, validCallDeliveryFailureRecordObjectMapper, obdEventQueuePublisher);
    }

    @Test
    public void shouldHandleCallDeliveryFailureRecord() {
        FailedCallReports failedCallReports = new FailedCallReports();

        ArrayList<FailedCallReport> reportArrayList = new ArrayList<>();
        FailedCallReport failedCallReport = mock(FailedCallReport.class);
        reportArrayList.add(failedCallReport);
        failedCallReports.setFailedCallReports(reportArrayList);

        when(callDeliveryFailureRecordValidator.validate(failedCallReport)).thenReturn(new Errors());

        obdCallDetailsService.processCallDeliveryFailureRecord(failedCallReports);

        verify(callDeliveryFailureRecordValidator, times(1)).validate(any(FailedCallReport.class));
    }

    @Test
    public void shouldPublishErroredOutCallDeliveryFailureRecords() {
        String msisdn = "12345";
        String subscriptionId = "abcd";
        FailedCallReports failedCallReports = new FailedCallReports();

        ArrayList<FailedCallReport> callDeliveryFailureRecordObjects = new ArrayList<>();
        FailedCallReport erroredOutFailedCallReport = mock(FailedCallReport.class);
        when(erroredOutFailedCallReport.getMsisdn()).thenReturn(msisdn);
        when(erroredOutFailedCallReport.getSubscriptionId()).thenReturn(subscriptionId);
        FailedCallReport successfulFailedCallReport = mock(FailedCallReport.class);
        callDeliveryFailureRecordObjects.add(erroredOutFailedCallReport);
        callDeliveryFailureRecordObjects.add(successfulFailedCallReport);
        failedCallReports.setFailedCallReports(callDeliveryFailureRecordObjects);

        when(callDeliveryFailureRecordValidator.validate(successfulFailedCallReport)).thenReturn(new Errors());

        Errors errors = new Errors();
        errors.add("Some error description");
        when(callDeliveryFailureRecordValidator.validate(erroredOutFailedCallReport)).thenReturn(errors);

        obdCallDetailsService.processCallDeliveryFailureRecord(failedCallReports);

        verify(callDeliveryFailureRecordValidator, times(2)).validate(any(FailedCallReport.class));

        ArgumentCaptor<InvalidFailedCallReports> invalidCallDeliveryFailureRecordArgumentCaptor = ArgumentCaptor.forClass(InvalidFailedCallReports.class);
        verify(obdEventQueuePublisher).publishInvalidCallDeliveryFailureRecord(invalidCallDeliveryFailureRecordArgumentCaptor.capture());
        InvalidFailedCallReports invalidFailedCallReports = invalidCallDeliveryFailureRecordArgumentCaptor.getValue();
        List<InvalidFailedCallReport> recordObjectFaileds = invalidFailedCallReports.getRecordObjectFaileds();

        assertEquals(1, recordObjectFaileds.size());
        assertEquals("Some error description", recordObjectFaileds.get(0).getDescription());
        assertEquals(msisdn, recordObjectFaileds.get(0).getMsisdn());
        assertEquals(subscriptionId, recordObjectFaileds.get(0).getSubscriptionId());
    }

    @Test
    public void shouldPublishSuccessfulCallDeliveryFailureRecords() {
        FailedCallReports failedCallReports = new FailedCallReports();

        ArrayList<FailedCallReport> callDeliveryFailureRecordObjects = new ArrayList<>();
        FailedCallReport erroredOutFailedCallReport = mock(FailedCallReport.class);
        FailedCallReport successfulFailedCallReport1 = new FailedCallReport("sub1", "1234567890", "WEEK13", "iu_dnp");
        FailedCallReport successfulFailedCallReport2 = new FailedCallReport("sub2", "1234567891", "WEEK13", "iu_dnc");
        callDeliveryFailureRecordObjects.add(erroredOutFailedCallReport);
        callDeliveryFailureRecordObjects.add(successfulFailedCallReport1);
        callDeliveryFailureRecordObjects.add(successfulFailedCallReport2);
        failedCallReports.setFailedCallReports(callDeliveryFailureRecordObjects);

        Errors errors = new Errors();
        errors.add("Some error description");
        when(callDeliveryFailureRecordValidator.validate(erroredOutFailedCallReport)).thenReturn(errors);
        Errors noError = new Errors();
        when(callDeliveryFailureRecordValidator.validate(successfulFailedCallReport1)).thenReturn(noError);
        when(callDeliveryFailureRecordValidator.validate(successfulFailedCallReport2)).thenReturn(noError);
        ValidFailedCallReport validFailedCallReport1 = new ValidFailedCallReport("sub1", "1234567890", "WEEK13", CampaignMessageStatus.DNP, DateTime.now());
        ValidFailedCallReport validFailedCallReport2 = new ValidFailedCallReport("sub2", "1234567891", "WEEK13", CampaignMessageStatus.DNC, DateTime.now());
        when(validCallDeliveryFailureRecordObjectMapper.mapFrom(successfulFailedCallReport1)).thenReturn(validFailedCallReport1);
        when(validCallDeliveryFailureRecordObjectMapper.mapFrom(successfulFailedCallReport2)).thenReturn(validFailedCallReport2);

        obdCallDetailsService.processCallDeliveryFailureRecord(failedCallReports);

        verify(callDeliveryFailureRecordValidator, times(3)).validate(any(FailedCallReport.class));

        ArgumentCaptor<ValidFailedCallReport> captor = ArgumentCaptor.forClass(ValidFailedCallReport.class);
        verify(obdEventQueuePublisher, times(2)).publishValidCallDeliveryFailureRecord(captor.capture());
        List<ValidFailedCallReport> actualValidFailedCallReports = captor.getAllValues();
        assertEquals(validFailedCallReport1, actualValidFailedCallReports.get(0));
        assertEquals(validFailedCallReport2, actualValidFailedCallReports.get(1));
    }

    @Test
    public void shouldNotPublishToErrorQueueIfErroredOutCallDeliveryFailureRecordsAreEmpty() {
        FailedCallReports failedCallReports = new FailedCallReports();

        ArrayList<FailedCallReport> callDeliveryFailureRecordObjects = new ArrayList<>();
        FailedCallReport successfulFailedCallReport = mock(FailedCallReport.class);
        callDeliveryFailureRecordObjects.add(successfulFailedCallReport);
        failedCallReports.setFailedCallReports(callDeliveryFailureRecordObjects);

        when(callDeliveryFailureRecordValidator.validate(successfulFailedCallReport)).thenReturn(new Errors());

        obdCallDetailsService.processCallDeliveryFailureRecord(failedCallReports);

        verify(callDeliveryFailureRecordValidator, times(1)).validate(any(FailedCallReport.class));
        verify(obdEventQueuePublisher, never()).publishInvalidCallDeliveryFailureRecord(any(InvalidFailedCallReports.class));
    }

}
