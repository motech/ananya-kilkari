package org.motechproject.ananya.kilkari.reporting.service;


import org.motechproject.ananya.reports.kilkari.contract.request.CallDetailsReportRequest;
import org.motechproject.ananya.reports.kilkari.contract.request.SubscriberReportRequest;
import org.motechproject.ananya.reports.kilkari.contract.request.SubscriptionReportRequest;
import org.motechproject.ananya.reports.kilkari.contract.request.SubscriptionStateChangeRequest;
import org.motechproject.ananya.reports.kilkari.contract.response.LocationResponse;
import org.motechproject.ananya.reports.kilkari.contract.response.SubscriberResponse;

import java.util.List;

public interface ReportingService {
    LocationResponse getLocation(String district, String block, String panchayat);

    SubscriberResponse getSubscriber(String subscriptionId);

    void reportSubscriptionCreation(SubscriptionReportRequest subscriptionReportRequest);

    void reportSubscriptionStateChange(SubscriptionStateChangeRequest subscriptionStateChangeRequest);

    void reportCampaignMessageDeliveryStatus(CallDetailsReportRequest callDetailsReportRequest);

    void reportSubscriberDetailsChange(String subscriptionId, SubscriberReportRequest subscriberReportRequest);

    void reportChangeMsisdnForSubscriber(String subscriptionId, String msisdn);

    List<SubscriberResponse> getSubscribersByMsisdn(String msisdn);
}
