package org.motechproject.ananya.kilkari.web.it;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.motechproject.ananya.kilkari.TimedRunner;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessage;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessageStatus;
import org.motechproject.ananya.kilkari.obd.repository.AllCampaignMessages;
import org.motechproject.ananya.kilkari.obd.repository.OnMobileOBDGateway;
import org.motechproject.ananya.kilkari.obd.repository.StubOnMobileOBDGateway;
import org.motechproject.ananya.kilkari.obd.service.request.InvalidFailedCallReport;
import org.motechproject.ananya.kilkari.obd.service.request.InvalidFailedCallReports;
import org.motechproject.ananya.kilkari.reporting.service.ReportingService;
import org.motechproject.ananya.kilkari.reporting.service.StubReportingService;
import org.motechproject.ananya.kilkari.subscription.domain.Operator;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.repository.AllSubscriptions;
import org.motechproject.ananya.kilkari.web.HttpHeaders;
import org.motechproject.ananya.kilkari.web.SpringIntegrationTest;
import org.motechproject.ananya.kilkari.web.controller.OBDController;
import org.motechproject.ananya.reports.kilkari.contract.request.CallDetailsReportRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.motechproject.ananya.kilkari.web.MVCTestUtils.mockMvc;
import static org.motechproject.ananya.kilkari.web.controller.ResponseMatchers.baseResponseMatcher;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

public class OBDControllerIT extends SpringIntegrationTest {
    @Autowired
    private OBDController obdController;
    @Autowired
    private AllSubscriptions allSubscriptions;
    @Autowired
    private AllCampaignMessages allCampaignMessages;
    @Autowired
    private StubOnMobileOBDGateway stubOnMobileOBDGateway;
    @Autowired
    private StubReportingService stubReportingService;

    private Subscription subscription1;
    private Subscription subscription2;
    private String msisdn;
    private OnMobileOBDGateway onMobileOBDGateway;
    private ReportingService reportingService;

    @Before
    public void setUp() {
        allSubscriptions.removeAll();

        msisdn = "1234567890";
        subscription1 = new Subscription(msisdn, SubscriptionPack.NAVJAAT_KILKARI, DateTime.now(), DateTime.now(), null);
        subscription2 = new Subscription(msisdn, SubscriptionPack.BARI_KILKARI, DateTime.now(), DateTime.now(), null);
        allSubscriptions.add(subscription1);
        allSubscriptions.add(subscription2);
        markForDeletion(subscription1);
        markForDeletion(subscription2);

        CampaignMessage campaignMessage = new CampaignMessage(subscription1.getSubscriptionId(), "WEEK13", DateTime.now(), msisdn, Operator.AIRTEL.name(), DateTime.now().plusDays(2));
        allCampaignMessages.add(campaignMessage);
        markForDeletion(campaignMessage);

        CampaignMessage campaignMessage1 = new CampaignMessage(subscription2.getSubscriptionId(), "WEEK13", DateTime.now(), msisdn, Operator.AIRTEL.name(), DateTime.now().plusDays(2));
        allCampaignMessages.add(campaignMessage1);
        markForDeletion(campaignMessage1);

        onMobileOBDGateway = mock(OnMobileOBDGateway.class);
        stubOnMobileOBDGateway.setBehavior(onMobileOBDGateway);

        reportingService = mock(ReportingService.class);
        stubReportingService.setBehavior(reportingService);
    }

    @Test
    public void shouldHandleCallDeliveryFailureRecords() throws Exception {
        String callDeliveryFailureRecord1 = createCallDeliveryFailureRecordJSON(subscription1.getSubscriptionId(), subscription1.getMsisdn(), "WEEK13", "Q.850_96");
        String callDeliveryFailureRecord2 = createCallDeliveryFailureRecordJSON(subscription2.getSubscriptionId(), subscription2.getMsisdn(), "WEEK", "Q.850_18");
        String requestBody = "{\"callrecords\": [" + callDeliveryFailureRecord1 + "," + callDeliveryFailureRecord2 + "]}";

        mockMvc(obdController)
                .perform(post("/obd/calldetails").body(requestBody.getBytes()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().type(HttpHeaders.APPLICATION_JSON))
                .andExpect(content().string(baseResponseMatcher("SUCCESS", "OBD call delivery failure records received successfully")));


        new TimedRunner<Boolean>(20, 1000) {
            @Override
            public Boolean run() {
                return stubOnMobileOBDGateway.isInvalidFailureRecordCalled() ? Boolean.TRUE : null;
            }
        }.executeWithTimeout();
        stubOnMobileOBDGateway.setInvalidFailureRecordCalled(false);

        new TimedRunner<Boolean>(20, 1000) {
            @Override
            public Boolean run() {
                return stubReportingService.isReportCampaignMessageDeliveryCalled() ? Boolean.TRUE : null;
            }
        }.executeWithTimeout();
        stubReportingService.setReportCampaignMessageDeliveryCalled(false);


        verifyOMGatewayRequest();
        verifyReportingRequest();
    }

    private void verifyOMGatewayRequest() throws IOException {
        ArgumentCaptor<InvalidFailedCallReports> invalidCallDeliveryFailureRecordArgumentCaptor = ArgumentCaptor.forClass(InvalidFailedCallReports.class);
        verify(onMobileOBDGateway).sendInvalidFailureRecord(invalidCallDeliveryFailureRecordArgumentCaptor.capture());
        List<InvalidFailedCallReport> recordObjectFaileds = invalidCallDeliveryFailureRecordArgumentCaptor.getValue().getRecordObjectFaileds();

        assertEquals(1, recordObjectFaileds.size());
        assertEquals(msisdn, recordObjectFaileds.get(0).getMsisdn());
        assertEquals(subscription2.getSubscriptionId(), recordObjectFaileds.get(0).getSubscriptionId());
        assertEquals("Invalid campaign id WEEK", recordObjectFaileds.get(0).getDescription());
    }

    private void verifyReportingRequest() {
        ArgumentCaptor<CallDetailsReportRequest> campaignMessageDeliveryReportRequestArgumentCaptor = ArgumentCaptor.forClass(CallDetailsReportRequest.class);
        verify(reportingService).reportCampaignMessageDeliveryStatus(campaignMessageDeliveryReportRequestArgumentCaptor.capture());
        CallDetailsReportRequest reportRequest = campaignMessageDeliveryReportRequestArgumentCaptor.getValue();

        assertEquals(CampaignMessageStatus.ND.name(), reportRequest.getStatus());
        assertEquals("WEEK13", reportRequest.getCampaignId());
        assertEquals(msisdn, reportRequest.getMsisdn());
        assertEquals(subscription1.getSubscriptionId(), reportRequest.getSubscriptionId());
        assertNotNull(reportRequest.getStartTime());
        assertNotNull(reportRequest.getEndTime());
        assertNull(reportRequest.getServiceOption());
    }

    private String createCallDeliveryFailureRecordJSON(String subscriptionId, String msisdn, String campaignId, String statusCode) {
        String jsonTemplate = "{ \"subscriptionId\":\"%s\",\"msisdn\":\"%s\",\"campaignId\":\"%s\",\"statusCode\":\"%s\"}";
        return String.format(jsonTemplate, subscriptionId, msisdn, campaignId, statusCode);
    }
}
