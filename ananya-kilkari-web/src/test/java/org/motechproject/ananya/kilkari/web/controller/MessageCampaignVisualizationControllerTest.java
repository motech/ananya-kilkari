package org.motechproject.ananya.kilkari.web.controller;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.messagecampaign.service.KilkariMessageCampaignService;
import org.springframework.test.web.server.setup.MockMvcBuilders;

import java.util.ArrayList;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

public class MessageCampaignVisualizationControllerTest {

    private MessageCampaignVisualizationController messageCampaignVisualizationController;

    @Mock
    private KilkariMessageCampaignService kilkariMessageCampaignService;

    @Before
    public void setUp() {
        initMocks(this);
        messageCampaignVisualizationController =
                new MessageCampaignVisualizationController(kilkariMessageCampaignService);
    }

    @Test
    public void shouldGetVisualizationForGivenExternalId() throws Exception {
        String msisdn = "msisdn";
        ArrayList<DateTime> messageTimings = new ArrayList<DateTime>();
        messageTimings.add(new DateTime(2012, 10, 25, 15, 48, 58));
        messageTimings.add(new DateTime(2012, 12, 20, 12, 43, 53));

        when(kilkariMessageCampaignService.getMessageTimings(msisdn, MessageCampaignVisualizationController.KILKARI_MESSAGE_CAMPAIGN_NAME)).thenReturn(messageTimings);

        MockMvcBuilders.standaloneSetup(messageCampaignVisualizationController).build()
                .perform(get("/messagecampaign/visualize").param("msisdn", msisdn))
                .andExpect(status().isOk())
                .andExpect(content().type("application/json;charset=UTF-8"))
                .andExpect(content().string("{\"externalId\":\"msisdn\",\"schedules\":[{\"mid\":\"msisdn\",\"messages\":[1351160338000,1355987633000]}]}"));

    }
}
