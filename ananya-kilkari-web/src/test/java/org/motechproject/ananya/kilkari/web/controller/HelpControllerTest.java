package org.motechproject.ananya.kilkari.web.controller;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.ananya.kilkari.service.KilkariSubscriberCareService;
import org.motechproject.ananya.kilkari.web.HttpHeaders;
import org.motechproject.ananya.kilkari.web.MVCTestUtils;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.motechproject.ananya.kilkari.web.controller.ResponseMatchers.baseResponseMatcher;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

public class HelpControllerTest {
    @Mock
    private KilkariSubscriberCareService kilkariSubscriberCareService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldProcessCareRequest() throws Exception {
        HelpController helpController = new HelpController(kilkariSubscriberCareService);

        MVCTestUtils.mockMvc(helpController)
                .perform(get("/help").param("msisdn", "1234567890").param("reason", "help").param("channel", "ivr"))
                .andExpect(status().isOk())
                .andExpect(content().type(HttpHeaders.APPLICATION_JAVASCRIPT))
                .andExpect(content().string(baseResponseMatcher("SUCCESS", "Subscriber care request processed successfully")));


        verify(kilkariSubscriberCareService).processSubscriberCareRequest(
                eq("1234567890"), eq("help"), eq("ivr"), any(DateTime.class));
    }

}
