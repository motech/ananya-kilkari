package org.motechproject.ananya.kilkari.web.controller;

import com.google.gson.*;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.domain.Subscription;
import org.motechproject.ananya.kilkari.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.domain.SubscriptionStatus;
import org.motechproject.ananya.kilkari.service.KilkariSubscriptionService;
import org.motechproject.ananya.kilkari.web.interceptors.KilkariChannelInterceptor;
import org.motechproject.ananya.kilkari.web.response.BaseResponse;
import org.motechproject.ananya.kilkari.web.response.SubscriberResponse;
import org.motechproject.ananya.kilkari.web.response.SubscriptionDetails;
import org.motechproject.ananya.kilkari.web.services.PublishService;
import org.springframework.test.web.server.setup.MockMvcBuilders;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

public class SubscriptionControllerTest {
    private SubscriptionController subscriptionController;


    @Mock
    private KilkariSubscriptionService kilkariSubscriptionService;

    @Mock
    private Subscription mockedSubscription;

    @Mock
    private PublishService publishService;

    @Before
    public void setUp() {
        initMocks(this);
        subscriptionController = new SubscriptionController(kilkariSubscriptionService, publishService);
    }

    @Test
    public void shouldGetSubscriptionsForGivenMsisdnForChannelIvr() throws Exception {
        String msisdn = "1234567890";
        String channel = "ivr";

        mockSubscription(msisdn);
        ArrayList<Subscription> subscriptions = new ArrayList<>();
        subscriptions.add(mockedSubscription);
        when(kilkariSubscriptionService.findByMsisdn(msisdn)).thenReturn(subscriptions);

        MockMvcBuilders.standaloneSetup(subscriptionController).addInterceptors(new KilkariChannelInterceptor()).build()
                .perform(get("/subscriber").param("msisdn", msisdn).param("channel", channel))
                .andExpect(status().isOk())
                .andExpect(content().type("application/json;charset=UTF-8"))
                .andExpect(content().string(subscriberResponseMatcherWithSubscriptions()));
    }

    @Test
    public void shouldGetSubscriptionsForGivenMsisdnForChannelOtherThanIvr() throws Exception {
        String msisdn = "1234567890";
        String channel = "not-ivr";

        mockSubscription(msisdn);
        ArrayList<Subscription> subscriptions = new ArrayList<>();
        subscriptions.add(mockedSubscription);
        when(kilkariSubscriptionService.findByMsisdn(msisdn)).thenReturn(subscriptions);

        MockMvcBuilders.standaloneSetup(subscriptionController).addInterceptors(new KilkariChannelInterceptor()).build()
                .perform(get("/subscriber").param("msisdn", msisdn).param("channel", channel))
                .andExpect(status().isOk())
                .andExpect(content().type("application/json;charset=UTF-8"))
                .andExpect(content().string(subscriberResponseMatcherWithSubscriptions()));
    }

    @Test
    public void shouldGetEmptySubscriptionResponseIfThereAreNoSubscriptionsForAGivenMsisdn() throws Exception {
        String msisdn = "1234567890";
        String channel = "not-ivr";

        when(kilkariSubscriptionService.findByMsisdn(msisdn)).thenReturn(null);

        MockMvcBuilders.standaloneSetup(subscriptionController).addInterceptors(new KilkariChannelInterceptor()).build()
                .perform(get("/subscriber").param("msisdn", msisdn).param("channel", channel))
                .andExpect(status().isOk())
                .andExpect(content().type("application/json;charset=UTF-8"))
                .andExpect(content().string(subscriberResponseMatcherWithNoSubscriptions("SUCCESS", "Subscriber details successfully fetched")));
    }

    @Test
    public void shouldReturnErrorResponseForInvalidMsisdnNumber() throws Exception {
        String msisdn = "12345";
        String channel = "ivr";

        mockSubscription(msisdn);
        ArrayList<Subscription> subscriptions = new ArrayList<>();
        subscriptions.add(mockedSubscription);
        when(kilkariSubscriptionService.findByMsisdn(msisdn)).thenReturn(subscriptions);

        MockMvcBuilders.standaloneSetup(subscriptionController).addInterceptors(new KilkariChannelInterceptor()).build()
                .perform(get("/subscriber").param("msisdn", msisdn).param("channel", channel))
                .andExpect(status().isOk())
                .andExpect(content().type("application/json;charset=UTF-8"))
                .andExpect(content().string(subscriberResponseMatcherWithNoSubscriptions("ERROR", "Invalid Msisdn")));
    }

    @Test
    public void shouldReturnErrorResponseForNonNumericMsisdnNumber() throws Exception {
        String msisdn = "123456789a";
        String channel = "ivr";

        mockSubscription(msisdn);
        ArrayList<Subscription> subscriptions = new ArrayList<>();
        subscriptions.add(mockedSubscription);
        when(kilkariSubscriptionService.findByMsisdn(msisdn)).thenReturn(subscriptions);

        MockMvcBuilders.standaloneSetup(subscriptionController).addInterceptors(new KilkariChannelInterceptor()).build()
                .perform(get("/subscriber").param("msisdn", msisdn).param("channel", channel))
                .andExpect(status().isOk())
                .andExpect(content().type("application/json;charset=UTF-8"))
                .andExpect(content().string(subscriberResponseMatcherWithNoSubscriptions("ERROR", "Invalid Msisdn")));
    }

    @Test
    public void shouldCreateNewSubscriptionEvent() throws Exception {
        String msisdn = "1234567890";
        String channel = "ivr";
        String pack = "twelve-months";

        MockMvcBuilders.standaloneSetup(subscriptionController).addInterceptors(new KilkariChannelInterceptor()).build()
                .perform(get("/subscription").param("msisdn", msisdn).param("channel", channel).param("pack", pack))
                .andExpect(status().isOk())
                .andExpect(content().type("application/json;charset=UTF-8"))
                .andExpect(content().string(baseResponseMatcher("SUCCESS", "Subscription request submitted successfully")));

        verify(publishService).createSubscription(msisdn, pack);
    }

    private void mockSubscription(String msisdn) {
        when(mockedSubscription.getMsisdn()).thenReturn(msisdn);
        when(mockedSubscription.getPack()).thenReturn(SubscriptionPack.FIFTEEN_MONTHS);
        when(mockedSubscription.getStatus()).thenReturn(SubscriptionStatus.NEW);
        when(mockedSubscription.getSubscriptionId()).thenReturn("subscription-id");
    }

    private BaseMatcher<String> baseResponseMatcher(final String status, final String description) {
        return new BaseMatcher<String>() {
            @Override
            public boolean matches(Object o) {
                return assertBaseResponse((String) o, status, description);
            }

            @Override
            public void describeTo(Description matcherDescription) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        };
    }

    private BaseMatcher<String> subscriberResponseMatcherWithNoSubscriptions(final String status, final String description) {
        return new BaseMatcher<String>() {
            @Override
            public boolean matches(Object o) {
                return assertSubscriberResponseWithNoSubscriptions((String) o, status, description);
            }

            @Override
            public void describeTo(Description matcherDescription) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        };
    }

    private BaseMatcher<String> subscriberResponseMatcherWithSubscriptions() {
        return new BaseMatcher<String>() {
            @Override
            public boolean matches(Object o) {
                return assertSubscriberResponse((String) o);
            }

            @Override
            public void describeTo(Description description) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        };
    }

    private boolean assertBaseResponse(String jsonContent, String status, String description) {
        BaseResponse baseResponse = fromJson(jsonContent.replace("var response = ", ""), BaseResponse.class);

        return baseResponse.getStatus().equals(status)
                && baseResponse.getDescription().equals(description);
    }

    private boolean assertSubscriberResponse(String jsonContent) {
        SubscriberResponse subscriberResponse = fromJson(jsonContent.replace("var response = ", ""), SubscriberResponse.class);
        SubscriptionDetails subscriptionDetails = subscriberResponse.getSubscriptionDetails().get(0);

        return subscriberResponse.getStatus().equals("SUCCESS")
                && subscriberResponse.getDescription().equals("Subscriber details successfully fetched")
                && subscriptionDetails.getPack().equals(mockedSubscription.getPack().name())
                && subscriptionDetails.getStatus().equals(mockedSubscription.getStatus().name())
                && subscriptionDetails.getSubscriptionId().equals(mockedSubscription.getSubscriptionId());
    }

    private boolean assertSubscriberResponseWithNoSubscriptions(String jsonContent, String status, String description) {
        SubscriberResponse subscriberResponse = fromJson(jsonContent.replace("var response = ", ""), SubscriberResponse.class);

        return subscriberResponse.getStatus().equals(status)
                && subscriberResponse.getDescription().equals(description)
                && subscriberResponse.getSubscriptionDetails().size() == 0;
    }

    private <T> T fromJson(String jsonString, Class<T> subscriberResponseClass) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, subscriberResponseClass);
    }
}
