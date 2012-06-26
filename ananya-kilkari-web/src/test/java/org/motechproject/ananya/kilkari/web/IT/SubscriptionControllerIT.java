package org.motechproject.ananya.kilkari.web.IT;

import org.junit.Test;
import org.motechproject.ananya.kilkari.domain.Channel;
import org.motechproject.ananya.kilkari.domain.Subscription;
import org.motechproject.ananya.kilkari.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.repository.AllSubscriptions;
import org.motechproject.ananya.kilkari.web.SpringIntegrationTest;
import org.motechproject.ananya.kilkari.web.controller.SubscriptionController;
import org.motechproject.ananya.kilkari.web.interceptors.KilkariChannelInterceptor;
import org.motechproject.ananya.kilkari.web.mapper.SubscriptionDetailsMapper;
import org.motechproject.ananya.kilkari.web.response.SubscriberResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.server.MvcResult;
import org.springframework.test.web.server.setup.MockMvcBuilders;

import static org.junit.Assert.assertEquals;
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

        SubscriberResponse actualResponse = (SubscriberResponse) new SubscriberResponse().fromJson(result.getResponse().getContentAsString());

        assertEquals(subscriberResponse, actualResponse);
    }

}
