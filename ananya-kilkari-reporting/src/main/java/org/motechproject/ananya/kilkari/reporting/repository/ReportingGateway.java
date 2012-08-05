package org.motechproject.ananya.kilkari.reporting.repository;

import org.motechproject.ananya.kilkari.contract.request.SubscriptionReportRequest;
import org.motechproject.ananya.kilkari.contract.response.LocationResponse;
import org.motechproject.ananya.kilkari.reporting.domain.*;

public interface ReportingGateway {
    LocationResponse getLocation(String district, String block, String panchayat);

    void reportSubscriptionCreation(SubscriptionReportRequest subscriptionReportRequest);

    void reportSubscriptionStateChange(SubscriptionStateChangeReportRequest subscriptionStateChangeReportRequest);

    void reportCampaignMessageDeliveryStatus(CampaignMessageDeliveryReportRequest campaignMessageDeliveryReportRequest);

    void reportSubscriberDetailsChange(String subscriptionId, SubscriberReportRequest request);
}
