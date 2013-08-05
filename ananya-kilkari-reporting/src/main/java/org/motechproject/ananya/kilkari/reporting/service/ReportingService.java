package org.motechproject.ananya.kilkari.reporting.service;


import org.motechproject.ananya.reports.kilkari.contract.request.*;
import org.motechproject.ananya.reports.kilkari.contract.response.LocationResponse;
import org.motechproject.ananya.reports.kilkari.contract.response.SubscriberResponse;

import java.util.List;

public interface ReportingService {
    LocationResponse getLocation(String state, String district, String block, String panchayat);

    SubscriberResponse getSubscriber(String subscriptionId);

    void reportSubscriptionCreation(SubscriptionReportRequest subscriptionReportRequest);

    void reportSubscriptionStateChange(SubscriptionStateChangeRequest subscriptionStateChangeRequest);

    void reportCampaignMessageDeliveryStatus(CallDetailsReportRequest callDetailsReportRequest);

    void reportSubscriberDetailsChange(String subscriptionId, SubscriberReportRequest subscriberReportRequest);

    void reportChangeMsisdnForEarlySubscription(SubscriberChangeMsisdnReportRequest reportRequest);

    void reportCampaignScheduleAlertReceived(CampaignScheduleAlertRequest campaignScheduleAlertRequest);

    List<SubscriberResponse> getSubscribersByMsisdn(String msisdn);

    void reportCampaignChange(CampaignChangeReportRequest expectedReportRequest, String subscriptionId);

    void reportCareRequest(SubscriberCareReportRequest subscriberCareReportRequest);
}
