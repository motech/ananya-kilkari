package org.motechproject.ananya.kilkari.web.it;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ananya.kilkari.messagecampaign.request.MessageCampaignRequest;
import org.motechproject.ananya.kilkari.messagecampaign.service.MessageCampaignService;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.repository.AllSubscriptions;
import org.motechproject.ananya.kilkari.web.HttpHeaders;
import org.motechproject.ananya.kilkari.web.SpringIntegrationTest;
import org.motechproject.ananya.kilkari.web.controller.MessageCampaignVisualizationController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.server.setup.MockMvcBuilders;

import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

public class MessageCampaignVisualizationControllerIT extends SpringIntegrationTest {

    @Autowired
    private MessageCampaignVisualizationController messageCampaignVisualizationController;
    @Autowired
    private MessageCampaignService messageCampaignService;
    @Autowired
    private AllSubscriptions allSubscriptions;

    @Test
    public void shouldGetVisualizationForGivenExternalId() throws Exception {
        String msisdn = "9876543210";
        SubscriptionPack subscriptionPack = SubscriptionPack.FIFTEEN_MONTHS;
        Subscription subscription = new Subscription(msisdn, subscriptionPack, DateTime.now());
        allSubscriptions.add(subscription);
        markForDeletion(subscription);

        MessageCampaignRequest messageCampaignRequest = new MessageCampaignRequest(
                subscription.getSubscriptionId(), subscriptionPack.name(), subscription.getStartDate());
        messageCampaignService.start(messageCampaignRequest, 0, 0);

        MockMvcBuilders.standaloneSetup(messageCampaignVisualizationController).build()
                .perform(get("/messagecampaign/visualize").param("msisdn", msisdn))
                .andExpect(status().isOk())
                .andExpect(content().type(HttpHeaders.APPLICATION_JSON));


        messageCampaignService.stop(messageCampaignRequest);
    }
}
