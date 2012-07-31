package org.motechproject.ananya.kilkari.reporting.service;

import org.motechproject.ananya.kilkari.reporting.domain.*;

public interface ReportingService {
    SubscriberLocation getLocation(String district, String block, String panchayat);

    void reportSubscriptionCreation(SubscriptionCreationReportRequest subscriptionCreationReportRequest);

    void reportSubscriptionStateChange(SubscriptionStateChangeReportRequest subscriptionStateChangeReportRequest);

    void reportCampaignMessageDeliveryStatus(CampaignMessageDeliveryReportRequest campaignMessageDeliveryReportRequest);

    void reportSubscriberDetailsChange(SubscriberUpdateReportRequest subscriberUpdateReportRequest);
}
