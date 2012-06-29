package org.motechproject.ananya.kilkari.web.IT;

import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.motechproject.ananya.kilkari.domain.Channel;
import org.motechproject.ananya.kilkari.domain.Subscription;
import org.motechproject.ananya.kilkari.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.repository.AllSubscriptions;
import org.motechproject.ananya.kilkari.web.SpringIntegrationTest;
import org.motechproject.ananya.kilkari.web.controller.SubscriptionController;
import org.motechproject.ananya.kilkari.web.interceptors.KilkariChannelInterceptor;
import org.motechproject.ananya.kilkari.web.mapper.SubscriptionDetailsMapper;
import org.motechproject.ananya.kilkari.web.response.BaseResponse;
import org.motechproject.ananya.kilkari.web.response.SubscriberResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.server.MvcResult;
import org.springframework.test.web.server.setup.MockMvcBuilders;

import static org.junit.Assert.*;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

public class SubscriptionControllerIT extends SpringIntegrationTest {

    @Autowired
    private SubscriptionController subscriptionController;

    @Autowired
    private AllSubscriptions allSubscriptions;

    @Test
    public void shouldRetrieveSubscriptionDetailsFromDatabase() throws Exception {
        String msisdn = "9876543210";
        Subscription subscription1 = new Subscription(msisdn, SubscriptionPack.TWELVE_MONTHS);
        Subscription subscription2 = new Subscription(msisdn, SubscriptionPack.FIFTEEN_MONTHS);
        allSubscriptions.add(subscription1);
        allSubscriptions.add(subscription2);
        markForDeletion(subscription1);
        markForDeletion(subscription2);

        SubscriberResponse subscriberResponse = new SubscriberResponse();
        subscriberResponse.addSubscriptionDetail(SubscriptionDetailsMapper.mapFrom(subscription1));
        subscriberResponse.addSubscriptionDetail(SubscriptionDetailsMapper.mapFrom(subscription2));

        MvcResult result = MockMvcBuilders.standaloneSetup(subscriptionController)
                .addInterceptors(new KilkariChannelInterceptor()).build()
                    .perform(get("/subscriber").param("msisdn", msisdn).param("channel", Channel.IVR.toString()))
                    .andExpect(status().isOk())
                    .andExpect(content().type("application/json;charset=UTF-8"))
                    .andReturn();

        SubscriberResponse actualResponse = fromJson(result.getResponse().getContentAsString(), SubscriberResponse.class);

        assertEquals(subscriberResponse, actualResponse);
    }

    @Test
    public void shouldCreateSubscriptionForTheGivenMsisdn() throws Exception {

        final String msisdn = "9876543210";
        final SubscriptionPack pack = SubscriptionPack.TWELVE_MONTHS;
        BaseResponse expectedResponse = new BaseResponse("SUCCESS", "Subscription request submitted successfully");

        MvcResult result = MockMvcBuilders.standaloneSetup(subscriptionController)
                .addInterceptors(new KilkariChannelInterceptor()).build()
                .perform(get("/subscription").param("msisdn", msisdn).param("pack", pack.toString())
                        .param("channel", Channel.IVR.toString()))
                .andExpect(status().isOk())
                .andExpect(content().type("application/json;charset=UTF-8"))
                .andReturn();

        BaseResponse actualResponse =  (BaseResponse) new BaseResponse().fromJson(result.getResponse().getContentAsString());
        assertEquals(expectedResponse, actualResponse);

        final Subscription[] subscription = new Subscription[1];

        new TimedRunner() {
            @Override
            boolean run() {
                subscription[0] = allSubscriptions.findByMsisdnAndPack(msisdn, pack);
                return (subscription[0] == null);
            }
        }.executeWithTimeout();

        assertNotNull(subscription[0]);
        markForDeletion(subscription[0]);
        assertEquals(msisdn, subscription[0].getMsisdn());
        assertEquals(pack, subscription[0].getPack());
        assertFalse(StringUtils.isBlank(subscription[0].getSubscriptionId()));
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
