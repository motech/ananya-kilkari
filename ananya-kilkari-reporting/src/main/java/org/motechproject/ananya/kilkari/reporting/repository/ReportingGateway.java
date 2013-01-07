package org.motechproject.ananya.kilkari.reporting.repository;


import org.motechproject.ananya.reports.kilkari.contract.request.*;
import org.motechproject.ananya.reports.kilkari.contract.response.LocationResponse;
import org.motechproject.ananya.reports.kilkari.contract.response.SubscriberResponse;

import java.util.List;

public interface ReportingGateway {
    LocationResponse getLocation(String district, String block, String panchayat);

    SubscriberResponse getSubscriber(String subscriptionId);

    void reportSubscriptionCreation(SubscriptionReportRequest subscriptionReportRequest);

    void reportSubscriptionStateChange(SubscriptionStateChangeRequest subscriptionStateChangeRequest);

    void reportCampaignMessageDeliveryStatus(CallDetailsReportRequest callDetailsReportRequest);

    void reportSubscriberDetailsChange(String subscriptionId, SubscriberReportRequest subscriberReportRequest);

    void reportChangeMsisdnForSubscriber(SubscriberChangeMsisdnReportRequest reportRequest);

    List<SubscriberResponse> getSubscribersByMsisdn(String msisdn);

    void reportCampaignScheduleAlertReceived(CampaignScheduleAlertRequest campaignScheduleAlertRequest);
}
