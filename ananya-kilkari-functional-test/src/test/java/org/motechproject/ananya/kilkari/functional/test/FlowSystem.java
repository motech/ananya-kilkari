package org.motechproject.ananya.kilkari.functional.test;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.functional.test.domain.SubscriptionData;
import org.motechproject.ananya.kilkari.functional.test.utils.CallBackRequestBuilder;
import org.motechproject.ananya.kilkari.functional.test.utils.DocumentVerifier;
import org.motechproject.ananya.kilkari.functional.test.utils.FakeTimeUtils;
import org.motechproject.ananya.kilkari.obd.repository.AllCampaignMessages;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionStatus;
import org.motechproject.ananya.kilkari.subscription.repository.AllSubscriptions;
import org.motechproject.ananya.kilkari.web.controller.SubscriptionController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import static org.motechproject.ananya.kilkari.functional.test.utils.MVCTestUtils.mockMvc;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.put;

@Component
public class FlowSystem {

    @Autowired
    private SubscriptionController subscriptionController;
    @Autowired
    private AllSubscriptions allSubscriptions;
    @Autowired
    private AllCampaignMessages allCampaignMessages;
    @Autowired
    private DocumentVerifier documentVerifier;



    public FlowSystem subscribe(final SubscriptionData subscriptionData) throws Exception {
        mockMvc(subscriptionController)
                .perform(get("/subscription").param("msisdn", subscriptionData.getMsisdn()).param("pack", subscriptionData.getPack().toString())
                        .param("channel", subscriptionData.getChannel()));

        Subscription subscription = documentVerifier.verifySubscriptionState(subscriptionData, SubscriptionStatus.PENDING_ACTIVATION);
        subscriptionData.setSubscriptionId(subscription.getSubscriptionId());
        return this;
    }

    public FlowSystem activate(SubscriptionData subscriptionData) throws Exception {
        mockMvc(subscriptionController)
                .perform(put(String.format("/subscription/%s", subscriptionData.getSubscriptionId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(
                                new CallBackRequestBuilder().forMsisdn(subscriptionData.getMsisdn())
                                        .forAction("ACT")
                                        .forStatus("SUCCESS")
                                        .build()
                                        .getBytes()
                        ));
        documentVerifier.verifySubscriptionState(subscriptionData, SubscriptionStatus.ACTIVE);
        return this;
    }

    public FlowSystem renew(SubscriptionData subscriptionData) throws Exception {
        mockMvc(subscriptionController)
                .perform(put(String.format("/subscription/%s", subscriptionData.getSubscriptionId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(
                                new CallBackRequestBuilder().forMsisdn(subscriptionData.getMsisdn())
                                        .forAction("REN")
                                        .forStatus("SUCCESS")
                                        .build()
                                        .getBytes()
                        ));
        documentVerifier.verifySubscriptionState(subscriptionData, SubscriptionStatus.ACTIVE);
        return this;
    }

    public FlowSystem moveToFutureTime(DateTime dateTime) {
        FakeTimeUtils.moveToFutureTime(dateTime);
        return this;
    }

    public FlowSystem verifyCampaignMessageInOBD(SubscriptionData subscriptionData,final String weekMessageId) {
        documentVerifier.verifyCampaignMessageExists(subscriptionData,weekMessageId);
        return this;
    }


    public FlowSystem verifyPackCompletion(SubscriptionData subscriptionData) {
        documentVerifier.verifySubscriptionState(subscriptionData, SubscriptionStatus.PENDING_COMPLETION);
        return this;
    }
}
