package org.motechproject.ananya.kilkari.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.domain.CallDetailRecord;
import org.motechproject.ananya.kilkari.domain.OBDRequest;
import org.motechproject.ananya.kilkari.domain.OBDRequestWrapper;
import org.motechproject.ananya.kilkari.domain.Subscription;
import org.motechproject.ananya.kilkari.service.KilkariCampaignService;
import org.motechproject.ananya.kilkari.service.SubscriptionService;
import org.motechproject.ananya.kilkari.web.TestUtils;
import org.motechproject.ananya.kilkari.web.validators.OBDRequestValidator;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
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

    @Mock
    private OBDRequestValidator obdRequestValidator;

    private OBDController obdController;

    private static final String CONTENT_TYPE_JSON = "application/json;charset=UTF-8";

    @Before
    public void setUp() {
        initMocks(this);
        obdController = new OBDController(subscriptionService, kilkariCampaignService, obdRequestValidator);
    }

    @Test
    public void shouldHandleSuccessfulResponseFromObd() throws Exception {
        when(obdRequestValidator.validate(any(OBDRequest.class), anyString())).thenReturn(new ArrayList<String>());

        OBDRequest obdRequest = new OBDRequest();
        obdRequest.setMsisdn("1234567890");
        obdRequest.setCampaignId("WEEK12");
        obdRequest.setServiceOption("HELP");
        CallDetailRecord callDetailRecord = new CallDetailRecord();
        callDetailRecord.setStartTime("21-11-2012 22-10-15");
        callDetailRecord.setEndTime("23-11-2012 22-10-15");
        obdRequest.setCallDetailRecord(callDetailRecord);
        byte[] requestBody = TestUtils.toJson(obdRequest).getBytes();
        when(subscriptionService.findBySubscriptionId("abcd1234")).thenReturn(new Subscription());

        mockMvc(obdController)
                .perform(post("/obd/calldetails/abcd1234").body(requestBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().type(CONTENT_TYPE_JSON))
                .andExpect(content().string(baseResponseMatcher("SUCCESS", "OBD call details received successfully")));
    }

    @Test
    public void shouldValidateObdRequest() throws Exception {
        when(obdRequestValidator.validate(any(OBDRequest.class), anyString())).thenReturn(Arrays.asList("Invalid msisdn 12345", "Invalid service option RANDOM_SERVICE_OPTION", "Invalid campaign id WEEKabc123"));

        OBDRequest obdRequest = new OBDRequest();
        obdRequest.setMsisdn("12345");
        obdRequest.setCampaignId("WEEKabc123");
        obdRequest.setServiceOption("RANDOM_SERVICE_OPTION");
        CallDetailRecord callDetailRecord = new CallDetailRecord();
        callDetailRecord.setStartTime("21-11-2012 22-10-15");
        callDetailRecord.setEndTime("23-11-2012 22-10-15");
        obdRequest.setCallDetailRecord(callDetailRecord);
        byte[] requestBody = TestUtils.toJson(obdRequest).getBytes();
        when(subscriptionService.findBySubscriptionId("abcd1234")).thenReturn(new Subscription());

        mockMvc(obdController)
                .perform(post("/obd/calldetails/abcd1234").body(requestBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().type(CONTENT_TYPE_JSON))
                .andExpect(content().string(baseResponseMatcher("ERROR", "OBD Request Invalid: Invalid msisdn 12345,Invalid service option RANDOM_SERVICE_OPTION,Invalid campaign id WEEKabc123")));
    }

    @Test
    public void shouldValidateSubscriptionId() throws Exception {
        String subscriptionId = "abcd1234";

        when(obdRequestValidator.validate(any(OBDRequest.class), anyString())).thenReturn(Arrays.asList("Invalid msisdn null" ,"Invalid service option null", "Invalid campaign id null" ,"Invalid subscription id " + subscriptionId));
        when(subscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(null);
        byte[] requestBody = TestUtils.toJson(new OBDRequest()).getBytes();

        mockMvc(obdController)
                .perform(post("/obd/calldetails/" + subscriptionId).body(requestBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().type(CONTENT_TYPE_JSON))
                .andExpect(content().string(baseResponseMatcher("ERROR", "OBD Request Invalid: Invalid msisdn null,Invalid service option null,Invalid campaign id null,Invalid subscription id " + subscriptionId)));
    }

    @Test
    public void shouldInvokeKilkariCampaignServiceForProcessingValidObdRequest() throws Exception {
        when(obdRequestValidator.validate(any(OBDRequest.class), anyString())).thenReturn(new ArrayList<String>());

        String subscriptionId = "abcd1234";
        OBDRequest obdRequest = new OBDRequest();
        obdRequest.setMsisdn("1234567890");
        obdRequest.setCampaignId("WEEK13");
        obdRequest.setServiceOption("HELP");
        CallDetailRecord callDetailRecord = new CallDetailRecord();
        callDetailRecord.setStartTime("21-11-2012 22-10-15");
        callDetailRecord.setEndTime("23-11-2012 22-10-15");
        obdRequest.setCallDetailRecord(callDetailRecord);
        byte[] requestBody = TestUtils.toJson(obdRequest).getBytes();
        when(subscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(new Subscription());

        mockMvc(obdController)
                .perform(post("/obd/calldetails/" + subscriptionId).body(requestBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().type(CONTENT_TYPE_JSON))
                .andExpect(content().string(baseResponseMatcher("SUCCESS", "OBD call details received successfully")));

        ArgumentCaptor<OBDRequestWrapper> obdRequestWrapperArgumentCaptor = ArgumentCaptor.forClass(OBDRequestWrapper.class);
        verify(kilkariCampaignService).processSuccessfulMessageDelivery(obdRequestWrapperArgumentCaptor.capture());
        OBDRequestWrapper obdRequestWrapper = obdRequestWrapperArgumentCaptor.getValue();

        assertEquals(subscriptionId, obdRequestWrapper.getSubscriptionId());
        assertEquals(obdRequest, obdRequestWrapper.getObdRequest());
        assertNotNull(obdRequestWrapper.getCreatedAt());
    }
}
