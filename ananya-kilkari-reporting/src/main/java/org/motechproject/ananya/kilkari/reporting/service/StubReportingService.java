package org.motechproject.ananya.kilkari.reporting.service;

import org.motechproject.ananya.kilkari.contract.request.CallDetailsReportRequest;
import org.motechproject.ananya.kilkari.contract.request.SubscriberReportRequest;
import org.motechproject.ananya.kilkari.contract.request.SubscriptionReportRequest;
import org.motechproject.ananya.kilkari.contract.request.SubscriptionStateChangeRequest;
import org.motechproject.ananya.kilkari.contract.response.LocationResponse;
import org.motechproject.ananya.kilkari.contract.response.SubscriberResponse;
import org.motechproject.ananya.kilkari.reporting.profile.TestProfile;
import org.springframework.stereotype.Service;

@Service
@TestProfile
public class StubReportingService implements ReportingService {
    private ReportingService behavior;
    private boolean reportCampaignMessageDeliveryCalled;

    @Override
    public LocationResponse getLocation(String district, String block, String panchayat) {
        if (verify()) {
            return behavior.getLocation(district, block, panchayat);
        }
        return null;
    }

    @Override
    public SubscriberResponse getSubscriber(String subscriptionId) {
        if (verify()) {
            return behavior.getSubscriber(subscriptionId);
        }
        return null;
    }

    @Override
    public void reportSubscriptionCreation(SubscriptionReportRequest subscriptionReportRequest) {
        if (verify()) {
            behavior.reportSubscriptionCreation(subscriptionReportRequest);
        }
    }

    @Override
    public void reportSubscriptionStateChange(SubscriptionStateChangeRequest subscriptionStateChangeRequest) {
        if (verify()) {
            behavior.reportSubscriptionStateChange(subscriptionStateChangeRequest);
        }
    }

    @Override
    public void reportCampaignMessageDeliveryStatus(CallDetailsReportRequest callDetailsReportRequest) {
        if (verify()) {
            behavior.reportCampaignMessageDeliveryStatus(callDetailsReportRequest);
            reportCampaignMessageDeliveryCalled = true;
        }
    }

    @Override
    public void reportSubscriberDetailsChange(String subscriptionId, SubscriberReportRequest subscriberReportRequest) {
        if (verify()) {
            behavior.reportSubscriberDetailsChange(subscriptionId, subscriberReportRequest);
        }
    }

    @Override
    public void reportChangeMsisdnForSubscriber(String subscriptionId, String msisdn) {
        if (verify()) {
            behavior.reportChangeMsisdnForSubscriber(subscriptionId, msisdn);
        }
    }

    public void setBehavior(ReportingService behavior) {
        this.behavior = behavior;
    }

    public boolean isReportCampaignMessageDeliveryCalled() {
        return reportCampaignMessageDeliveryCalled;
    }

    public void setReportCampaignMessageDeliveryCalled(boolean reportCampaignMessageDeliveryCalled) {
        this.reportCampaignMessageDeliveryCalled = reportCampaignMessageDeliveryCalled;
    }

    private boolean verify() {
        if (behavior == null) {
            System.err.println(String.format("WARNING: %s: You need to set behavior before calling this method. Use setBehavior method.", StubReportingService.class.getCanonicalName()));
            return false;
        }
        return true;
    }
}
