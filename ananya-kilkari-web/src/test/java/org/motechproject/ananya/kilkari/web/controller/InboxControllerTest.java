package org.motechproject.ananya.kilkari.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.kilkari.web.HttpHeaders;
import org.springframework.http.MediaType;

import static org.motechproject.ananya.kilkari.web.MVCTestUtils.mockMvc;
import static org.motechproject.ananya.kilkari.web.controller.ResponseMatchers.baseResponseMatcher;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

public class InboxControllerTest {

    private InboxController inboxController;

    @Before
    public void setup(){
        inboxController = new InboxController();
    }

    @Test
    public void shouldRecordInboxCallDetails() throws Exception {
        mockMvc(inboxController)
                .perform(post("/inbox/calldetails").body(getRequestJSON().getBytes()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().type(HttpHeaders.APPLICATION_JSON))
                .andExpect(content().string(baseResponseMatcher("SUCCESS", "Inbox calldetails request submitted successfully")));
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
                "              \"endTime\":\"22-12-2011 02-59-59\",\n" +
                "           }\n" +
                "        },\n" +
                "        {\n" +
                "        \"msisdn\":\"9740123425\",\n" +
                "        \"pack\": \"PCK1\",\n" +
                "        \"campaignId\":\"WEEK11\",\n" +
                "        \"callDetailRecord\":{\n" +
                "              \"startTime\":\"22-12-2011 02-59-59\",\n" +
                "              \"endTime\":\"23-12-2011 02-59-59\",\n" +
                "           }\n" +
                "        }\n" +
                "   ]\n" +
                "}";
        return json;
    }
}
