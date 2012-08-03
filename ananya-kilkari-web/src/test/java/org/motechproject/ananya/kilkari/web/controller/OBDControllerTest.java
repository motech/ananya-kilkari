package org.motechproject.ananya.kilkari.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.obd.request.FailedCallReports;
import org.motechproject.ananya.kilkari.obd.request.FailedCallReport;
import org.motechproject.ananya.kilkari.obd.request.InvalidOBDRequestEntries;
import org.motechproject.ananya.kilkari.obd.domain.CallDetailRecord;
import org.motechproject.ananya.kilkari.obd.request.InvalidOBDRequestEntry;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallDetailsRequest;
import org.motechproject.ananya.kilkari.service.KilkariCampaignService;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.service.SubscriptionService;
import org.motechproject.ananya.kilkari.web.HttpHeaders;
import org.motechproject.ananya.kilkari.web.TestUtils;
import org.springframework.http.MediaType;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ananya.kilkari.web.MVCTestUtils.mockMvc;
import static org.motechproject.ananya.kilkari.web.controller.ResponseMatchers.baseResponseMatcher;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

public class OBDControllerTest {

    @Mock
    private SubscriptionService subscriptionService;

    @Mock
    private KilkariCampaignService kilkariCampaignService;

    private OBDController obdController;

    @Before
    public void setUp() {
        initMocks(this);
        obdController = new OBDController(kilkariCampaignService);
    }

    @Test
    public void shouldHandleSuccessfulResponseFromObd() throws Exception {
        String subscriptionId = "abcd1234";
        OBDSuccessfulCallDetailsRequest successfulCallDetailsRequest = new OBDSuccessfulCallDetailsRequest();
        successfulCallDetailsRequest.setMsisdn("1234567890");
        successfulCallDetailsRequest.setCampaignId("WEEK12");
        successfulCallDetailsRequest.setServiceOption("HELP");
        CallDetailRecord callDetailRecord = new CallDetailRecord();
        callDetailRecord.setStartTime("21-11-2012 22-10-15");
        callDetailRecord.setEndTime("23-11-2012 22-10-15");
        successfulCallDetailsRequest.setCallDetailRecord(callDetailRecord);
        byte[] requestBody = TestUtils.toJson(successfulCallDetailsRequest).getBytes();
        when(subscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(new Subscription());

        mockMvc(obdController)
                .perform(post("/obd/calldetails/" + subscriptionId).body(requestBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().type(HttpHeaders.APPLICATION_JSON))
                .andExpect(content().string(baseResponseMatcher("SUCCESS", "OBD call details received successfully for subscriptionId : " + subscriptionId)));
    }

    @Test
    public void shouldHandleCallDeliveryFailureRecordFromObd() throws Exception {
        String callDeliveryFailureRecord1 = createCallDeliveryFailureRecordJSON("subscriptionId1", "msisdn1", "campaignId1", "DNP");
        String callDeliveryFailureRecord2 = createCallDeliveryFailureRecordJSON("subscriptionId2", "msisdn2", "campaignId2", "DNP");
        String requestBody = "{\"callrecords\": [" + callDeliveryFailureRecord1 + "," + callDeliveryFailureRecord2 + "]}";
        mockMvc(obdController)
                .perform(post("/obd/calldetails").body(requestBody.getBytes()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().type(HttpHeaders.APPLICATION_JSON))
                .andExpect(content().string(baseResponseMatcher("SUCCESS", "OBD call delivery failure records received successfully")));

        ArgumentCaptor<FailedCallReports> captor = ArgumentCaptor.forClass(FailedCallReports.class);
        verify(kilkariCampaignService).publishCallDeliveryFailureRequest(captor.capture());
        FailedCallReports failedCallReports = captor.getValue();
        List<FailedCallReport> callDeliveryFailureRecordObjects = failedCallReports.getCallrecords();
        assertEquals(2, callDeliveryFailureRecordObjects.size());

        assertNotNull(failedCallReports.getCreatedAt());
        FailedCallReport failedCallReport1 = callDeliveryFailureRecordObjects.get(0);
        assertEquals("msisdn1", failedCallReport1.getMsisdn());
        assertEquals("subscriptionId1", failedCallReport1.getSubscriptionId());
        assertEquals("campaignId1", failedCallReport1.getCampaignId());
        assertEquals("DNP", failedCallReport1.getStatusCode());

        FailedCallReport failedCallReport2 = callDeliveryFailureRecordObjects.get(1);
        assertEquals("msisdn2", failedCallReport2.getMsisdn());
        assertEquals("subscriptionId2", failedCallReport2.getSubscriptionId());
        assertEquals("campaignId2", failedCallReport2.getCampaignId());
        assertEquals("DNP", failedCallReport2.getStatusCode());
    }

    @Test
    public void shouldInvokeKilkariCampaignServiceForProcessingValidObdRequest() throws Exception {
        String subscriptionId = "abcd1234";
        OBDSuccessfulCallDetailsRequest successfulCallDetailsRequest = new OBDSuccessfulCallDetailsRequest();
        successfulCallDetailsRequest.setMsisdn("1234567890");
        successfulCallDetailsRequest.setCampaignId("WEEK13");
        successfulCallDetailsRequest.setServiceOption("HELP");
        CallDetailRecord callDetailRecord = new CallDetailRecord();
        callDetailRecord.setStartTime("21-11-2012 22-10-15");
        callDetailRecord.setEndTime("23-11-2012 22-10-15");
        successfulCallDetailsRequest.setCallDetailRecord(callDetailRecord);
        successfulCallDetailsRequest.setSubscriptionId(subscriptionId);
        byte[] requestBody = TestUtils.toJson(successfulCallDetailsRequest).getBytes();
        when(subscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(new Subscription());

        mockMvc(obdController)
                .perform(post("/obd/calldetails/" + subscriptionId).body(requestBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().type(HttpHeaders.APPLICATION_JSON))
                .andExpect(content().string(baseResponseMatcher("SUCCESS", "OBD call details received successfully for subscriptionId : " + subscriptionId)));

        ArgumentCaptor<OBDSuccessfulCallDetailsRequest> successfulCallRequestArgumentCaptor = ArgumentCaptor.forClass(OBDSuccessfulCallDetailsRequest.class);
        verify(kilkariCampaignService).publishSuccessfulCallRequest(successfulCallRequestArgumentCaptor.capture());
        OBDSuccessfulCallDetailsRequest obdSuccessfulCallDetailsRequest = successfulCallRequestArgumentCaptor.getValue();

        assertEquals(subscriptionId, obdSuccessfulCallDetailsRequest.getSubscriptionId());
        assertNotNull(obdSuccessfulCallDetailsRequest.getCreatedAt());
    }

    @Test
    public void shouldProcessInvalidRecordsRequest() throws Exception {
        String invalidRecordJSON1 = createInvalidCallRecordJSON("msisdn1", "subscriptionId1", "campaignId1", "operator1", "description1");
        String invalidRecordJSON2 = createInvalidCallRecordJSON("msisdn2", "subscriptionId2", "campaignId2", "operator2", "description2");
        String requestBody = "{\"callrecords\": [" + invalidRecordJSON1 + "," + invalidRecordJSON2 + "]}";
        mockMvc(obdController)
                .perform(post("/obd/invalidcallrecords").body(requestBody.getBytes()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().type(HttpHeaders.APPLICATION_JSON))
                .andExpect(content().string(baseResponseMatcher("SUCCESS", "OBD invalid call records received successfully")));

        ArgumentCaptor<InvalidOBDRequestEntries> captor = ArgumentCaptor.forClass(InvalidOBDRequestEntries.class);
        verify(kilkariCampaignService).publishInvalidCallRecordsRequest(captor.capture());
        InvalidOBDRequestEntries actualRequestEntries = captor.getValue();
        List<InvalidOBDRequestEntry> callrecords = actualRequestEntries.getInvalidOBDRequestEntryList();
        assertEquals(2, callrecords.size());

        InvalidOBDRequestEntry invalidOBDRequestEntry1 = callrecords.get(0);
        assertEquals("msisdn1", invalidOBDRequestEntry1.getMsisdn());
        assertEquals("subscriptionId1", invalidOBDRequestEntry1.getSubscriptionId());
        assertEquals("campaignId1", invalidOBDRequestEntry1.getCampaignId());
        assertEquals("operator1", invalidOBDRequestEntry1.getOperator());
        assertEquals("description1", invalidOBDRequestEntry1.getDescription());

        InvalidOBDRequestEntry invalidOBDRequestEntry2 = callrecords.get(0);
        assertEquals("msisdn1", invalidOBDRequestEntry2.getMsisdn());
        assertEquals("subscriptionId1", invalidOBDRequestEntry2.getSubscriptionId());
        assertEquals("campaignId1", invalidOBDRequestEntry2.getCampaignId());
        assertEquals("operator1", invalidOBDRequestEntry2.getOperator());
        assertEquals("description1", invalidOBDRequestEntry2.getDescription());
    }

    @Test
    public void shouldProcessInvalidCallRecordsRequestWithZeroItems() throws Exception {
        String requestBody = "{}";
        mockMvc(obdController)
                .perform(post("/obd/invalidcallrecords").body(requestBody.getBytes()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().type(HttpHeaders.APPLICATION_JSON))
                .andExpect(content().string(baseResponseMatcher("SUCCESS", "OBD invalid call records received successfully")));

        ArgumentCaptor<InvalidOBDRequestEntries> captor = ArgumentCaptor.forClass(InvalidOBDRequestEntries.class);
        verify(kilkariCampaignService).publishInvalidCallRecordsRequest(captor.capture());
        InvalidOBDRequestEntries actualRequestEntries = captor.getValue();
        List<InvalidOBDRequestEntry> callrecords = actualRequestEntries.getInvalidOBDRequestEntryList();
        assertTrue(callrecords.isEmpty());
    }

    private String createInvalidCallRecordJSON(String msisdn, String subscriptionId, String campaignId, String operator, String description) {
        String jsonTemplate = "{\"msisdn\":\"%s\", \"subscriptionId\":\"%s\",\"campaignId\":\"%s\",\"operator\":\"%s\",\"description\":\"%s\"}";
        return String.format(jsonTemplate, msisdn, subscriptionId, campaignId, operator, description);
    }

    private String createCallDeliveryFailureRecordJSON(String subscriptionId, String msisdn, String campaignId, String statusCode) {
        String jsonTemplate = "{ \"subscriptionId\":\"%s\",\"msisdn\":\"%s\",\"campaignId\":\"%s\",\"statusCode\":\"%s\"}";
        return String.format(jsonTemplate, subscriptionId, msisdn, campaignId, statusCode);
    }
}
