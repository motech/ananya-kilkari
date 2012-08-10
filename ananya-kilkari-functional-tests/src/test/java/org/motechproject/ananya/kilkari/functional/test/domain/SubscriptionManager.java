package org.motechproject.ananya.kilkari.functional.test.domain;

import org.motechproject.ananya.kilkari.functional.test.builder.CallBackRequestBuilder;
import org.motechproject.ananya.kilkari.functional.test.verifiers.SubscriptionVerifier;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionStatus;
import org.motechproject.ananya.kilkari.web.controller.SubscriptionController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import static org.motechproject.ananya.kilkari.functional.test.utils.MVCTestUtils.mockMvc;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.put;

@Component
public class SubscriptionManager {
    @Autowired
    private SubscriptionController subscriptionController;
    @Autowired
    private SubscriptionVerifier subscriptionVerifier;

    public void activates(SubscriptionData subscriptionData) throws Exception {
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
        subscriptionVerifier.verifySubscriptionState(subscriptionData, SubscriptionStatus.ACTIVE);
    }

    public void renews(SubscriptionData subscriptionData) throws Exception {
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
        subscriptionVerifier.verifySubscriptionState(subscriptionData, SubscriptionStatus.ACTIVE);
    }

    public void failsRenew(SubscriptionData subscriptionData) throws Exception {
        mockMvc(subscriptionController)
                .perform(put(String.format("/subscription/%s", subscriptionData.getSubscriptionId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(
                                new CallBackRequestBuilder().forMsisdn(subscriptionData.getMsisdn())
                                        .forAction("REN")
                                        .forStatus("BAL_LOW")
                                        .build()
                                        .getBytes()
                        ));
        subscriptionVerifier.verifySubscriptionState(subscriptionData, SubscriptionStatus.SUSPENDED);
    }

    public void confirmsDeactivation(SubscriptionData subscriptionData) throws Exception {
        mockMvc(subscriptionController)
                .perform(put(String.format("/subscription/%s", subscriptionData.getSubscriptionId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(
                                new CallBackRequestBuilder().forMsisdn(subscriptionData.getMsisdn())
                                        .forAction("DCT")
                                        .forStatus("SUCCESS")
                                        .build()
                                        .getBytes()
                        ));
        subscriptionVerifier.verifySubscriptionState(subscriptionData, SubscriptionStatus.DEACTIVATED);
    }
}
