package org.motechproject.ananya.kilkari.service;

import org.motechproject.ananya.kilkari.domain.SubscriberLocation;
import org.motechproject.ananya.kilkari.domain.SubscriptionCreationReportRequest;
import org.motechproject.ananya.kilkari.domain.SubscriptionStateChangeReportRequest;

public interface ReportingService {
    SubscriberLocation getLocation(String district, String block, String panchayat);

    void reportSubscriptionCreation(SubscriptionCreationReportRequest subscriptionCreationReportRequest);

    void reportSubscriptionStateChange(SubscriptionStateChangeReportRequest subscriptionStateChangeReportRequest);
}
