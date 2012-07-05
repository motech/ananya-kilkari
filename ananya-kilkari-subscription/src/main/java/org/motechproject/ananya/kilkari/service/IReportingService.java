package org.motechproject.ananya.kilkari.service;

import org.motechproject.ananya.kilkari.domain.SubscriberLocation;
import org.motechproject.ananya.kilkari.domain.SubscriptionCreationReportRequest;
import org.motechproject.ananya.kilkari.domain.SubscriptionStateChangeReportRequest;

public interface IReportingService {
    String CREATE_SUBSCRIPTION_PATH = "subscription";
    String SUBSCRIPTION_STATE_CHANGE_PATH = "updatesubscription";
    String GET_LOCATION_PATH = "location";

    void createSubscription(SubscriptionCreationReportRequest subscriptionCreationReportRequest);

    void updateSubscriptionStateChange(SubscriptionStateChangeReportRequest subscriptionStateChangeReportRequest);

    SubscriberLocation getLocation(String district, String block, String panchayat);
}
