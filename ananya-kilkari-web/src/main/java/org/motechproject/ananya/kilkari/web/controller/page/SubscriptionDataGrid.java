package org.motechproject.ananya.kilkari.web.controller.page;

import org.motechproject.ananya.kilkari.subscription.service.response.SubscriptionDetailsResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubscriptionDataGrid {
    private Map<String, String> header = new HashMap<>();
    private List<SubscriptionDetailsResponse> content;

    public SubscriptionDataGrid( List<SubscriptionDetailsResponse> content) {
        this.content = content;
        initHeaders();
    }

    private void initHeaders() {
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
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public List<SubscriptionDetailsResponse> getContent() {
        return content;
    }
}
