package org.motechproject.ananya.kilkari.web.it;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.motechproject.ananya.kilkari.TimedRunner;
import org.motechproject.ananya.kilkari.builder.ChangeSubscriptionWebRequestBuilder;
import org.motechproject.ananya.kilkari.builder.SubscriptionWebRequestBuilder;
import org.motechproject.ananya.kilkari.messagecampaign.service.MessageCampaignService;
import org.motechproject.ananya.kilkari.obd.domain.Channel;
import org.motechproject.ananya.kilkari.reporting.service.ReportingService;
import org.motechproject.ananya.kilkari.reporting.service.StubReportingService;
import org.motechproject.ananya.kilkari.request.ChangeMsisdnWebRequest;
import org.motechproject.ananya.kilkari.request.ChangeSubscriptionWebRequest;
import org.motechproject.ananya.kilkari.request.SubscriptionWebRequest;
import org.motechproject.ananya.kilkari.request.UnSubscriptionWebRequest;
import org.motechproject.ananya.kilkari.subscription.domain.*;
import org.motechproject.ananya.kilkari.subscription.repository.AllSubscriptions;
import org.motechproject.ananya.kilkari.subscription.repository.OnMobileSubscriptionGateway;
import org.motechproject.ananya.kilkari.subscription.service.stub.StubOnMobileSubscriptionGateway;
import org.motechproject.ananya.kilkari.subscription.validators.DateUtils;
import org.motechproject.ananya.kilkari.web.HttpHeaders;
import org.motechproject.ananya.kilkari.web.SpringIntegrationTest;
import org.motechproject.ananya.kilkari.web.TestUtils;
import org.motechproject.ananya.kilkari.web.controller.SubscriptionController;
import org.motechproject.ananya.kilkari.web.mapper.SubscriptionDetailsMapper;
import org.motechproject.ananya.kilkari.web.response.BaseResponse;
import org.motechproject.ananya.kilkari.web.response.SubscriptionWebResponse;
import org.motechproject.ananya.reports.kilkari.contract.response.LocationResponse;
import org.motechproject.ananya.reports.kilkari.contract.response.SubscriberResponse;
import org.motechproject.scheduler.MotechSchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.server.MvcResult;

import java.util.Arrays;
import java.util.Date;
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

    @Test
    public void shouldRetrieveSubscriptionDetailsFromDatabase() throws Exception {
        String msisdn = "9876543210";
        String channelString = Channel.IVR.toString();
        Subscription subscription1 = new Subscription(msisdn, SubscriptionPack.NAVJAAT_KILKARI, DateTime.now(), DateTime.now(), null);
        Subscription subscription2 = new Subscription(msisdn, SubscriptionPack.BARI_KILKARI, DateTime.now(), DateTime.now(), null);
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
        final SubscriptionPack pack = SubscriptionPack.NAVJAAT_KILKARI;
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
            public Subscription run() {
                List<Subscription> subscriptionList = allSubscriptions.findByMsisdnAndPack(msisdn, pack);
                return subscriptionList.isEmpty() ? null : subscriptionList.get(0);
            }
        }.executeWithTimeout();

        assertNotNull(subscription);
        markForDeletion(subscription);
        assertEquals(msisdn, subscription.getMsisdn());
        assertEquals(pack, subscription.getPack());
        assertFalse(StringUtils.isBlank(subscription.getSubscriptionId()));
    }

    @Test
    public void shouldCreateSubscriptionForTheGivenMsisdnForTheCallCentreChannel() throws Exception {
        final String msisdn = "9876543210";
        String channelString = Channel.CONTACT_CENTER.toString();
        final SubscriptionPack pack = SubscriptionPack.BARI_KILKARI;
        BaseResponse expectedResponse = BaseResponse.success("Subscription request submitted successfully");

        ReportingService mockedReportingService = Mockito.mock(ReportingService.class);
        when(mockedReportingService.getLocation("district", "block", "panchayat")).thenReturn(new LocationResponse("district", "block", "panchayat"));
        reportingService.setBehavior(mockedReportingService);
        onMobileSubscriptionService.setBehavior(mock(OnMobileSubscriptionGateway.class));

        SubscriptionWebRequest expectedWebRequest = new SubscriptionWebRequestBuilder().withDefaults().withEDD(null).withChannel(null).build();
        MvcResult result = mockMvc(subscriptionController)
                .perform(post("/subscription")
                        .param("channel", channelString)
                        .body(TestUtils.toJson(expectedWebRequest).getBytes()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().type(HttpHeaders.APPLICATION_JSON))
                .andReturn();


        String responseString = result.getResponse().getContentAsString();
        responseString = performIVRChannelValidationAndCleanup(responseString, channelString);

        BaseResponse actualResponse = TestUtils.fromJson(responseString, BaseResponse.class);
        assertEquals(expectedResponse, actualResponse);

        final Subscription subscription = new TimedRunner<Subscription>(20, 1000) {
            @Override
            public Subscription run() {
                List<Subscription> subscriptions = allSubscriptions.findByMsisdnAndPack(msisdn, pack);
                return subscriptions.isEmpty() ? null : subscriptions.get(0);

            }
        }.executeWithTimeout();

        assertNotNull(subscription);
        markForDeletion(subscription);
        assertEquals(msisdn, subscription.getMsisdn());
        assertEquals(pack, subscription.getPack());
        assertFalse(StringUtils.isBlank(subscription.getSubscriptionId()));
    }

    @Test
    public void shouldCreateAndScheduleAnEarlySubscription() throws Exception {
        final String msisdn = "9876543210";
        final String channelString = Channel.CONTACT_CENTER.toString();
        final SubscriptionPack pack = SubscriptionPack.BARI_KILKARI;
        DateTime now = DateTime.now();
        DateTime edd = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), 0, 0).plusMonths(4);
        DateTime expectedStartDate = SubscriptionPack.BARI_KILKARI.getStartDate(edd);
        BaseResponse expectedResponse = BaseResponse.success("Subscription request submitted successfully");

        ReportingService mockedReportingService = Mockito.mock(ReportingService.class);
        when(mockedReportingService.getLocation("district", "block", "panchayat")).thenReturn(new LocationResponse("district", "block", "panchayat"));
        reportingService.setBehavior(mockedReportingService);

        SubscriptionWebRequest expectedWebRequest = new SubscriptionWebRequestBuilder().withDefaults().withMsisdn(msisdn).withEDD(edd.toString("dd-MM-yyyy")).withCreatedAt(now).build();
        MvcResult result = mockMvc(subscriptionController)
                .perform(post("/subscription")
                        .param("channel", channelString)
                        .body(TestUtils.toJson(expectedWebRequest).getBytes()).contentType(MediaType.APPLICATION_JSON))
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
        assertEquals(SubscriptionStatus.NEW_EARLY, subscription.getStatus());
        assertEquals(expectedStartDate.getMillis(), subscription.getStartDate().getMillis());
        assertFalse(StringUtils.isBlank(subscription.getSubscriptionId()));

        List<Date> scheduledDates = motechSchedulerService.getScheduledJobTimingsWithPrefix(SubscriptionEventKeys.EARLY_SUBSCRIPTION,
                subscription.getSubscriptionId(), DateTime.now().toDate(), DateTime.now().plusMonths(4).toDate());

        assertEquals(1, scheduledDates.size());
        assertEquals(expectedStartDate.toDate(), scheduledDates.get(0));
    }

    @Test
    public void shouldUnscheduleNewEarlyJobWhenChangeSubscriptionRequestedForANewEarlySubscription() throws Exception {
        final String msisdn = "9776655449";
        final String channelString = Channel.CONTACT_CENTER.toString();
        final SubscriptionPack pack = SubscriptionPack.BARI_KILKARI;
        DateTime now = DateTime.now();
        DateTime edd = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), 0, 0).plusMonths(4);
        DateTime expectedStartDate = SubscriptionPack.BARI_KILKARI.getStartDate(edd);

        ReportingService mockedReportingService = Mockito.mock(ReportingService.class);
        when(mockedReportingService.getLocation("district", "block", "panchayat")).thenReturn(new LocationResponse("district", "block", "panchayat"));
        reportingService.setBehavior(mockedReportingService);

        SubscriptionWebRequest expectedWebRequest = new SubscriptionWebRequestBuilder().withDefaults().withMsisdn(msisdn).withEDD(edd.toString("dd-MM-yyyy")).withCreatedAt(now).build();
        MvcResult result = mockMvc(subscriptionController)
                .perform(post("/subscription")
                        .param("channel", channelString)
                        .body(TestUtils.toJson(expectedWebRequest).getBytes()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().type(HttpHeaders.APPLICATION_JSON))
                .andReturn();

        Subscription subscription = allSubscriptions.findSubscriptionInProgress(msisdn, pack);
        String subscriptionId = subscription.getSubscriptionId();
        List<Date> scheduledDates = motechSchedulerService.getScheduledJobTimingsWithPrefix(SubscriptionEventKeys.EARLY_SUBSCRIPTION,
                subscription.getSubscriptionId(), DateTime.now().toDate(), DateTime.now().plusMonths(4).toDate());

        assertEquals(1, scheduledDates.size());
        assertEquals(expectedStartDate.toDate(), scheduledDates.get(0));

        ChangeSubscriptionWebRequest expectedChangeSubscriptionWebRequest = new ChangeSubscriptionWebRequestBuilder().withDefaults().withChangeType("change_schedule").withEDD(DateUtils.formatDate(DateTime.now().plusDays(1))).build();
        result = mockMvc(subscriptionController)
                .perform(put("/subscription/"+subscriptionId+"/changesubscription" )
                        .param("channel", channelString)
                        .body(TestUtils.toJson(expectedChangeSubscriptionWebRequest).getBytes()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().type(HttpHeaders.APPLICATION_JSON))
                .andReturn();

        scheduledDates = motechSchedulerService.getScheduledJobTimingsWithPrefix(SubscriptionEventKeys.EARLY_SUBSCRIPTION,
                subscription.getSubscriptionId(), DateTime.now().toDate(), DateTime.now().plusMonths(4).toDate());

        assertEquals(0, scheduledDates.size());
    }

    @Test
    public void shouldDeactivateSubscriptionForTheGivenMsisdnForTheCallCentreChannel() throws Exception {
        final String msisdn = "1111111111";
        String channelString = Channel.CONTACT_CENTER.toString();
        final SubscriptionPack pack = SubscriptionPack.BARI_KILKARI;
        BaseResponse expectedResponse = BaseResponse.success("Subscription unsubscribed successfully");

        reportingService.setBehavior(Mockito.mock(ReportingService.class));
        OnMobileSubscriptionGateway onMobileSubscriptionGateway = mock(OnMobileSubscriptionGateway.class);
        onMobileSubscriptionService.setBehavior(onMobileSubscriptionGateway);

        UnSubscriptionWebRequest expectedUnSubscriptionWebRequest = new UnSubscriptionWebRequest();
        expectedUnSubscriptionWebRequest.setReason("Reason for deactivation");

        Subscription expectedSubscription = new Subscription(msisdn, pack, DateTime.now().minusMonths(15), DateTime.now(), null);
        expectedSubscription.setStatus(SubscriptionStatus.ACTIVE);
        allSubscriptions.add(expectedSubscription);
        markForDeletion(expectedSubscription);

        final String subscriptionId = expectedSubscription.getSubscriptionId();

        MvcResult result = mockMvc(subscriptionController)
                .perform(delete("/subscription/" + subscriptionId)
                        .param("channel", channelString)
                        .body(TestUtils.toJson(expectedUnSubscriptionWebRequest).getBytes()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().type(HttpHeaders.APPLICATION_JSON))
                .andReturn();


        String responseString = result.getResponse().getContentAsString();
        responseString = performIVRChannelValidationAndCleanup(responseString, channelString);

        BaseResponse actualResponse = TestUtils.fromJson(responseString, BaseResponse.class);
        assertTrue(expectedResponse.equals(actualResponse));

        Boolean statusChanged = new TimedRunner<Boolean>(20, 1000) {
            @Override
            public Boolean run() {
                Subscription subscription = allSubscriptions.findBySubscriptionId(subscriptionId);
                return subscription.getStatus() == SubscriptionStatus.PENDING_DEACTIVATION ? Boolean.TRUE : null;

            }
        }.executeWithTimeout();

        Boolean deactivationRequested = new TimedRunner<Boolean>(20, 1000) {
            @Override
            public Boolean run() {
                return onMobileSubscriptionService.isDeactivateSubscriptionCalled() ? Boolean.TRUE : null;

            }
        }.executeWithTimeout();

        assertNotNull(statusChanged);
        assertNotNull(deactivationRequested);
    }

    @Test
    public void shouldChangeMsisdnForAnExistingSubscription() throws Exception {
        String oldMsisdn = "9876543210";
        String newMsisdn = "9876543211";
        DateTime createdAt = DateTime.now().minusWeeks(4).minusHours(1);
        Subscription oldSubscription = new Subscription(oldMsisdn, SubscriptionPack.NANHI_KILKARI, createdAt, createdAt, null);
        oldSubscription.setStatus(SubscriptionStatus.ACTIVE);

        allSubscriptions.add(oldSubscription);

        ChangeMsisdnWebRequest changeMsisdnWebRequest = new ChangeMsisdnWebRequest(oldMsisdn, newMsisdn, Arrays.asList(SubscriptionPack.NANHI_KILKARI.toString()), Channel.CONTACT_CENTER.toString());

        ReportingService mockReportingService = mock(ReportingService.class);
        reportingService.setBehavior(mockReportingService);
        when(mockReportingService.getSubscriber(oldSubscription.getSubscriptionId())).thenReturn(new SubscriberResponse("name", 25, null, null, null));

        MvcResult result = mockMvc(subscriptionController)
                .perform(post("/subscription/changemsisdn")
                        .param("channel", Channel.CONTACT_CENTER.toString())
                        .body(TestUtils.toJson(changeMsisdnWebRequest).getBytes()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().type(HttpHeaders.APPLICATION_JSON))
                .andReturn();

        Subscription newSubscription = allSubscriptions.findSubscriptionInProgress(newMsisdn, SubscriptionPack.NANHI_KILKARI);
        assertNotNull(newSubscription);

        oldSubscription = allSubscriptions.findBySubscriptionId(oldSubscription.getSubscriptionId());
        assertTrue(oldSubscription.getStatus() == SubscriptionStatus.DEACTIVATION_REQUEST_RECEIVED || oldSubscription.getStatus() == SubscriptionStatus.PENDING_DEACTIVATION);

        markForDeletion(oldSubscription, newSubscription);
    }


    private String performIVRChannelValidationAndCleanup(String jsonContent, String channel) {
        if (Channel.isIVR(channel)) {
            assertTrue(jsonContent.startsWith(SubscriptionControllerIT.IVR_RESPONSE_PREFIX));
            jsonContent = jsonContent.replace(SubscriptionControllerIT.IVR_RESPONSE_PREFIX, "");
        }
        return jsonContent;
    }
}
