package org.motechproject.ananya.kilkari.web.IT;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.kilkari.messagecampaign.request.KilkariMessageCampaignRequest;
import org.motechproject.ananya.kilkari.messagecampaign.service.KilkariMessageCampaignService;
import org.motechproject.ananya.kilkari.web.controller.MessageCampaignVisualizationController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.server.setup.MockMvcBuilders;

import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class MessageCampaignVisualizationControllerIT {

    @Autowired
    private MessageCampaignVisualizationController messageCampaignVisualizationController;

    @Autowired
    private KilkariMessageCampaignService kilkariMessageCampaignService;

    @Test
    public void shouldGetVisualizationForGivenExternalId() throws Exception {
        String msisdn = "msisdn";

        KilkariMessageCampaignRequest messageCampaignRequest = new KilkariMessageCampaignRequest(
                msisdn, MessageCampaignVisualizationController.KILKARI_MESSAGE_CAMPAIGN_NAME, new DateTime(2012, 5, 5, 13, 30, 30), new DateTime(2012, 5, 5, 0, 0));
        kilkariMessageCampaignService.start(messageCampaignRequest);

        MockMvcBuilders.standaloneSetup(messageCampaignVisualizationController).build()
                .perform(get("/messagecampaign/visualize").param("msisdn", msisdn))
                .andExpect(status().isOk())
                .andExpect(content().type("application/json;charset=UTF-8"));
//                .andExpect(content().string("{\"externalId\":\"msisdn\",\"schedules\":[{\"mid\":\"msisdn\",\"messages\":[1351160338000,1355987633000]}]}"));

    }
}
