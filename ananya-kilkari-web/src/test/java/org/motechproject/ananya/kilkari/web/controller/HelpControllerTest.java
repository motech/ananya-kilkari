package org.motechproject.ananya.kilkari.web.controller;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.ananya.kilkari.obd.domain.Channel;
import org.motechproject.ananya.kilkari.request.HelpWebRequest;
import org.motechproject.ananya.kilkari.service.KilkariSubscriberCareService;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriberCareDoc;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriberCareReasons;
import org.motechproject.ananya.kilkari.web.HttpHeaders;
import org.motechproject.ananya.kilkari.web.MVCTestUtils;
import org.springframework.http.MediaType;
import org.springframework.test.web.server.MvcResult;

import java.nio.charset.Charset;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.motechproject.ananya.kilkari.web.controller.ResponseMatchers.baseResponseMatcher;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

public class HelpControllerTest {

    private HelpController helpController;

    @Mock
    private KilkariSubscriberCareService kilkariSubscriberCareService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        helpController = new HelpController(kilkariSubscriberCareService);
    }

    @Test
    public void shouldProcessCareRequest() throws Exception {
        MVCTestUtils.mockMvc(helpController)
                .perform(get("/help").param("msisdn", "1234567890").param("reason", "help").param("channel", "ivr"))
                .andExpect(status().isOk())
                .andExpect(content().type(HttpHeaders.APPLICATION_JAVASCRIPT))
                .andExpect(content().string(baseResponseMatcher("SUCCESS", "Subscriber care request processed successfully")));


        verify(kilkariSubscriberCareService).processSubscriberCareRequest(
                eq("1234567890"), eq("help"), eq("ivr"), any(DateTime.class));
    }

    @Test
    public void shouldReturnMsisdnsInCSVFormatWhichHaveAcessedHelp() throws Exception {
        String fromDate = "12-12-2012 00:00:00";
        String toDate = "15-12-2012 00:00:00";
        final DateTime now = DateTime.now();
        when(kilkariSubscriberCareService.fetchSubscriberCareDocs(new HelpWebRequest(fromDate, toDate, Channel.CONTACT_CENTER.name()))).thenReturn(new ArrayList<SubscriberCareDoc>(){{
            add(new SubscriberCareDoc("msisdn", SubscriberCareReasons.HELP, now, Channel.IVR));
        }});

        MvcResult mvcResult = MVCTestUtils.mockMvc(helpController)
                .perform(get("/help/list").param("startDateTime", fromDate).param("endDateTime", toDate).param("channel", Channel.CONTACT_CENTER.name()).
                        accept(new MediaType("text", "csv", Charset.defaultCharset())))
                .andExpect(status().isOk()).andReturn();

        verify(kilkariSubscriberCareService).fetchSubscriberCareDocs(new HelpWebRequest(fromDate, toDate, Channel.CONTACT_CENTER.name()));
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertTrue(contentAsString.contains("msisdn,date,time"));
        assertTrue(contentAsString.contains(String.format("msisdn,%s,%s", now.toString("dd-MM-yyyy"), now.toString("HH:mm:ss"))));
    }

    @Test
    public void shouldThrowAnExceptionIfDateTimeFormatIsInvalid() throws Exception {
        String fromDate = "blah";
        String toDate = "everon";
        final DateTime now = DateTime.now();
        when(kilkariSubscriberCareService.fetchSubscriberCareDocs(new HelpWebRequest(fromDate, toDate, Channel.CONTACT_CENTER.name()))).thenReturn(new ArrayList<SubscriberCareDoc>(){{
            add(new SubscriberCareDoc("msisdn", SubscriberCareReasons.HELP, now, Channel.IVR));
        }});

        MvcResult mvcResult = MVCTestUtils.mockMvc(helpController)
                .perform(get("/help/list").param("startDateTime", fromDate).param("endDateTime", toDate).param("channel", Channel.CONTACT_CENTER.name()).
                        accept(new MediaType("text", "csv", Charset.defaultCharset())))
                .andExpect(status().isBadRequest()).andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertEquals("{\"status\":\"FAILED\",\"description\":\"Invalid start date : blah,Invalid end date : everon\"}", contentAsString);
    }
}
