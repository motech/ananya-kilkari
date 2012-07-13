package org.motechproject.ananya.kilkari.reporting.gateway;

import org.motechproject.ananya.kilkari.reporting.domain.CampaignMessageDeliveryReportRequest;
import org.motechproject.ananya.kilkari.reporting.domain.SubscriberLocation;
import org.motechproject.ananya.kilkari.reporting.domain.SubscriptionCreationReportRequest;
import org.motechproject.ananya.kilkari.reporting.domain.SubscriptionStateChangeReportRequest;

public interface ReportingGateway {
    String CREATE_SUBSCRIPTION_PATH = "subscription";
    String SUBSCRIPTION_STATE_CHANGE_PATH = "updatesubscription";
    String GET_LOCATION_PATH = "location";
    String OBD_CALL_DETAILS_PATH = "obd/callDetails";

    void createSubscription(SubscriptionCreationReportRequest subscriptionCreationReportRequest);

    void updateSubscriptionStateChange(SubscriptionStateChangeReportRequest subscriptionStateChangeReportRequest);

    SubscriberLocation getLocation(String district, String block, String panchayat);

    void reportCampaignMessageDelivery(CampaignMessageDeliveryReportRequest campaignMessageDeliveryReportRequest);
}
