package org.motechproject.ananya.kilkari.functional.test.domain;

import org.motechproject.ananya.kilkari.functional.test.builder.CampaignChangeRequestBuilder;
import org.motechproject.ananya.kilkari.functional.test.utils.JsonUtils;
import org.motechproject.ananya.kilkari.functional.test.verifiers.ReportVerifier;
import org.motechproject.ananya.kilkari.functional.test.verifiers.SubscriptionVerifier;
import org.motechproject.ananya.kilkari.obd.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionStatus;
import org.motechproject.ananya.kilkari.web.HttpHeaders;
import org.motechproject.ananya.kilkari.web.controller.SubscriptionController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import static org.motechproject.ananya.kilkari.functional.test.utils.MVCTestUtils.mockMvc;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

@Component
public class CallCenter {
    @Autowired
    private SubscriptionController subscriptionController;
    @Autowired
    private SubscriptionVerifier subscriptionVerifier;
    @Autowired
    private ReportVerifier reportVerifier;

    public void subscribes(SubscriptionData subscriptionData) throws Exception {
        reportVerifier.setUpReportingExpectations(subscriptionData);
        mockMvc(subscriptionController)
                .perform(post("/subscription/")
                        .body(JsonUtils.toJson(subscriptionData).getBytes())
                        .param("channel", Channel.CONTACT_CENTER.name())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().type(HttpHeaders.APPLICATION_JSON))
                .andReturn();

        Subscription subscription = subscriptionVerifier.verifySubscriptionState(subscriptionData, SubscriptionStatus.PENDING_ACTIVATION);
        reportVerifier.verifySubscriptionCreationRequest(subscriptionData);
        subscriptionData.setSubscriptionId(subscription.getSubscriptionId());
    }

    public void changesCampaign(SubscriptionData subscriptionData) throws Exception {
        mockMvc(subscriptionController)
                .perform(post("/subscription/" + subscriptionData.getSubscriptionId() + "/changecampaign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(
                                new CampaignChangeRequestBuilder()
                                        .forReason("INFANT_DEATH")
                                        .build()
                                        .getBytes()
                        ).param("channel",Channel.CONTACT_CENTER.name()));
        
    }

    public void unSubscribes(SubscriptionData subscriptionData) throws Exception {
        reportVerifier.setUpReportingExpectations(subscriptionData);
        UnSubscriptionData unSubscriptionData = new UnSubscriptionData("CONTACT_CENTER", "reason");
        mockMvc(subscriptionController)
                .perform(delete("/subscription/"+ subscriptionData.getSubscriptionId())
                        .body(JsonUtils.toJson(unSubscriptionData).getBytes())
                        .param("channel",Channel.CONTACT_CENTER.name())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().type(HttpHeaders.APPLICATION_JSON));

        Subscription subscription = subscriptionVerifier.verifySubscriptionState(subscriptionData, SubscriptionStatus.PENDING_DEACTIVATION);
        reportVerifier.verifySubscriptionCreationRequest(subscriptionData);
        subscriptionData.setSubscriptionId(subscription.getSubscriptionId());
    }
}
