package org.motechproject.ananya.kilkari.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.request.CallDurationWebRequest;
import org.motechproject.ananya.kilkari.request.InboxCallDetailsWebRequest;
import org.motechproject.ananya.kilkari.service.KilkariCampaignService;
import org.motechproject.ananya.kilkari.web.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ananya.kilkari.web.MVCTestUtils.mockMvc;
import static org.motechproject.ananya.kilkari.web.controller.ResponseMatchers.baseResponseMatcher;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

public class InboxControllerTest {

    private InboxController inboxController;

    @Mock
    private KilkariCampaignService kilkariCampaignService;
    @Before
    public void setup(){
        initMocks(this);
        inboxController = new InboxController(kilkariCampaignService);
    }

    @Test
    public void shouldRecordInboxCallDetails() throws Exception {
        mockMvc(inboxController)
                .perform(post("/inbox/calldetails").body(getRequestJSON().getBytes()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().type(HttpHeaders.APPLICATION_JSON))
                .andExpect(content().string(baseResponseMatcher("SUCCESS", "Inbox calldetails request submitted successfully")));

        ArgumentCaptor<InboxCallDetailsWebRequest> captor = ArgumentCaptor.forClass(InboxCallDetailsWebRequest.class);
        verify(kilkariCampaignService, times(2)).publishInboxCallDetailsRequest(captor.capture());
        List<InboxCallDetailsWebRequest> requests = captor.getAllValues();
        assertEquals(createRequest("9740123425", "PCK1", "WEEK12", "21-12-2011 02-59-59", "22-12-2011 02-59-58"), requests.get(0));
        assertEquals(createRequest("9740123426", "PCK2", "WEEK11", "22-12-2011 02-59-49", "23-12-2011 02-59-48"), requests.get(1));
    }


    private InboxCallDetailsWebRequest createRequest(String msisdn, String pack, String campaignId, final String startTime, final String endTime) {
        return new InboxCallDetailsWebRequest(msisdn, campaignId, new CallDurationWebRequest(startTime, endTime), pack);
    }


    private String getRequestJSON() {
        String json = "{\n" +
                "   \"callrecords\" : [\n" +
                "        {\n" +
                "        \"msisdn\":\"9740123425\",\n" +
                "        \"pack\": \"PCK1\",\n" +
                "        \"campaignId\":\"WEEK12\",\n" +
                "        \"callDetailRecord\":{\n" +
                "              \"startTime\":\"21-12-2011 02-59-59\",\n" +
                "              \"endTime\":\"22-12-2011 02-59-58\"\n" +
                "           }\n" +
                "        },\n" +
                "        {\n" +
                "        \"msisdn\":\"9740123426\",\n" +
                "        \"pack\": \"PCK2\",\n" +
                "        \"campaignId\":\"WEEK11\",\n" +
                "        \"callDetailRecord\":{\n" +
                "              \"startTime\":\"22-12-2011 02-59-49\",\n" +
                "              \"endTime\":\"23-12-2011 02-59-48\"\n" +
                "           }\n" +
                "        }\n" +
                "   ]\n" +
                "}";
        return json;
    }
}
