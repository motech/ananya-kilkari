package org.motechproject.ananya.kilkari.functional.test;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.functional.test.domain.SubscriptionData;
import org.motechproject.ananya.kilkari.functional.test.utils.*;
import org.motechproject.ananya.kilkari.obd.repository.AllCampaignMessages;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionStatus;
import org.motechproject.ananya.kilkari.subscription.repository.AllSubscriptions;
import org.motechproject.ananya.kilkari.web.HttpHeaders;
import org.motechproject.ananya.kilkari.web.controller.SubscriptionController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import static org.motechproject.ananya.kilkari.functional.test.utils.MVCTestUtils.mockMvc;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

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

    @Autowired
    private ReportVerifier reportVerifier;



    public FlowSystem subscribe(final SubscriptionData subscriptionData) throws Exception {
        reportVerifier.setUpReporting(subscriptionData);
        mockMvc(subscriptionController)
                .perform(post("/subscription/")
                .body(TestUtils.toJson(subscriptionData).getBytes())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().type(HttpHeaders.APPLICATION_JSON))
                .andReturn();

        Subscription subscription = documentVerifier.verifySubscriptionState(subscriptionData, SubscriptionStatus.PENDING_ACTIVATION);
        reportVerifier.verifySubscriptionCreationRequest(subscriptionData);
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
