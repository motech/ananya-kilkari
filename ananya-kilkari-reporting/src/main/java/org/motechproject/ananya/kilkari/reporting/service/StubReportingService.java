package org.motechproject.ananya.kilkari.reporting.service;

import org.motechproject.ananya.kilkari.reporting.domain.CampaignMessageDeliveryReportRequest;
import org.motechproject.ananya.kilkari.reporting.domain.SubscriberLocation;
import org.motechproject.ananya.kilkari.reporting.domain.SubscriptionCreationReportRequest;
import org.motechproject.ananya.kilkari.reporting.domain.SubscriptionStateChangeReportRequest;
import org.motechproject.ananya.kilkari.reporting.profile.TestProfile;
import org.springframework.stereotype.Service;

@Service
@TestProfile
public class StubReportingService implements ReportingService {

    private ReportingService behavior;

    @Override
    public SubscriberLocation getLocation(String district, String block, String panchayat) {
        if (verify()) {
            return behavior.getLocation(district, block, panchayat);
        }
        return null;
    }

    @Override
    public void reportSubscriptionCreation(SubscriptionCreationReportRequest subscriptionCreationReportRequest) {
        if (verify()) {
            behavior.reportSubscriptionCreation(subscriptionCreationReportRequest);
        }
    }

    @Override
    public void reportSubscriptionStateChange(SubscriptionStateChangeReportRequest subscriptionStateChangeReportRequest) {
        if (verify()) {
            behavior.reportSubscriptionStateChange(subscriptionStateChangeReportRequest);
        }
    }

    @Override
    public void reportCampaignMessageDeliveryStatus(CampaignMessageDeliveryReportRequest campaignMessageDeliveryReportRequest) {
        if (verify()) {
            behavior.reportCampaignMessageDeliveryStatus(campaignMessageDeliveryReportRequest);
        }
    }

    public void setBehavior(ReportingService behavior) {
        this.behavior = behavior;
    }

    private boolean verify() {
        if (behavior == null) {
            System.err.println(String.format("WARNING: %s: You need to set behavior before calling this method. Use setBehavior method.", StubReportingService.class.getCanonicalName()));
            return false;
        }
        return true;
    }
}
