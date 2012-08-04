package org.motechproject.ananya.kilkari.reporting.repository;

import org.motechproject.ananya.kilkari.reporting.domain.*;
import org.motechproject.ananya.kilkari.reporting.profile.TestProfile;
import org.springframework.stereotype.Repository;

@Repository
@TestProfile
public class StubReportingGateway implements ReportingGateway {
    private ReportingGateway behavior;

    @Override
    public SubscriberLocation getLocation(String district, String block, String panchayat) {
        if (verify())
            return behavior.getLocation(district, block, panchayat);
        return null;
    }

    @Override
    public void reportSubscriptionCreation(SubscriptionCreationReportRequest subscriptionCreationReportRequest) {
        if (verify())
            behavior.reportSubscriptionCreation(subscriptionCreationReportRequest);
    }

    @Override
    public void reportSubscriptionStateChange(SubscriptionStateChangeReportRequest subscriptionStateChangeReportRequest) {
        if (verify())
            behavior.reportSubscriptionStateChange(subscriptionStateChangeReportRequest);
    }

    @Override
    public void reportCampaignMessageDeliveryStatus(CampaignMessageDeliveryReportRequest campaignMessageDeliveryReportRequest) {
        if (verify())
            behavior.reportCampaignMessageDeliveryStatus(campaignMessageDeliveryReportRequest);
    }

    @Override
    public void reportSubscriberDetailsChange(String subscriptionId, SubscriberReportRequest request) {
        if (verify())
            behavior.reportSubscriberDetailsChange(subscriptionId, request);
    }

    public void setBehavior(ReportingGateway behavior) {
        this.behavior = behavior;
    }

    private boolean verify() {
        if (behavior == null) {
            System.err.println(String.format("WARNING: %s: You need to set behavior before calling this method. Use setBehavior method.", StubReportingGateway.class.getCanonicalName()));
            return false;
        }
        return true;
    }
}
