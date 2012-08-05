package org.motechproject.ananya.kilkari.reporting.service;

import org.motechproject.ananya.kilkari.contract.request.SubscriptionReportRequest;
import org.motechproject.ananya.kilkari.contract.request.SubscriptionStateChangeRequest;
import org.motechproject.ananya.kilkari.contract.response.LocationResponse;
import org.motechproject.ananya.kilkari.reporting.domain.*;

public interface ReportingService {
    LocationResponse getLocation(String district, String block, String panchayat);

    void reportSubscriptionCreation(SubscriptionReportRequest subscriptionReportRequest);

    void reportSubscriptionStateChange(SubscriptionStateChangeRequest subscriptionStateChangeRequest);

    void reportCampaignMessageDeliveryStatus(CampaignMessageDeliveryReportRequest campaignMessageDeliveryReportRequest);

    void reportSubscriberDetailsChange(String subscriptionId, SubscriberReportRequest subscriberReportRequest);
}
