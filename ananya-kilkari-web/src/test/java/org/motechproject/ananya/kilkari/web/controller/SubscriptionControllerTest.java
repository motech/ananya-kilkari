package org.motechproject.ananya.kilkari.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.domain.Subscription;
import org.motechproject.ananya.kilkari.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.domain.SubscriptionStatus;
import org.motechproject.ananya.kilkari.service.KilkariSubscriptionService;
import org.motechproject.ananya.kilkari.web.interceptors.KilkariChannelInterceptor;
import org.springframework.test.web.server.setup.MockMvcBuilders;

import java.util.ArrayList;

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

    @Before
    public void setUp() {
        initMocks(this);
        subscriptionController = new SubscriptionController(kilkariSubscriptionService);
    }

    @Test
    public void shouldGetSubscriptionsForGivenMsisdnForChannelIvr() throws Exception {
        String msisdn = "1234";
        String channel = "ivr";

        mockSubscription(msisdn);
        ArrayList<Subscription> subscriptions = new ArrayList<>();
        subscriptions.add(mockedSubscription);
        when(kilkariSubscriptionService.findByMsisdn(msisdn)).thenReturn(subscriptions);

        MockMvcBuilders.standaloneSetup(subscriptionController).addInterceptors(new KilkariChannelInterceptor()).build()
                .perform(get("/subscription").param("msisdn", msisdn).param("channel", channel))
                .andExpect(status().isOk())
                .andExpect(content().type("application/json;charset=UTF-8"))
                .andExpect(content().string("var response = {\"subscriptionDetails\":[{\"pack\":\"FIFTEEN_MONTHS\",\"status\":\"NEW\",\"subscriptionId\":\"subscription-id\"}]}"));
    }

    @Test
    public void shouldGetSubscriptionsForGivenMsisdnForChannelOtherThanIvr() throws Exception {
        String msisdn = "1234";
        String channel = "not-ivr";

        mockSubscription(msisdn);
        ArrayList<Subscription> subscriptions = new ArrayList<>();
        subscriptions.add(mockedSubscription);
        when(kilkariSubscriptionService.findByMsisdn(msisdn)).thenReturn(subscriptions);

        MockMvcBuilders.standaloneSetup(subscriptionController).addInterceptors(new KilkariChannelInterceptor()).build()
                .perform(get("/subscription").param("msisdn", msisdn).param("channel", channel))
                .andExpect(status().isOk())
                .andExpect(content().type("application/json;charset=UTF-8"))
                .andExpect(content().string("{\"subscriptionDetails\":[{\"pack\":\"FIFTEEN_MONTHS\",\"status\":\"NEW\",\"subscriptionId\":\"subscription-id\"}]}"));
    }

    @Test
    public void shouldGetEmptySubscriptionResponseIfThereAreNoSubscriptionsForAGivenMsisdn() throws Exception {
        String msisdn = "1234";
        String channel = "not-ivr";

        when(kilkariSubscriptionService.findByMsisdn(msisdn)).thenReturn(null);

        MockMvcBuilders.standaloneSetup(subscriptionController).addInterceptors(new KilkariChannelInterceptor()).build()
                .perform(get("/subscription").param("msisdn", msisdn).param("channel", channel))
                .andExpect(status().isOk())
                .andExpect(content().type("application/json;charset=UTF-8"))
                .andExpect(content().string("{\"subscriptionDetails\":[]}"));
    }

    private void mockSubscription(String msisdn) {
        when(mockedSubscription.getMsisdn()).thenReturn(msisdn);
        when(mockedSubscription.getPack()).thenReturn(SubscriptionPack.FIFTEEN_MONTHS);
        when(mockedSubscription.getStatus()).thenReturn(SubscriptionStatus.NEW);
        when(mockedSubscription.getSubscriptionId()).thenReturn("subscription-id");
    }
}
