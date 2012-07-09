package org.motechproject.ananya.kilkari.web.it;

import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.motechproject.ananya.kilkari.builder.SubscriptionRequestBuilder;
import org.motechproject.ananya.kilkari.domain.*;
import org.motechproject.ananya.kilkari.messagecampaign.request.KilkariMessageCampaignEnrollmentRecord;
import org.motechproject.ananya.kilkari.messagecampaign.service.KilkariMessageCampaignService;
import org.motechproject.ananya.kilkari.repository.AllSubscriptions;
import org.motechproject.ananya.kilkari.service.OnMobileSubscriptionService;
import org.motechproject.ananya.kilkari.service.ReportingService;
import org.motechproject.ananya.kilkari.service.stub.StubOnMobileSubscriptionService;
import org.motechproject.ananya.kilkari.service.stub.StubReportingService;
import org.motechproject.ananya.kilkari.web.SpringIntegrationTest;
import org.motechproject.ananya.kilkari.web.contract.mapper.SubscriptionDetailsMapper;
import org.motechproject.ananya.kilkari.web.contract.response.BaseResponse;
import org.motechproject.ananya.kilkari.web.contract.response.SubscriberResponse;
import org.motechproject.ananya.kilkari.web.controller.SubscriptionController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.server.MvcResult;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.motechproject.ananya.kilkari.web.MVCTestUtils.mockMvc;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

public class SubscriptionControllerIT extends SpringIntegrationTest {

    @Autowired
    private SubscriptionController subscriptionController;

    @Autowired
    private AllSubscriptions allSubscriptions;

    @Autowired
    private KilkariMessageCampaignService kilkariMessageCampaignService;

    @Autowired
    private StubReportingService reportingService;

    @Autowired
    private StubOnMobileSubscriptionService onMobileSubscriptionService;

    @Before
    public void setUp()  {
        allSubscriptions.removeAll();
    }

    private static final String CONTENT_TYPE_JAVASCRIPT = "application/javascript;charset=UTF-8";
    private static final String CONTENT_TYPE_JSON = "application/json;charset=UTF-8";
    private static final String IVR_RESPONSE_PREFIX = "var response = ";


    private static final String SEVEN_MONTH_CAMPAIGN_NAME = "kilkari-mother-child-campaign-seven-months";
    private static final String TWELVE_MONTH_CAMPAIGN_NAME = "kilkari-mother-child-campaign-twelve-months";
    private static final String FIFTEEN_MONTH_CAMPAIGN_NAME = "kilkari-mother-child-campaign-fifteen-months";

    @Test
    public void shouldRetrieveSubscriptionDetailsFromDatabase() throws Exception {
        String msisdn = "9876543210";
        String channelString = Channel.IVR.toString();
        Subscription subscription1 = new Subscription(msisdn, SubscriptionPack.TWELVE_MONTHS);
        Subscription subscription2 = new Subscription(msisdn, SubscriptionPack.FIFTEEN_MONTHS);
        allSubscriptions.add(subscription1);
        allSubscriptions.add(subscription2);
        markForDeletion(subscription1);
        markForDeletion(subscription2);

        SubscriberResponse subscriberResponse = new SubscriberResponse();
        subscriberResponse.addSubscriptionDetail(SubscriptionDetailsMapper.mapFrom(subscription1));
        subscriberResponse.addSubscriptionDetail(SubscriptionDetailsMapper.mapFrom(subscription2));

        reportingService.setBehavior(mock(ReportingService.class));
        onMobileSubscriptionService.setBehavior(mock(OnMobileSubscriptionService.class));

        MvcResult result = mockMvc(subscriptionController)
                .perform(get("/subscriber").param("msisdn", msisdn).param("channel", channelString))
                .andExpect(status().isOk())
                .andExpect(content().type(SubscriptionControllerIT.CONTENT_TYPE_JAVASCRIPT))
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        responseString = performIVRChannelValidationAndCleanup(responseString, channelString);

        SubscriberResponse actualResponse = fromJson(responseString, SubscriberResponse.class);
        assertEquals(subscriberResponse, actualResponse);
    }

    @Test
    public void shouldCreateSubscriptionForTheGivenMsisdnForTheIVRChannel() throws Exception {

        final String msisdn = "9876543210";
        String channelString = Channel.IVR.toString();
        final SubscriptionPack pack = SubscriptionPack.TWELVE_MONTHS;
        BaseResponse expectedResponse = new BaseResponse("SUCCESS", "Subscription request submitted successfully");

        reportingService.setBehavior(mock(ReportingService.class));
        onMobileSubscriptionService.setBehavior(mock(OnMobileSubscriptionService.class));

        MvcResult result = mockMvc(subscriptionController)
                .perform(get("/subscription").param("msisdn", msisdn).param("pack", pack.toString())
                        .param("channel", channelString))
                .andExpect(status().isOk())
                .andExpect(content().type(SubscriptionControllerIT.CONTENT_TYPE_JAVASCRIPT))
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        responseString = performIVRChannelValidationAndCleanup(responseString, channelString);

        BaseResponse actualResponse =  (BaseResponse) new BaseResponse().fromJson(responseString);
        assertEquals(expectedResponse, actualResponse);

        final Subscription[] subscription = new Subscription[1];

        new TimedRunner(20,1000) {
            @Override
            boolean run() {
                List<Subscription> subscriptionList = allSubscriptions.findByMsisdnAndPack(msisdn, pack);
                subscription[0] = subscriptionList.isEmpty() ? null : subscriptionList.get(0);
                return (subscription[0] != null);

            }
        }.executeWithTimeout();

        assertNotNull(subscription[0]);
        markForDeletion(subscription[0]);
        assertEquals(msisdn, subscription[0].getMsisdn());
        assertEquals(pack, subscription[0].getPack());
        assertFalse(StringUtils.isBlank(subscription[0].getSubscriptionId()));

        final KilkariMessageCampaignEnrollmentRecord[] campaignEnrollmentRecord =
                new KilkariMessageCampaignEnrollmentRecord[1];

        new TimedRunner(20,1000) {
            @Override
            boolean run() {
                campaignEnrollmentRecord[0] = kilkariMessageCampaignService.searchEnrollment(
                        subscription[0].getSubscriptionId(), SubscriptionControllerIT.TWELVE_MONTH_CAMPAIGN_NAME);
                return (campaignEnrollmentRecord[0] != null);
            }
        }.executeWithTimeout();

        assertNotNull(campaignEnrollmentRecord[0]);
        assertEquals(subscription[0].getSubscriptionId(), campaignEnrollmentRecord[0].getExternalId());
        assertEquals(SubscriptionControllerIT.TWELVE_MONTH_CAMPAIGN_NAME, campaignEnrollmentRecord[0].getCampaignName());
        List<DateTime> messageTimings = kilkariMessageCampaignService.getMessageTimings(
                subscription[0].getSubscriptionId(), pack.name(), DateTime.now().minusDays(3), DateTime.now().plusYears(4));
        assertEquals(48, messageTimings.size());
    }

    @Test
    public void shouldCreateSubscriptionForTheGivenMsisdnForTheCallCentreChannel() throws Exception {
        final String msisdn = "9876543210";
        String channelString = Channel.CALL_CENTER.toString();
        final SubscriptionPack pack = SubscriptionPack.FIFTEEN_MONTHS;
        BaseResponse expectedResponse = new BaseResponse("SUCCESS", "Subscription request submitted successfully");

        ReportingService mockedReportingService = Mockito.mock(ReportingService.class);
        when(mockedReportingService.getLocation("district", "block", "panchayat")).thenReturn(new SubscriberLocation("district", "block", "panchayat"));
        reportingService.setBehavior(mockedReportingService);
        onMobileSubscriptionService.setBehavior(mock(OnMobileSubscriptionService.class));

        SubscriptionRequest expectedRequest= new SubscriptionRequestBuilder().withDefaults().build();
        MvcResult result = mockMvc(subscriptionController)
                .perform(post("/subscription").body(toJson(expectedRequest).getBytes()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().type(SubscriptionControllerIT.CONTENT_TYPE_JSON))
                .andReturn();


        String responseString = result.getResponse().getContentAsString();
        responseString = performIVRChannelValidationAndCleanup(responseString, channelString);

        BaseResponse actualResponse =  (BaseResponse) new BaseResponse().fromJson(responseString);
        assertEquals(expectedResponse, actualResponse);

        final Subscription[] subscription = new Subscription[1];

        new org.motechproject.ananya.kilkari.web.it.TimedRunner(20, 1000) {
            @Override
            boolean run() {
                List<Subscription> subscriptionList = allSubscriptions.findByMsisdnAndPack(msisdn, pack);
                subscription[0] = subscriptionList.isEmpty() ? null : subscriptionList.get(0);
                return (subscription[0] != null);

            }
        }.executeWithTimeout();

        assertNotNull(subscription[0]);
        markForDeletion(subscription[0]);
        assertEquals(msisdn, subscription[0].getMsisdn());
        assertEquals(pack, subscription[0].getPack());
        assertFalse(StringUtils.isBlank(subscription[0].getSubscriptionId()));

        final KilkariMessageCampaignEnrollmentRecord[] campaignEnrollmentRecord =
                new KilkariMessageCampaignEnrollmentRecord[1];

        new TimedRunner(20,1000) {
            @Override
            boolean run() {
                campaignEnrollmentRecord[0] = kilkariMessageCampaignService.searchEnrollment(
                        subscription[0].getSubscriptionId(), SubscriptionControllerIT.FIFTEEN_MONTH_CAMPAIGN_NAME);
                return (campaignEnrollmentRecord[0] != null);
            }
        }.executeWithTimeout();

        assertNotNull(campaignEnrollmentRecord[0]);
        assertEquals(subscription[0].getSubscriptionId(), campaignEnrollmentRecord[0].getExternalId());
        assertEquals(SubscriptionControllerIT.FIFTEEN_MONTH_CAMPAIGN_NAME, campaignEnrollmentRecord[0].getCampaignName());
        List<DateTime> messageTimings = kilkariMessageCampaignService.getMessageTimings(
                subscription[0].getSubscriptionId(), pack.name(), DateTime.now().minusDays(3), DateTime.now().plusYears(4));
        assertEquals(60, messageTimings.size());
    }

    private String performIVRChannelValidationAndCleanup(String jsonContent, String channel) {
        if (Channel.isIVR(channel)) {
            assertTrue(jsonContent.startsWith(SubscriptionControllerIT.IVR_RESPONSE_PREFIX));
            jsonContent = jsonContent.replace(SubscriptionControllerIT.IVR_RESPONSE_PREFIX, "");
        }
        return jsonContent;
    }

    private String toJson(Object objectToSerialize) {
        Gson gson = new Gson();
        return gson.toJson(objectToSerialize);
    }

    private <T> T fromJson(String jsonString, Class<T> subscriberResponseClass) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, subscriberResponseClass);
    }
}
