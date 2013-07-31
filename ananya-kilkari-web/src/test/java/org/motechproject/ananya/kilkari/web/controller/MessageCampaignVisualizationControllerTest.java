package org.motechproject.ananya.kilkari.web.controller;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.service.KilkariCampaignService;
import org.motechproject.ananya.kilkari.web.HttpHeaders;
import org.springframework.test.web.server.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

public class MessageCampaignVisualizationControllerTest {

    private MessageCampaignVisualizationController messageCampaignVisualizationController;

    @Mock
    private KilkariCampaignService kilkariCampaignService;

    @Before
    public void setUp() {
        initMocks(this);
        messageCampaignVisualizationController =
                new MessageCampaignVisualizationController(kilkariCampaignService);
    }

    @Test
    public void shouldGetVisualizationForGivenExternalId() throws Exception {
        String msisdn = "msisdn";
        ArrayList<DateTime> messageTimings = new ArrayList<DateTime>();
        messageTimings.add(new DateTime(2012, 10, 25, 15, 48, 58));
        messageTimings.add(new DateTime(2012, 12, 20, 12, 43, 53));

        HashMap<String, List<DateTime>> subscriptionCampaignMap = new HashMap<String, List<DateTime>>();
        subscriptionCampaignMap.put("Message Schedule: ", messageTimings);
        subscriptionCampaignMap.put("Inbox Deletion: ", messageTimings);
        when(kilkariCampaignService.getTimings(msisdn)).thenReturn(subscriptionCampaignMap);

        MockMvcBuilders.standaloneSetup(messageCampaignVisualizationController).build()
                .perform(get("/messagecampaign/visualize").param("msisdn", msisdn))
                .andExpect(status().isOk())
                .andExpect(content().type(HttpHeaders.APPLICATION_JSON))
                .andExpect(content().string("{\"externalId\":\"msisdn\",\"campaignSchedules\":[{\"mid\":\"Message Schedule: \",\"messages\":[1351160338000,1355987633000]}]," +
                        "\"subscriptionSchedules\":[{\"mid\":\"Inbox Deletion: \",\"messages\":[1351160338000,1355987633000]}],\"startDate\":1351160338000}"));

    }
}
