package org.motechproject.ananya.kilkari.obd.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessage;
import org.motechproject.ananya.kilkari.obd.service.request.FailedCallReports;
import org.motechproject.ananya.kilkari.obd.service.request.InvalidOBDRequestEntries;
import org.motechproject.ananya.kilkari.obd.service.request.OBDSuccessfulCallDetailsRequest;
import org.motechproject.ananya.kilkari.obd.service.request.OBDSuccessfulCallDetailsRequestBuilder;
import org.motechproject.ananya.kilkari.reporting.service.ReportingService;
import org.motechproject.ananya.reports.kilkari.contract.request.CallDetailsReportRequest;

import static junit.framework.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class OBDServiceTest {

    @Mock
    private CampaignMessageService campaignMessageService;
    @Mock
    private OBDEventQueuePublisher obdEventQueuePublisher;
    @Mock
    private ReportingService reportingService;
    private OBDService obdService;
    private OBDSuccessfulCallDetailsRequest obdSuccessfulCallDetailsRequest;
    private CampaignMessage campaignMessage;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        campaignMessage = mock(CampaignMessage.class);

        obdSuccessfulCallDetailsRequest = new OBDSuccessfulCallDetailsRequestBuilder().withDefaults().build();
        obdService = new OBDService(obdEventQueuePublisher, campaignMessageService, reportingService);
    }

    @Test
    public void shouldPublishCallDeliveryFailure() {
        FailedCallReports failedCallReports = new FailedCallReports();

        obdService.processCallDeliveryFailure(failedCallReports);

        verify(obdEventQueuePublisher).publishCallDeliveryFailureRecord(failedCallReports);
    }

    @Test
    public void shouldPublishInvalidObdEntries() {
        InvalidOBDRequestEntries invalidOBDRequestEntries = new InvalidOBDRequestEntries();

        obdService.processInvalidOBDRequestEntries(invalidOBDRequestEntries);

        verify(obdEventQueuePublisher).publishInvalidOBDRequestEntries(invalidOBDRequestEntries);
    }

    @Test
    public void shouldProcessSuccessfulCallDelivery() {

        Integer retryCount = 1;
        when(campaignMessage.getNARetryCount()).thenReturn(retryCount);
        when(campaignMessageService.find(obdSuccessfulCallDetailsRequest.getSubscriptionId(), obdSuccessfulCallDetailsRequest.getCampaignId()))
                .thenReturn(campaignMessage);

        boolean processed = obdService.processSuccessfulCallDelivery(obdSuccessfulCallDetailsRequest);

        assertTrue(processed);
        verify(campaignMessageService).deleteCampaignMessage(campaignMessage);
        ArgumentCaptor<CallDetailsReportRequest> reportRequestArgumentCaptor = ArgumentCaptor.forClass(CallDetailsReportRequest.class);
        verify(reportingService).reportCampaignMessageDeliveryStatus(reportRequestArgumentCaptor.capture());
        CallDetailsReportRequest campaignMessageDeliveryReportRequest = reportRequestArgumentCaptor.getValue();

        assertEquals(obdSuccessfulCallDetailsRequest.getSubscriptionId(), campaignMessageDeliveryReportRequest.getSubscriptionId());
        assertEquals(obdSuccessfulCallDetailsRequest.getMsisdn(), campaignMessageDeliveryReportRequest.getMsisdn());
        assertEquals(obdSuccessfulCallDetailsRequest.getCampaignId(), campaignMessageDeliveryReportRequest.getCampaignId());
        assertEquals(obdSuccessfulCallDetailsRequest.getServiceOption().name(), campaignMessageDeliveryReportRequest.getServiceOption());
    }


    @Test
    public void shouldNotProcessIfCampaignMessageIsNotFound() {
        when(campaignMessageService.find(obdSuccessfulCallDetailsRequest.getSubscriptionId(), obdSuccessfulCallDetailsRequest.getCampaignId()))
                .thenReturn(null);

        boolean processed = obdService.processSuccessfulCallDelivery(obdSuccessfulCallDetailsRequest);

        assertFalse(processed);
        verify(campaignMessageService, never()).deleteCampaignMessage(any(CampaignMessage.class));
    }
}
