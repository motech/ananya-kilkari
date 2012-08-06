package org.motechproject.ananya.kilkari.reporting.repository;

import org.motechproject.ananya.kilkari.contract.request.CallDetailsReportRequest;
import org.motechproject.ananya.kilkari.contract.request.SubscriberReportRequest;
import org.motechproject.ananya.kilkari.contract.request.SubscriptionReportRequest;
import org.motechproject.ananya.kilkari.contract.request.SubscriptionStateChangeRequest;
import org.motechproject.ananya.kilkari.contract.response.LocationResponse;

public interface ReportingGateway {
    LocationResponse getLocation(String district, String block, String panchayat);

    void reportSubscriptionCreation(SubscriptionReportRequest subscriptionReportRequest);

    void reportSubscriptionStateChange(SubscriptionStateChangeRequest subscriptionStateChangeRequest);

    void reportCampaignMessageDeliveryStatus(CallDetailsReportRequest callDetailsReportRequest);

    void reportSubscriberDetailsChange(String subscriptionId, SubscriberReportRequest subscriberReportRequest);
}
