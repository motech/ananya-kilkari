package org.motechproject.ananya.kilkari.reporting.service;

import org.motechproject.ananya.kilkari.contract.request.*;
import org.motechproject.ananya.kilkari.contract.response.LocationResponse;
import org.motechproject.ananya.kilkari.contract.response.SubscriberResponse;

public interface ReportingService {
    LocationResponse getLocation(String district, String block, String panchayat);

    SubscriberResponse getSubscriber(String subscriptionId);

    void reportSubscriptionCreation(SubscriptionReportRequest subscriptionReportRequest);

    void reportSubscriptionStateChange(SubscriptionStateChangeRequest subscriptionStateChangeRequest);

    void reportCampaignMessageDeliveryStatus(CallDetailsReportRequest callDetailsReportRequest);

    void reportSubscriberDetailsChange(String subscriptionId, SubscriberReportRequest subscriberReportRequest);

    void reportChangeMsisdnForSubscriber(String subscriptionId, String msisdn);

    void reportChangePack(SubscriptionChangePackRequest capture);
}
