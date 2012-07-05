package org.motechproject.ananya.kilkari.web.IT;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ananya.kilkari.domain.Subscription;
import org.motechproject.ananya.kilkari.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.messagecampaign.request.KilkariMessageCampaignRequest;
import org.motechproject.ananya.kilkari.messagecampaign.service.KilkariMessageCampaignService;
import org.motechproject.ananya.kilkari.repository.AllSubscriptions;
import org.motechproject.ananya.kilkari.service.KilkariCampaignService;
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
    private KilkariMessageCampaignService kilkariMessageCampaignService;
    @Autowired
    private AllSubscriptions allSubscriptions;

    @Test
    public void shouldGetVisualizationForGivenExternalId() throws Exception {
        String msisdn = "9876543210";

        KilkariMessageCampaignRequest messageCampaignRequest = new KilkariMessageCampaignRequest(
                msisdn, KilkariCampaignService.KILKARI_MESSAGE_CAMPAIGN_NAME,
                new DateTime(2012, 5, 5, 13, 30, 30), new DateTime(2012, 5, 5, 0, 0));
        kilkariMessageCampaignService.start(messageCampaignRequest);
        Subscription subscription = new Subscription(msisdn, SubscriptionPack.FIFTEEN_MONTHS);
        allSubscriptions.add(subscription);
        markForDeletion(subscription);

        MockMvcBuilders.standaloneSetup(messageCampaignVisualizationController).build()
                .perform(get("/messagecampaign/visualize").param("msisdn", msisdn))
                .andExpect(status().isOk())
                .andExpect(content().type("application/json;charset=UTF-8"));
//                .andExpect(content().string("{\"externalId\":\"msisdn\",\"schedules\":[{\"mid\":\"msisdn\",\"messages\":[1351160338000,1355987633000]}]}"));

        kilkariMessageCampaignService.stop(messageCampaignRequest);
    }
}
