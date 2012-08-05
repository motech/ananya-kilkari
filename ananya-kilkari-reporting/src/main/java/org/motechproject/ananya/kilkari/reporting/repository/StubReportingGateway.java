package org.motechproject.ananya.kilkari.reporting.repository;

import org.motechproject.ananya.kilkari.contract.request.CallDetailsRequest;
import org.motechproject.ananya.kilkari.contract.request.SubscriberReportRequest;
import org.motechproject.ananya.kilkari.contract.request.SubscriptionReportRequest;
import org.motechproject.ananya.kilkari.contract.request.SubscriptionStateChangeRequest;
import org.motechproject.ananya.kilkari.contract.response.LocationResponse;
import org.motechproject.ananya.kilkari.reporting.profile.TestProfile;
import org.springframework.stereotype.Repository;

@Repository
@TestProfile
public class StubReportingGateway implements ReportingGateway {
    private ReportingGateway behavior;

    @Override
    public LocationResponse getLocation(String district, String block, String panchayat) {
        if (verify())
            return behavior.getLocation(district, block, panchayat);
        return null;
    }

    @Override
    public void reportSubscriptionCreation(SubscriptionReportRequest subscriptionReportRequest) {
        if (verify())
            behavior.reportSubscriptionCreation(subscriptionReportRequest);
    }

    @Override
    public void reportSubscriptionStateChange(SubscriptionStateChangeRequest subscriptionStateChangeRequest) {
        if (verify())
            behavior.reportSubscriptionStateChange(subscriptionStateChangeRequest);
    }

    @Override
    public void reportCampaignMessageDeliveryStatus(CallDetailsRequest callDetailsRequest) {
        if (verify())
            behavior.reportCampaignMessageDeliveryStatus(callDetailsRequest);
    }

    @Override
    public void reportSubscriberDetailsChange(String subscriptionId, SubscriberReportRequest subscriberReportRequest) {
        if (verify())
            behavior.reportSubscriberDetailsChange(subscriptionId, subscriberReportRequest);
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
