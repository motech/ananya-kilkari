package org.motechproject.ananya.kilkari.web.it;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.motechproject.ananya.kilkari.builder.SubscriptionWebRequestBuilder;
import org.motechproject.ananya.kilkari.messagecampaign.service.MessageCampaignService;
import org.motechproject.ananya.kilkari.reporting.domain.SubscriberLocation;
import org.motechproject.ananya.kilkari.reporting.service.ReportingService;
import org.motechproject.ananya.kilkari.reporting.service.StubReportingService;
import org.motechproject.ananya.kilkari.request.SubscriptionWebRequest;
import org.motechproject.ananya.kilkari.request.UnsubscriptionRequest;
import org.motechproject.ananya.kilkari.subscription.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionStatus;
import org.motechproject.ananya.kilkari.subscription.repository.AllSubscriptions;
import org.motechproject.ananya.kilkari.subscription.repository.OnMobileSubscriptionGateway;
import org.motechproject.ananya.kilkari.subscription.service.stub.StubOnMobileSubscriptionGateway;
import org.motechproject.ananya.kilkari.web.HttpHeaders;
import org.motechproject.ananya.kilkari.web.SpringIntegrationTest;
import org.motechproject.ananya.kilkari.web.TestUtils;
import org.motechproject.ananya.kilkari.web.controller.SubscriptionController;
import org.motechproject.ananya.kilkari.web.mapper.SubscriptionDetailsMapper;
import org.motechproject.ananya.kilkari.web.response.BaseResponse;
import org.motechproject.ananya.kilkari.web.response.SubscriptionWebResponse;
import org.motechproject.scheduler.MotechSchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.server.MvcResult;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.motechproject.ananya.kilkari.web.MVCTestUtils.mockMvc;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

public class SubscriptionControllerIT extends SpringIntegrationTest {

    @Autowired
    private SubscriptionController subscriptionController;

    @Autowired
    private AllSubscriptions allSubscriptions;

    @Autowired
    private MessageCampaignService messageCampaignService;

    @Autowired
    private StubReportingService reportingService;

    @Autowired
    private StubOnMobileSubscriptionGateway onMobileSubscriptionService;

    @Autowired
    private SubscriptionDetailsMapper subscriptionDetailsMapper;

    @Autowired
    private MotechSchedulerService motechSchedulerService;

    @Before
    public void setUp() {
        allSubscriptions.removeAll();
    }

    private static final String IVR_RESPONSE_PREFIX = "var response = ";

    private static final String TWELVE_MONTH_CAMPAIGN_NAME = "kilkari-mother-child-campaign-twelve-months";
    private static final String FIFTEEN_MONTH_CAMPAIGN_NAME = "kilkari-mother-child-campaign-fifteen-months";

    @Test
    public void shouldRetrieveSubscriptionDetailsFromDatabase() throws Exception {
        String msisdn = "9876543210";
        String channelString = Channel.IVR.toString();
        Subscription subscription1 = new Subscription(msisdn, SubscriptionPack.TWELVE_MONTHS, DateTime.now());
        Subscription subscription2 = new Subscription(msisdn, SubscriptionPack.FIFTEEN_MONTHS, DateTime.now());
        allSubscriptions.add(subscription1);
        allSubscriptions.add(subscription2);
        markForDeletion(subscription1);
        markForDeletion(subscription2);

        SubscriptionWebResponse subscriptionWebResponse = new SubscriptionWebResponse();
        subscriptionWebResponse.addSubscriptionDetail(subscriptionDetailsMapper.mapFrom(subscription1));
        subscriptionWebResponse.addSubscriptionDetail(subscriptionDetailsMapper.mapFrom(subscription2));

        onMobileSubscriptionService.setBehavior(mock(OnMobileSubscriptionGateway.class));

        MvcResult result = mockMvc(subscriptionController)
                .perform(get("/subscriber").param("msisdn", msisdn).param("channel", channelString))
                .andExpect(status().isOk())
                .andExpect(content().type(HttpHeaders.APPLICATION_JAVASCRIPT))
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        responseString = performIVRChannelValidationAndCleanup(responseString, channelString);

        SubscriptionWebResponse actualResponse = TestUtils.fromJson(responseString, SubscriptionWebResponse.class);
        assertEquals(subscriptionWebResponse, actualResponse);
    }

    @Test
    public void shouldCreateSubscriptionForTheGivenMsisdnForTheIVRChannel() throws Exception {
        final String msisdn = "9876543210";
        String channelString = Channel.IVR.toString();
        final SubscriptionPack pack = SubscriptionPack.TWELVE_MONTHS;
        BaseResponse expectedResponse = BaseResponse.success("Subscription request submitted successfully");

        reportingService.setBehavior(mock(ReportingService.class));
        onMobileSubscriptionService.setBehavior(mock(OnMobileSubscriptionGateway.class));

        MvcResult result = mockMvc(subscriptionController)
                .perform(get("/subscription").param("msisdn", msisdn).param("pack", pack.toString())
                        .param("channel", channelString))
                .andExpect(status().isOk())
                .andExpect(content().type(HttpHeaders.APPLICATION_JAVASCRIPT))
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        responseString = performIVRChannelValidationAndCleanup(responseString, channelString);

        BaseResponse actualResponse = TestUtils.fromJson(responseString, BaseResponse.class);
        assertEquals(expectedResponse, actualResponse);

        final Subscription subscription = new TimedRunner<Subscription>(20, 1000) {
            @Override
            Subscription run() {
                List<Subscription> subscriptionList = allSubscriptions.findByMsisdnAndPack(msisdn, pack);
                return subscriptionList.isEmpty() ? null : subscriptionList.get(0);
            }
        }.execute();

        assertNotNull(subscription);
        markForDeletion(subscription);
        assertEquals(msisdn, subscription.getMsisdn());
        assertEquals(pack, subscription.getPack());
        assertFalse(StringUtils.isBlank(subscription.getSubscriptionId()));
    }

    @Test
    public void shouldCreateSubscriptionForTheGivenMsisdnForTheCallCentreChannel() throws Exception {
        final String msisdn = "9876543210";
        String channelString = Channel.CALL_CENTER.toString();
        final SubscriptionPack pack = SubscriptionPack.FIFTEEN_MONTHS;
        BaseResponse expectedResponse = BaseResponse.success("Subscription request submitted successfully");

        ReportingService mockedReportingService = Mockito.mock(ReportingService.class);
        when(mockedReportingService.getLocation("district", "block", "panchayat")).thenReturn(new SubscriberLocation("district", "block", "panchayat"));
        reportingService.setBehavior(mockedReportingService);
        onMobileSubscriptionService.setBehavior(mock(OnMobileSubscriptionGateway.class));

        SubscriptionWebRequest expectedWebRequest = new SubscriptionWebRequestBuilder().withDefaults().withEDD(null).build();
        MvcResult result = mockMvc(subscriptionController)
                .perform(post("/subscription").body(TestUtils.toJson(expectedWebRequest).getBytes()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().type(HttpHeaders.APPLICATION_JSON))
                .andReturn();


        String responseString = result.getResponse().getContentAsString();
        responseString = performIVRChannelValidationAndCleanup(responseString, channelString);

        BaseResponse actualResponse = TestUtils.fromJson(responseString, BaseResponse.class);
        assertEquals(expectedResponse, actualResponse);

        final Subscription subscription = new TimedRunner<Subscription>(20, 1000) {
            @Override
            Subscription run() {
                List<Subscription> subscriptions = allSubscriptions.findByMsisdnAndPack(msisdn, pack);
                return subscriptions.isEmpty() ? null : subscriptions.get(0);

            }
        }.execute();

        assertNotNull(subscription);
        markForDeletion(subscription);
        assertEquals(msisdn, subscription.getMsisdn());
        assertEquals(pack, subscription.getPack());
        assertFalse(StringUtils.isBlank(subscription.getSubscriptionId()));
    }

    @Test
    public void shouldCreateAndScheduleAnEarlySubscription() throws Exception {
        final String msisdn = "9876543210";
        final String channelString = Channel.CALL_CENTER.toString();
        final SubscriptionPack pack = SubscriptionPack.FIFTEEN_MONTHS;
        DateTime now = DateTime.now();
        DateTime edd = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), 0, 0).plusMonths(4);
        DateTime expectedStartDate = SubscriptionPack.FIFTEEN_MONTHS.adjustStartDate(edd);
        BaseResponse expectedResponse = BaseResponse.success("Subscription request submitted successfully");

        ReportingService mockedReportingService = Mockito.mock(ReportingService.class);
        when(mockedReportingService.getLocation("district", "block", "panchayat")).thenReturn(new SubscriberLocation("district", "block", "panchayat"));
        reportingService.setBehavior(mockedReportingService);

        SubscriptionWebRequest expectedWebRequest = new SubscriptionWebRequestBuilder().withDefaults().withEDD(edd.toString("dd-MM-yyyy")).withCreatedAt(now).build();
        MvcResult result = mockMvc(subscriptionController)
                .perform(post("/subscription").body(TestUtils.toJson(expectedWebRequest).getBytes()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().type(HttpHeaders.APPLICATION_JSON))
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        responseString = performIVRChannelValidationAndCleanup(responseString, channelString);

        BaseResponse actualResponse = TestUtils.fromJson(responseString, BaseResponse.class);
        assertEquals(expectedResponse, actualResponse);

        Subscription subscription = allSubscriptions.findSubscriptionInProgress(msisdn, pack);

        assertNotNull(subscription);
        markForDeletion(subscription);
        assertEquals(msisdn, subscription.getMsisdn());
        assertEquals(pack, subscription.getPack());
        assertEquals(expectedStartDate.getMillis(), subscription.getStartDate().getMillis());
        assertFalse(StringUtils.isBlank(subscription.getSubscriptionId()));

        // TODO: Fetch job timings and assert
    }


    @Test
    public void shouldDeactivateSubscriptionForTheGivenMsisdnForTheCallCentreChannel() throws Exception {
        final String msisdn = "1111111111";
        String channelString = Channel.CALL_CENTER.toString();
        final SubscriptionPack pack = SubscriptionPack.FIFTEEN_MONTHS;
        BaseResponse expectedResponse = BaseResponse.success("Subscription unsubscribed successfully");

        reportingService.setBehavior(Mockito.mock(ReportingService.class));
        OnMobileSubscriptionGateway onMobileSubscriptionGateway = mock(OnMobileSubscriptionGateway.class);
        onMobileSubscriptionService.setBehavior(onMobileSubscriptionGateway);

        UnsubscriptionRequest expectedUnsubscriptionRequest = new UnsubscriptionRequest();
        expectedUnsubscriptionRequest.setChannel(Channel.CALL_CENTER.name());
        expectedUnsubscriptionRequest.setReason("Reason for deactivation");

        Subscription expectedSubscription = new Subscription(msisdn, pack, DateTime.now().minusMonths(15));
        expectedSubscription.setStatus(SubscriptionStatus.ACTIVE);
        allSubscriptions.add(expectedSubscription);
        markForDeletion(expectedSubscription);

        final String subscriptionId = expectedSubscription.getSubscriptionId();

        MvcResult result = mockMvc(subscriptionController)
                .perform(delete("/subscription/" + subscriptionId).body(TestUtils.toJson(expectedUnsubscriptionRequest).getBytes()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().type(HttpHeaders.APPLICATION_JSON))
                .andReturn();


        String responseString = result.getResponse().getContentAsString();
        responseString = performIVRChannelValidationAndCleanup(responseString, channelString);

        BaseResponse actualResponse = TestUtils.fromJson(responseString, BaseResponse.class);
        assertTrue(expectedResponse.equals(actualResponse));

        Boolean statusChanged = new TimedRunner<Boolean>(20, 1000) {
            @Override
            Boolean run() {
                Subscription subscription = allSubscriptions.findBySubscriptionId(subscriptionId);
                return subscription.getStatus() == SubscriptionStatus.PENDING_DEACTIVATION ? Boolean.TRUE : null;

            }
        }.execute();

        Boolean deactivationRequested = new TimedRunner<Boolean>(20, 1000) {
            @Override
            Boolean run() {
                return onMobileSubscriptionService.isDeactivateSubscriptionCalled() ? Boolean.TRUE : null;

            }
        }.execute();

        assertNotNull(statusChanged);
        assertNotNull(deactivationRequested);
    }


    private String performIVRChannelValidationAndCleanup(String jsonContent, String channel) {
        if (Channel.isIVR(channel)) {
            assertTrue(jsonContent.startsWith(SubscriptionControllerIT.IVR_RESPONSE_PREFIX));
            jsonContent = jsonContent.replace(SubscriptionControllerIT.IVR_RESPONSE_PREFIX, "");
        }
        return jsonContent;
    }
}
