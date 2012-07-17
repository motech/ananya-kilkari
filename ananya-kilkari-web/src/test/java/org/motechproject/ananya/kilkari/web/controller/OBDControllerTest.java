package org.motechproject.ananya.kilkari.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.obd.contract.InvalidCallRecordRequestObject;
import org.motechproject.ananya.kilkari.obd.contract.InvalidCallRecordsRequest;
import org.motechproject.ananya.kilkari.obd.domain.CallDetailRecord;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallRequest;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallRequestWrapper;
import org.motechproject.ananya.kilkari.service.KilkariCampaignService;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.service.SubscriptionService;
import org.motechproject.ananya.kilkari.web.HttpHeaders;
import org.motechproject.ananya.kilkari.web.TestUtils;
import org.springframework.http.MediaType;

import java.util.ArrayList;

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
        obdController = new OBDController(subscriptionService, kilkariCampaignService);
    }

    @Test
    public void shouldHandleSuccessfulResponseFromObd() throws Exception {
        String subscriptionId = "abcd1234";
        OBDSuccessfulCallRequest successfulCallRequest = new OBDSuccessfulCallRequest();
        successfulCallRequest.setMsisdn("1234567890");
        successfulCallRequest.setCampaignId("WEEK12");
        successfulCallRequest.setServiceOption("HELP");
        CallDetailRecord callDetailRecord = new CallDetailRecord();
        callDetailRecord.setStartTime("21-11-2012 22-10-15");
        callDetailRecord.setEndTime("23-11-2012 22-10-15");
        successfulCallRequest.setCallDetailRecord(callDetailRecord);
        byte[] requestBody = TestUtils.toJson(successfulCallRequest).getBytes();
        when(subscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(new Subscription());

        mockMvc(obdController)
                .perform(post("/obd/calldetails/" + subscriptionId).body(requestBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().type(HttpHeaders.APPLICATION_JSON))
                .andExpect(content().string(baseResponseMatcher("SUCCESS", "OBD call details received successfully for subscriptionId : " + subscriptionId)));
    }

    @Test
    public void shouldInvokeKilkariCampaignServiceForProcessingValidObdRequest() throws Exception {
        String subscriptionId = "abcd1234";
        OBDSuccessfulCallRequest successfulCallRequest = new OBDSuccessfulCallRequest();
        successfulCallRequest.setMsisdn("1234567890");
        successfulCallRequest.setCampaignId("WEEK13");
        successfulCallRequest.setServiceOption("HELP");
        CallDetailRecord callDetailRecord = new CallDetailRecord();
        callDetailRecord.setStartTime("21-11-2012 22-10-15");
        callDetailRecord.setEndTime("23-11-2012 22-10-15");
        successfulCallRequest.setCallDetailRecord(callDetailRecord);
        byte[] requestBody = TestUtils.toJson(successfulCallRequest).getBytes();
        when(subscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(new Subscription());

        mockMvc(obdController)
                .perform(post("/obd/calldetails/" + subscriptionId).body(requestBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().type(HttpHeaders.APPLICATION_JSON))
                .andExpect(content().string(baseResponseMatcher("SUCCESS", "OBD call details received successfully for subscriptionId : " + subscriptionId)));

        ArgumentCaptor<OBDSuccessfulCallRequestWrapper> successfulCallRequestWrapperArgumentCaptor = ArgumentCaptor.forClass(OBDSuccessfulCallRequestWrapper.class);
        verify(kilkariCampaignService).processSuccessfulCallRequest(successfulCallRequestWrapperArgumentCaptor.capture());
        OBDSuccessfulCallRequestWrapper successfulCallRequestWrapper = successfulCallRequestWrapperArgumentCaptor.getValue();

        assertEquals(subscriptionId, successfulCallRequestWrapper.getSubscriptionId());
        assertEquals(successfulCallRequest, successfulCallRequestWrapper.getSuccessfulCallRequest());
        assertNotNull(successfulCallRequestWrapper.getCreatedAt());
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

        ArgumentCaptor<InvalidCallRecordsRequest> captor = ArgumentCaptor.forClass(InvalidCallRecordsRequest.class);
        verify(kilkariCampaignService).processInvalidCallRecordsRequest(captor.capture());
        InvalidCallRecordsRequest actualRequest = captor.getValue();
        ArrayList<InvalidCallRecordRequestObject> callrecords = actualRequest.getCallrecords();
        assertEquals(2, callrecords.size());

        InvalidCallRecordRequestObject invalidCallRecordRequestObject1 = callrecords.get(0);
        assertEquals("msisdn1", invalidCallRecordRequestObject1.getMsisdn());
        assertEquals("subscriptionId1", invalidCallRecordRequestObject1.getSubscriptionId());
        assertEquals("campaignId1", invalidCallRecordRequestObject1.getCampaignId());
        assertEquals("operator1", invalidCallRecordRequestObject1.getOperator());
        assertEquals("description1", invalidCallRecordRequestObject1.getDescription());

        InvalidCallRecordRequestObject invalidCallRecordRequestObject2 = callrecords.get(0);
        assertEquals("msisdn1", invalidCallRecordRequestObject2.getMsisdn());
        assertEquals("subscriptionId1", invalidCallRecordRequestObject2.getSubscriptionId());
        assertEquals("campaignId1", invalidCallRecordRequestObject2.getCampaignId());
        assertEquals("operator1", invalidCallRecordRequestObject2.getOperator());
        assertEquals("description1", invalidCallRecordRequestObject2.getDescription());
    }

    @Test
    public void shouldProcessInvalidCallRecordsRequestWithZeroItems() throws Exception {
        String requestBody = "{}";
        mockMvc(obdController)
                .perform(post("/obd/invalidcallrecords").body(requestBody.getBytes()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().type(HttpHeaders.APPLICATION_JSON))
                .andExpect(content().string(baseResponseMatcher("SUCCESS", "OBD invalid call records received successfully")));

        ArgumentCaptor<InvalidCallRecordsRequest> captor = ArgumentCaptor.forClass(InvalidCallRecordsRequest.class);
        verify(kilkariCampaignService).processInvalidCallRecordsRequest(captor.capture());
        InvalidCallRecordsRequest actualRequest = captor.getValue();
        ArrayList<InvalidCallRecordRequestObject> callrecords = actualRequest.getCallrecords();
        assertTrue(callrecords.isEmpty());
    }

    private String createInvalidCallRecordJSON(String msisdn, String subscriptionId, String campaignId, String operator, String description) {
        String jsonTemplate = "{\"msisdn\":\"%s\", \"subscriptionId\":\"%s\",\"campaignId\":\"%s\",\"operator\":\"%s\",\"description\":\"%s\"}";
        return String.format(jsonTemplate, msisdn, subscriptionId, campaignId, operator, description);
    }
}
