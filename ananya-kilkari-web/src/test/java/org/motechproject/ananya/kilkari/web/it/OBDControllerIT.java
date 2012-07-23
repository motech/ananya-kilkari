package org.motechproject.ananya.kilkari.web.it;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.motechproject.ananya.kilkari.obd.contract.InvalidCallDeliveryFailureRecord;
import org.motechproject.ananya.kilkari.obd.contract.InvalidCallDeliveryFailureRecordObject;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessage;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessageStatus;
import org.motechproject.ananya.kilkari.obd.gateway.OnMobileOBDGateway;
import org.motechproject.ananya.kilkari.obd.gateway.StubOnMobileOBDGateway;
import org.motechproject.ananya.kilkari.obd.repository.AllCampaignMessages;
import org.motechproject.ananya.kilkari.reporting.domain.CampaignMessageDeliveryReportRequest;
import org.motechproject.ananya.kilkari.reporting.service.ReportingService;
import org.motechproject.ananya.kilkari.reporting.service.StubReportingService;
import org.motechproject.ananya.kilkari.subscription.domain.Operator;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.repository.AllSubscriptions;
import org.motechproject.ananya.kilkari.web.HttpHeaders;
import org.motechproject.ananya.kilkari.web.SpringIntegrationTest;
import org.motechproject.ananya.kilkari.web.controller.OBDController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

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

    @Before
    public void setUp() {
        allSubscriptions.removeAll();
    }

    @Test
    public void shouldHandleCallDeliveryFailureRecords() throws Exception {
        String msisdn = "1234567890";
        Subscription subscription1 = new Subscription(msisdn, SubscriptionPack.TWELVE_MONTHS, DateTime.now());
        Subscription subscription2 = new Subscription(msisdn, SubscriptionPack.FIFTEEN_MONTHS, DateTime.now());
        allSubscriptions.add(subscription1);
        allSubscriptions.add(subscription2);
        markForDeletion(subscription1);
        markForDeletion(subscription2);
        CampaignMessage campaignMessage = new CampaignMessage(subscription1.getSubscriptionId(), "WEEK13", msisdn, Operator.AIRTEL.name(), DateTime.now().plusDays(2));
        allCampaignMessages.add(campaignMessage);
        markForDeletion(campaignMessage);

        OnMobileOBDGateway onMobileOBDGateway = mock(OnMobileOBDGateway.class);
        stubOnMobileOBDGateway.setBehavior(onMobileOBDGateway);

        ReportingService reportingService = mock(ReportingService.class);
        stubReportingService.setBehavior(reportingService);

        String callDeliveryFailureRecord1 = createCallDeliveryFailureRecordJSON(subscription1.getSubscriptionId(), subscription1.getMsisdn(), "WEEK13", "DNP");
        String callDeliveryFailureRecord2 = createCallDeliveryFailureRecordJSON(subscription2.getSubscriptionId(), subscription2.getMsisdn(), "WEEK", "DNP");
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
        }.execute();
        stubOnMobileOBDGateway.setInvalidFailureRecordCalled(false);

        new TimedRunner<Boolean>(20, 1000) {
            @Override
            public Boolean run() {
                return stubReportingService.isReportCampaignMessageDeliveryCalled() ? Boolean.TRUE : null;
            }
        }.execute();
        stubReportingService.setReportCampaignMessageDeliveryCalled(false);

        ArgumentCaptor<InvalidCallDeliveryFailureRecord> invalidCallDeliveryFailureRecordArgumentCaptor = ArgumentCaptor.forClass(InvalidCallDeliveryFailureRecord.class);
        verify(onMobileOBDGateway).sendInvalidFailureRecord(invalidCallDeliveryFailureRecordArgumentCaptor.capture());
        List<InvalidCallDeliveryFailureRecordObject> recordObjects = invalidCallDeliveryFailureRecordArgumentCaptor.getValue().getRecordObjects();

        ArgumentCaptor<CampaignMessageDeliveryReportRequest> campaignMessageDeliveryReportRequestArgumentCaptor = ArgumentCaptor.forClass(CampaignMessageDeliveryReportRequest.class);
        verify(reportingService).reportCampaignMessageDeliveryStatus(campaignMessageDeliveryReportRequestArgumentCaptor.capture());
        CampaignMessageDeliveryReportRequest reportRequest = campaignMessageDeliveryReportRequestArgumentCaptor.getValue();

        assertEquals(1, recordObjects.size());
        assertEquals(msisdn, recordObjects.get(0).getMsisdn());
        assertEquals(subscription2.getSubscriptionId(), recordObjects.get(0).getSubscriptionId());
        assertEquals("Invalid campaign id WEEK", recordObjects.get(0).getDescription());

        assertEquals(CampaignMessageStatus.DNP.name(), reportRequest.getStatus());
        assertEquals("0", reportRequest.getRetryCount());
        assertEquals("WEEK13", reportRequest.getCampaignId());
        assertEquals(msisdn, reportRequest.getMsisdn());
        assertEquals(subscription1.getSubscriptionId(), reportRequest.getSubscriptionId());
        assertNotNull(reportRequest.getCallDetailRecord().getStartTime());
        assertNotNull(reportRequest.getCallDetailRecord().getEndTime());
        assertNull(reportRequest.getServiceOption());

    }

    private String createCallDeliveryFailureRecordJSON(String subscriptionId, String msisdn, String campaignId, String statusCode) {
        String jsonTemplate = "{ \"subscriptionId\":\"%s\",\"msisdn\":\"%s\",\"campaignId\":\"%s\",\"statusCode\":\"%s\"}";
        return String.format(jsonTemplate, subscriptionId, msisdn, campaignId, statusCode);
    }
}
