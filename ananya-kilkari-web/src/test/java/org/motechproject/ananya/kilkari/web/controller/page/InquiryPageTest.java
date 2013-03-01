package org.motechproject.ananya.kilkari.web.controller.page;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.kilkari.obd.domain.Channel;
import org.motechproject.ananya.kilkari.service.KilkariSubscriptionService;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionStatus;
import org.motechproject.ananya.kilkari.subscription.service.response.SubscriptionDetailsResponse;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public class InquiryPageTest {
    @Mock
    private KilkariSubscriptionService kilkariSubscriptionService;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldGetAllDetails() {
        InquiryPage inquiryPage = new InquiryPage(kilkariSubscriptionService);
        Map<String, String> header = new HashMap<>();
        header.put("subscriptionId", "Subscription Id");
        header.put("pack", "Pack");
        header.put("status", "Status");
        header.put("campaignId", "Campaign Id");
        header.put("beneficiaryName", "Name");
        header.put("beneficiaryAge", "Age");
        header.put("lastWeeklyMessageScheduledDate", "Last Weekly Message Scheduled Date");
        header.put("startWeekNumber", "Start Week Number");
        header.put("expectedDateOfDelivery", "Expected Date of Delivery");
        header.put("dateOfBirth", "Date of Birth");
        String msisdn = "1234567890";
        ArrayList<SubscriptionDetailsResponse> subscriptionDetailsResponses = new ArrayList<>();
        SubscriptionDetailsResponse subscriptionDetailsResponse = new SubscriptionDetailsResponse("subscriptionId", SubscriptionPack.BARI_KILKARI, SubscriptionStatus.ACTIVE, "campaignId");
        subscriptionDetailsResponses.add(subscriptionDetailsResponse);
        when(kilkariSubscriptionService.getSubscriptionDetails(msisdn, Channel.CONTACT_CENTER)).thenReturn(subscriptionDetailsResponses);

        Map<String, Object> model = inquiryPage.getSubscriptionDetails(msisdn);

        assertEquals(1, model.keySet().size());
        assertEquals(subscriptionDetailsResponses, ((SubscriptionDataGrid) model.get("subscriptionDetails")).getContent());
        assertEquals(header, ((SubscriptionDataGrid) model.get("subscriptionDetails")).getHeader());
    }

    @Test
    public void shouldGetError() {
        InquiryPage inquiryPage = new InquiryPage(kilkariSubscriptionService);
        String errorMessage = "our custom error message";
        String msisdn = "123456";
        when(kilkariSubscriptionService.getSubscriptionDetails(msisdn,Channel.CONTACT_CENTER)).thenThrow(new RuntimeException(errorMessage));

        Map<String, Object> model = inquiryPage.getSubscriptionDetails(msisdn);

        assertEquals(1, model.keySet().size());
        assertTrue(((String) model.get("subscriberError")).contains(errorMessage));
    }

    @Test
    public void shouldGetInquiryPage() {
        InquiryPage inquiryPage = new InquiryPage(kilkariSubscriptionService);

        ModelAndView modelView = inquiryPage.display();

        assertEquals("admin/inquiry",modelView.getViewName());
    }
}
