package org.motechproject.ananya.kilkari.web.controller;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.builder.SubscriptionWebRequestBuilder;
import org.motechproject.ananya.kilkari.domain.CallbackAction;
import org.motechproject.ananya.kilkari.domain.CallbackStatus;
import org.motechproject.ananya.kilkari.request.*;
import org.motechproject.ananya.kilkari.service.KilkariSubscriptionService;
import org.motechproject.ananya.kilkari.subscription.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.domain.Operator;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionStatus;
import org.motechproject.ananya.kilkari.subscription.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.subscription.service.SubscriptionService;
import org.motechproject.ananya.kilkari.subscription.service.response.SubscriptionResponse;
import org.motechproject.ananya.kilkari.subscription.validators.Errors;
import org.motechproject.ananya.kilkari.web.HttpConstants;
import org.motechproject.ananya.kilkari.web.HttpHeaders;
import org.motechproject.ananya.kilkari.web.TestUtils;
import org.motechproject.ananya.kilkari.web.mapper.SubscriptionDetailsMapper;
import org.motechproject.ananya.kilkari.web.response.BaseResponse;
import org.motechproject.ananya.kilkari.web.response.SubscriptionDetails;
import org.motechproject.ananya.kilkari.web.response.SubscriptionWebResponse;
import org.motechproject.ananya.kilkari.web.validators.CallbackRequestValidator;
import org.motechproject.ananya.kilkari.web.validators.CampaignChangeRequestValidator;
import org.motechproject.ananya.kilkari.web.validators.UnsubscriptionRequestValidator;
import org.springframework.http.MediaType;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ananya.kilkari.web.MVCTestUtils.mockMvc;
import static org.motechproject.ananya.kilkari.web.controller.ResponseMatchers.baseResponseMatcher;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;


public class SubscriptionControllerTest {
    private SubscriptionController subscriptionController;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private SubscriptionResponse mockedSubscriptionResponse;
    @Mock
    private KilkariSubscriptionService kilkariSubscriptionService;
    @Mock
    private SubscriptionService subscriptionService;
    @Mock
    private CallbackRequestValidator callbackRequestValidator;
    @Mock
    private UnsubscriptionRequestValidator unsubscriptionRequestValidator;
    @Mock
    private SubscriptionDetailsMapper mockedSubscriptionDetailsMapper;
    @Mock
    private CampaignChangeRequestValidator campaignChangeRequestValidator;

    private static final String IVR_RESPONSE_PREFIX = "var response = ";

    @Before
    public void setUp() {
        initMocks(this);
        subscriptionController = new SubscriptionController(kilkariSubscriptionService, callbackRequestValidator, unsubscriptionRequestValidator, mockedSubscriptionDetailsMapper, campaignChangeRequestValidator);
    }

    @Test
    public void shouldGetSubscriptionsForGivenMsisdnForChannelIvr() throws Exception {
        String msisdn = "1234567890";
        String channel = "ivr";

        mockSubscription(msisdn);
        ArrayList<SubscriptionResponse> subscriptionResponses = new ArrayList<>();
        subscriptionResponses.add(mockedSubscriptionResponse);
        when(kilkariSubscriptionService.findByMsisdn(msisdn)).thenReturn(subscriptionResponses);

        mockMvc(subscriptionController)
                .perform(get("/subscriber").param("msisdn", msisdn).param("channel", channel))
                .andExpect(status().isOk())
                .andExpect(content().type(HttpHeaders.APPLICATION_JAVASCRIPT))
                .andExpect(content().string(subscriberResponseMatcherWithSubscriptions(channel)));
    }

    @Test
    public void shouldGetSubscriptionsForGivenMsisdnForChannelOtherThanIvr() throws Exception {
        String msisdn = "1234567890";
        String channel = "call_center";

        mockSubscription(msisdn);
        ArrayList<SubscriptionResponse> subscriptionResponses = new ArrayList<>();
        subscriptionResponses.add(mockedSubscriptionResponse);
        when(kilkariSubscriptionService.findByMsisdn(msisdn)).thenReturn(subscriptionResponses);

        mockMvc(subscriptionController)
                .perform(get("/subscriber").param("msisdn", msisdn).param("channel", channel))
                .andExpect(status().isOk())
                .andExpect(content().type(HttpHeaders.APPLICATION_JSON))
                .andExpect(content().string(subscriberResponseMatcherWithSubscriptions(channel)));
    }

    @Test
    public void shouldGetEmptySubscriptionResponseIfThereAreNoSubscriptionsForAGivenMsisdnForIvr() throws Exception {
        String msisdn = "1234567890";
        String channel = "ivr";

        when(kilkariSubscriptionService.findByMsisdn(msisdn)).thenReturn(null);

        mockMvc(subscriptionController)
                .perform(get("/subscriber").param("msisdn", msisdn).param("channel", channel))
                .andExpect(status().isOk())
                .andExpect(content().type(HttpConstants.IVR.getResponseContentType()))
                .andExpect(content().string(subscriberResponseMatcherWithNoSubscriptions(channel)));
    }

    @Test
    public void shouldGetEmptySubscriptionResponseIfThereAreNoSubscriptionsForAGivenMsisdnOtherThanIvr() throws Exception {
        String msisdn = "1234567890";
        String channel = "call_center";

        when(kilkariSubscriptionService.findByMsisdn(msisdn)).thenReturn(null);

        mockMvc(subscriptionController)
                .perform(get("/subscriber").param("msisdn", msisdn).param("channel", channel))
                .andExpect(status().isOk())
                .andExpect(content().type(HttpHeaders.APPLICATION_JSON))
                .andExpect(content().string(subscriberResponseMatcherWithNoSubscriptions(channel)));
    }

    @Test
    public void shouldReturnErrorResponseForInvalidMsisdnNumberForIvr() throws Exception {
        String msisdn = "12345";
        String channel = "ivr";

        when(kilkariSubscriptionService.findByMsisdn(msisdn)).thenThrow(new ValidationException("Invalid Msisdn"));

        mockMvc(subscriptionController)
                .perform(get("/subscriber").param("msisdn", msisdn).param("channel", channel))
                .andExpect(status().isOk())
                .andExpect(content().type(HttpHeaders.APPLICATION_JAVASCRIPT))
                .andExpect(content().string(errorResponseMatcherForInvalidMsisdn(channel)));

    }

    @Test
    public void shouldReturnErrorResponseForInvalidMsisdnNumberOtherThanIvr() throws Exception {
        String msisdn = "12345";
        String channel = "call_center";

        when(kilkariSubscriptionService.findByMsisdn(msisdn)).thenThrow(new ValidationException("Invalid Msisdn"));

        mockMvc(subscriptionController)
                .perform(get("/subscriber").param("msisdn", msisdn).param("channel", channel))
                .andExpect(status().is(400))
                .andExpect(content().type(HttpHeaders.APPLICATION_JSON))
                .andExpect(content().string(errorResponseMatcherForInvalidMsisdn(channel)));
    }

    @Test
    public void shouldReturnCorrectErrorResponseForRuntimeExceptionForIvr() throws Exception {
        String msisdn = "1234567890";
        String channel = "ivr";

        when(kilkariSubscriptionService.findByMsisdn(msisdn)).thenThrow(new RuntimeException("runtime exception"));

        mockMvc(subscriptionController)
                .perform(get("/subscriber").param("msisdn", msisdn).param("channel", channel))
                .andExpect(status().isOk())
                .andExpect(content().type(HttpHeaders.APPLICATION_JAVASCRIPT))
                .andExpect(content().string(errorResponseMatcherForRuntimeException(channel)));
    }

    @Test
    public void shouldReturnCorrectErrorResponseForRuntimeExceptionForOtherThanIvr() throws Exception {
        String msisdn = "1234567890";
        String channel = "call_center";

        when(kilkariSubscriptionService.findByMsisdn(msisdn)).thenThrow(new RuntimeException("runtime exception"));

        mockMvc(subscriptionController)
                .perform(get("/subscriber").param("msisdn", msisdn).param("channel", channel))
                .andExpect(status().is(500))
                .andExpect(content().type(HttpHeaders.APPLICATION_JSON))
                .andExpect(content().string(errorResponseMatcherForRuntimeException(channel)));
    }

    @Test
    public void shouldCreateNewSubscriptionEvent() throws Exception {
        String msisdn = "1234567890";
        String channel = "ivr";
        String pack = "twelve-months";
        DateTime beforeCreate = DateTime.now();

        mockMvc(subscriptionController)
                .perform(get("/subscription").param("msisdn", msisdn).param("channel", channel).param("pack", pack))
                .andExpect(status().isOk())
                .andExpect(content().type(HttpHeaders.APPLICATION_JAVASCRIPT))
                .andExpect(content().string(baseResponseMatcher("SUCCESS", "Subscription request submitted successfully")));

        ArgumentCaptor<SubscriptionWebRequest> subscriptionRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionWebRequest.class);
        verify(kilkariSubscriptionService).createSubscriptionAsync(subscriptionRequestArgumentCaptor.capture());
        SubscriptionWebRequest subscriptionWebRequest = subscriptionRequestArgumentCaptor.getValue();

        assertEquals(msisdn, subscriptionWebRequest.getMsisdn());
        assertEquals(pack, subscriptionWebRequest.getPack());
        assertEquals(channel, subscriptionWebRequest.getChannel());

        assertCreatedAt(beforeCreate, subscriptionWebRequest);
    }

    @Test
    public void shouldCreateNewSubscriptionEventForCC() throws Exception {
        DateTime createdAt = DateTime.now();

        SubscriptionWebRequest expectedWebRequest = new SubscriptionWebRequestBuilder().withDefaults().withCreatedAt(createdAt).build();

        mockMvc(subscriptionController)
                .perform(post("/subscription").body(TestUtils.toJson(expectedWebRequest).getBytes()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().type(HttpHeaders.APPLICATION_JSON))
                .andExpect(content().string(baseResponseMatcher("SUCCESS", "Subscription request submitted successfully")));

        ArgumentCaptor<SubscriptionWebRequest> subscriptionRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionWebRequest.class);
        verify(kilkariSubscriptionService).createSubscription(subscriptionRequestArgumentCaptor.capture());
        SubscriptionWebRequest subscriptionWebRequest = subscriptionRequestArgumentCaptor.getValue();

        assertTrue(expectedWebRequest.equals(subscriptionWebRequest));
        assertCreatedAt(createdAt, subscriptionWebRequest);
    }

    @Test
    public void shouldGiveAnErrorMessageWhenCallBackRequestIsInvalid() throws Exception {
        ArrayList<String> errorsMessages = new ArrayList<String>() {
            {
                add("Invalid msisdn invalidMsisdn");
                add("Invalid operator invalidOperator");
            }
        };

        byte[] requestBody = TestUtils.toJson(new CallbackRequest()).getBytes();

        Errors errors = new Errors();
        errors.addAll(errorsMessages);
        when(callbackRequestValidator.validate(any(CallbackRequestWrapper.class))).thenReturn(errors);

        mockMvc(subscriptionController)
                .perform(put("/subscription/abcd1234")
                        .body(requestBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().type(HttpHeaders.APPLICATION_JSON))
                .andExpect(content().string(baseResponseMatcher("ERROR", "Invalid msisdn invalidMsisdn,Invalid operator invalidOperator")));


        verifyZeroInteractions(kilkariSubscriptionService);
    }

    @Test
    public void shouldPublishTheCallbackRequestIfValidationSucceeds() throws Exception {
        String subscriptionId = "abcd1234";
        CallbackRequest callbackRequest = new CallbackRequest();
        callbackRequest.setMsisdn("1234567890");
        callbackRequest.setAction(CallbackAction.ACT.name());
        callbackRequest.setStatus(CallbackStatus.SUCCESS.name());
        callbackRequest.setReason("reason");
        callbackRequest.setOperator(Operator.AIRTEL.name());
        callbackRequest.setGraceCount("2");
        byte[] requestBody = TestUtils.toJson(callbackRequest).getBytes();
        when(callbackRequestValidator.validate(any(CallbackRequestWrapper.class))).thenReturn(new Errors());

        mockMvc(subscriptionController)
                .perform(put("/subscription/" + subscriptionId)
                        .body(requestBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().type(HttpHeaders.APPLICATION_JSON))
                .andExpect(content().string(baseResponseMatcher("SUCCESS", "Callback request processed successfully")));

        ArgumentCaptor<CallbackRequestWrapper> callbackRequestWrapperArgumentCaptor = ArgumentCaptor.forClass(CallbackRequestWrapper.class);
        verify(kilkariSubscriptionService).processCallbackRequest(callbackRequestWrapperArgumentCaptor.capture());
        CallbackRequestWrapper callbackRequestWrapper = callbackRequestWrapperArgumentCaptor.getValue();

        assertEquals(subscriptionId, callbackRequestWrapper.getSubscriptionId());
        assertEquals(CallbackAction.ACT.name(), callbackRequestWrapper.getAction());
        assertEquals(CallbackStatus.SUCCESS.name(), callbackRequestWrapper.getStatus());
        assertNotNull(callbackRequestWrapper.getCreatedAt());
    }

    @Test
    public void shouldValidateChannelOnSubscriptionCreationRequest_FromCC() {
        SubscriptionWebRequest subscriptionWebRequest = mock(SubscriptionWebRequest.class);
        when(subscriptionWebRequest.getChannel()).thenReturn("call_center");

        subscriptionController.createSubscription(subscriptionWebRequest);

        verify(subscriptionWebRequest).validateChannel();
    }

    @Test
    public void shouldValidateChannelOnSubscriptionCreationRequest_FromIVR() {
        SubscriptionWebRequest subscriptionWebRequest = mock(SubscriptionWebRequest.class);
        when(subscriptionWebRequest.getChannel()).thenReturn("ivr");

        subscriptionController.createSubscriptionForIVR(subscriptionWebRequest);

        verify(subscriptionWebRequest).validateChannel();
    }

    @Test
    public void shouldValidateForChannel() {
        expectedException.expect(ValidationException.class);
        expectedException.expectMessage("Invalid channel xyz");

        SubscriptionWebRequest subscriptionWebRequest = new SubscriptionWebRequestBuilder().withDefaults().withChannel("xyz").build();

        subscriptionController.createSubscription(subscriptionWebRequest);
    }

    @Test
    public void shouldUnsubscribeAUserGivenValidDetails() throws Exception {
        String subscriptionId = "abcd1234";
        UnsubscriptionRequest unsubscriptionRequest = new UnsubscriptionRequest();
        unsubscriptionRequest.setReason("reason");
        unsubscriptionRequest.setChannel(Channel.CALL_CENTER.name());
        byte[] requestBody = TestUtils.toJson(unsubscriptionRequest).getBytes();

        when(unsubscriptionRequestValidator.validate(subscriptionId)).thenReturn(new Errors());

        mockMvc(subscriptionController)
                .perform(delete("/subscription/" + subscriptionId)
                        .body(requestBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().type(HttpHeaders.APPLICATION_JSON))
                .andExpect(content().string(baseResponseMatcher("SUCCESS", "Subscription unsubscribed successfully")));

        ArgumentCaptor<UnsubscriptionRequest> unsubscriptionRequestArgumentCaptor = ArgumentCaptor.forClass(UnsubscriptionRequest.class);
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(kilkariSubscriptionService).requestDeactivation(stringArgumentCaptor.capture(), unsubscriptionRequestArgumentCaptor.capture());

        UnsubscriptionRequest actualUnsubscriptionRequest = unsubscriptionRequestArgumentCaptor.getValue();
        String actualSubscriptionId = stringArgumentCaptor.getValue();

        assertEquals(subscriptionId, actualSubscriptionId);
        assertEquals(Channel.CALL_CENTER.name(), actualUnsubscriptionRequest.getChannel());
    }

    @Test
    public void shouldValidateUnsubscriptionRequestDetails() throws Exception {
        String subscriptionId = "abcd1234";
        UnsubscriptionRequest unsubscriptionRequest = new UnsubscriptionRequest();
        unsubscriptionRequest.setReason("reason");
        byte[] requestBody = TestUtils.toJson(unsubscriptionRequest).getBytes();

        Errors errors = new Errors();
        errors.add("some error description1");
        errors.add("some error description2");
        when(unsubscriptionRequestValidator.validate(anyString())).thenReturn(errors);

        mockMvc(subscriptionController)
                .perform(delete("/subscription/" + subscriptionId)
                        .body(requestBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().type(HttpHeaders.APPLICATION_JSON))
                .andExpect(content().string(baseResponseMatcher("ERROR", "some error description1,some error description2")));
    }

    @Test
    public void shouldProcessValidCampaignChangeRequest() throws Exception {
        CampaignChangeRequest campaignChangeRequest = new CampaignChangeRequest();
        String subscriptionId = "subscriptionId";
        String reason = "INFANT_DEATH";
        campaignChangeRequest.setSubscriptionId(subscriptionId);
        campaignChangeRequest.setReason(reason);
        byte[] requestBody = TestUtils.toJson(campaignChangeRequest).getBytes();

        when(campaignChangeRequestValidator.validate(any(CampaignChangeRequest.class))).thenReturn(new Errors());

        mockMvc(subscriptionController)
                .perform(post("/subscription/changecampaign")
                        .body(requestBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().type(HttpHeaders.APPLICATION_JSON))
                .andExpect(content().string(baseResponseMatcher("SUCCESS", "Campaign Change request submitted successfully")));

        ArgumentCaptor<CampaignChangeRequest> campaignChangeRequestArgumentCaptor = ArgumentCaptor.forClass(CampaignChangeRequest.class);
        verify(kilkariSubscriptionService).processCampaignChange(campaignChangeRequestArgumentCaptor.capture());
        CampaignChangeRequest changeRequest = campaignChangeRequestArgumentCaptor.getValue();

        assertEquals(subscriptionId, changeRequest.getSubscriptionId());
        assertEquals(reason, changeRequest.getReason());
    }

    @Test
    public void shouldValidateCampaignChangeRequest() throws Exception {
        CampaignChangeRequest campaignChangeRequest = new CampaignChangeRequest();
        campaignChangeRequest.setSubscriptionId("subscriptionId");
        campaignChangeRequest.setReason("reason");
        byte[] requestBody = TestUtils.toJson(campaignChangeRequest).getBytes();

        Errors errors = new Errors();
        errors.add("some error description1");
        errors.add("some error description2");
        when(campaignChangeRequestValidator.validate(any(CampaignChangeRequest.class))).thenReturn(errors);

        mockMvc(subscriptionController)
                .perform(post("/subscription/changecampaign")
                        .body(requestBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().type(HttpHeaders.APPLICATION_JSON))
                .andExpect(content().string(baseResponseMatcher("ERROR", "some error description1,some error description2")));
    }

    @Test
    public void shouldUpdateSubscriberDetails() throws Exception {
        SubscriberWebRequest subscriberWebRequest = new SubscriberWebRequest();
        byte[] requestBody = TestUtils.toJson(subscriberWebRequest).getBytes();
        String subscriptionId = "subscription-id";

        mockMvc(subscriptionController)
                .perform(put("/subscriber/"+subscriptionId)
                        .body(requestBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().type(HttpHeaders.APPLICATION_JSON))
                .andExpect(content().string(baseResponseMatcher("SUCCESS", "Subscriber Update request submitted successfully")));

        verify(kilkariSubscriptionService).updateSubscriberDetails(subscriberWebRequest, subscriptionId);
    }

    private void mockSubscription(String msisdn) {
        String subscriptionId = "subscription-id";
        SubscriptionPack subscriptionPack = SubscriptionPack.FIFTEEN_MONTHS;
        SubscriptionStatus subscriptionStatus = SubscriptionStatus.NEW;

        when(mockedSubscriptionResponse.getMsisdn()).thenReturn(msisdn);
        when(mockedSubscriptionResponse.getPack()).thenReturn(subscriptionPack);
        when(mockedSubscriptionResponse.getStatus()).thenReturn(subscriptionStatus);
        when(mockedSubscriptionResponse.getSubscriptionId()).thenReturn(subscriptionId);

        when(mockedSubscriptionDetailsMapper.mapFrom(mockedSubscriptionResponse)).thenReturn(new SubscriptionDetails(subscriptionId, subscriptionPack.name(), subscriptionStatus.name(), null));
    }

    private void assertCreatedAt(DateTime beforeCreate, SubscriptionWebRequest subscriptionWebRequest) {
        DateTime createdAt = subscriptionWebRequest.getCreatedAt();
        DateTime afterCreate = DateTime.now();
        assertTrue(createdAt.isEqual(beforeCreate) || createdAt.isAfter(beforeCreate));
        assertTrue(createdAt.isEqual(afterCreate) || createdAt.isBefore(afterCreate));
    }


    private Matcher<String> errorResponseMatcherForInvalidMsisdn(final String channel) {
        return new BaseMatcher<String>() {
            @Override
            public boolean matches(Object o) {
                return assertErrorResponseForInvalidMsisdn((String) o, channel);
            }

            @Override
            public void describeTo(Description matcherDescription) {
            }
        };
    }

    private Matcher<String> errorResponseMatcherForRuntimeException(final String channel) {
        return new BaseMatcher<String>() {
            @Override
            public boolean matches(Object o) {
                return assertErrorResponseForRuntimeException((String) o, channel);
            }

            @Override
            public void describeTo(Description matcherDescription) {
            }
        };
    }

    private BaseMatcher<String> subscriberResponseMatcherWithNoSubscriptions(final String channel) {
        return new BaseMatcher<String>() {
            @Override
            public boolean matches(Object o) {
                return assertSubscriberResponseWithNoSubscriptions((String) o, channel);
            }

            @Override
            public void describeTo(Description matcherDescription) {
            }
        };
    }

    private BaseMatcher<String> subscriberResponseMatcherWithSubscriptions(final String channel) {
        return new BaseMatcher<String>() {
            @Override
            public boolean matches(Object o) {
                return assertSubscriberResponse((String) o, channel);
            }

            @Override
            public void describeTo(Description description) {
            }
        };
    }


    private String performIVRChannelValidationAndCleanup(String jsonContent, String channel) {
        if (Channel.isIVR(channel)) {
            assertTrue(jsonContent.startsWith(IVR_RESPONSE_PREFIX));
            jsonContent = jsonContent.replace(IVR_RESPONSE_PREFIX, "");
        }
        return jsonContent;
    }

    private boolean assertErrorResponseForInvalidMsisdn(String jsonContent, String channel) {
        jsonContent = performIVRChannelValidationAndCleanup(jsonContent, channel);

        BaseResponse baseResponse = TestUtils.fromJson(jsonContent, BaseResponse.class);

        return baseResponse.isError() &&
                baseResponse.getDescription().equals("Invalid Msisdn");
    }

    private boolean assertErrorResponseForRuntimeException(String jsonContent, String channel) {
        jsonContent = performIVRChannelValidationAndCleanup(jsonContent, channel);

        BaseResponse baseResponse = TestUtils.fromJson(jsonContent, BaseResponse.class);

        return baseResponse.isError() &&
                baseResponse.getDescription().equals("runtime exception");
    }

    private boolean assertSubscriberResponse(String jsonContent, String channel) {
        jsonContent = performIVRChannelValidationAndCleanup(jsonContent, channel);

        SubscriptionWebResponse subscriptionWebResponse = TestUtils.fromJson(jsonContent, SubscriptionWebResponse.class);
        SubscriptionDetails subscriptionDetails = subscriptionWebResponse.getSubscriptionDetails().get(0);

        return subscriptionDetails.getPack().equals(mockedSubscriptionResponse.getPack().name())
                && subscriptionDetails.getStatus().equals(mockedSubscriptionResponse.getStatus().name())
                && subscriptionDetails.getSubscriptionId().equals(mockedSubscriptionResponse.getSubscriptionId());
    }

    private boolean assertSubscriberResponseWithNoSubscriptions(String jsonContent, String channel) {
        jsonContent = performIVRChannelValidationAndCleanup(jsonContent, channel);

        SubscriptionWebResponse subscriptionWebResponse = TestUtils.fromJson(jsonContent, SubscriptionWebResponse.class);

        return subscriptionWebResponse.getSubscriptionDetails().size() == 0;
    }
}
