package org.motechproject.ananya.kilkari.functional.test.domain;

import org.motechproject.ananya.kilkari.functional.test.utils.JsonUtils;
import org.motechproject.ananya.kilkari.functional.test.verifiers.CampaignMessageVerifier;
import org.motechproject.ananya.kilkari.functional.test.verifiers.OnMobileOBDVerifier;
import org.motechproject.ananya.kilkari.obd.repository.AllCampaignMessages;
import org.motechproject.ananya.kilkari.web.HttpHeaders;
import org.motechproject.ananya.kilkari.web.controller.InboxController;
import org.motechproject.ananya.kilkari.web.controller.SubscriptionController;
import org.motechproject.ananya.kilkari.web.response.SubscriptionWebResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.server.MvcResult;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.motechproject.ananya.kilkari.functional.test.utils.MVCTestUtils.mockMvc;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

@Component
public class User {

    @Autowired
    private CampaignMessageVerifier campaignMessageVerifier;
    @Autowired
    private OnMobileOBDVerifier onMobileOBDVerifier;
    @Autowired
    private SubscriptionController subscriptionController;

    public void messageIsReady(SubscriptionData subscriptionData, String weekMessageId) {
        campaignMessageVerifier.verifyCampaignMessageExists(subscriptionData, weekMessageId);
    }

    public void messageIsNotCreated(SubscriptionData subscriptionData, String weekMessageId) {
        campaignMessageVerifier.verifyCampaignMessageIsNotCreatedAfterCampaignAlert(subscriptionData, weekMessageId);
    }

    public void messageWasDeliveredDuringFirstSlot(SubscriptionData subscriptionData, String weekMessageId) {
        onMobileOBDVerifier.verifyThatNewMessageWasDelivered(subscriptionData, weekMessageId);
    }

    public void messageWasDeliveredDuringSecondSlot(SubscriptionData subscriptionData, String weekMessageId) {
        onMobileOBDVerifier.verifyThatRetryMessageWasDelivered(subscriptionData, weekMessageId);
    }

    public void resetOnMobileOBDVerifier() {
        onMobileOBDVerifier.reset();
    }

    public void resetCampaignMessageVerifier() {
        campaignMessageVerifier.reset();
    }

    public void canListenToThisWeeksInboxMessage(SubscriptionData subscriptionData, String inboxMessageId) throws Exception {
        SubscriptionWebResponse subscriptionWebResponse = getSubscriberDetails(subscriptionData);
        assertEquals(1, subscriptionWebResponse.getSubscriptionDetails().size());
        assertEquals(inboxMessageId, subscriptionWebResponse.getSubscriptionDetails().get(0).getLastCampaignId());
    }

    public void cannotListenToPreviousWeeksInboxMessage(SubscriptionData subscriptionData, String inboxMessageId) throws Exception {
        SubscriptionWebResponse subscriptionWebResponse = getSubscriberDetails(subscriptionData);
        assertEquals(1, subscriptionWebResponse.getSubscriptionDetails().size());
        assertNotSame(inboxMessageId, subscriptionWebResponse.getSubscriptionDetails().get(0).getLastCampaignId());
    }

    private SubscriptionWebResponse getSubscriberDetails(SubscriptionData subscriptionData) throws Exception {
        MvcResult result = mockMvc(subscriptionController)
                .perform(get("/subscriber").param("msisdn", subscriptionData.getMsisdn()).param("channel", "IVR"))
                .andExpect(status().isOk())
                .andExpect(content().type(HttpHeaders.APPLICATION_JAVASCRIPT))
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        return JsonUtils.fromJsonWithResponse(responseString, SubscriptionWebResponse.class);
    }
}
