package org.motechproject.ananya.kilkari.reporting.repository;

import org.motechproject.ananya.kilkari.reporting.domain.CampaignMessageDeliveryReportRequest;
import org.motechproject.ananya.kilkari.reporting.domain.SubscriberLocation;
import org.motechproject.ananya.kilkari.reporting.domain.SubscriptionCreationReportRequest;
import org.motechproject.ananya.kilkari.reporting.domain.SubscriptionStateChangeReportRequest;
import org.motechproject.ananya.kilkari.reporting.profile.TestProfile;
import org.springframework.stereotype.Service;

@Service
@TestProfile
public class StubReportingGateway implements ReportingGateway {

    private ReportingGateway behavior;
    private boolean isCreateSubscriptionCalled;
    private boolean isReportCampaignMessageDeliveryCalled;

    public boolean isReportCampaignMessageDeliveryCalled() {
        return isReportCampaignMessageDeliveryCalled;
    }

    public void setReportCampaignMessageDeliveryCalled(boolean reportCampaignMessageDeliveryCalled) {
        isReportCampaignMessageDeliveryCalled = reportCampaignMessageDeliveryCalled;
    }

    public boolean isUpdateSubscriptionCalled() {
        return isUpdateSubscriptionCalled;
    }

    public void setUpdateSubscriptionCalled(boolean updateSubscriptionCalled) {
        isUpdateSubscriptionCalled = updateSubscriptionCalled;
    }

    private boolean isUpdateSubscriptionCalled;

    public boolean isCreateSubscriptionCalled() {
        return isCreateSubscriptionCalled;
    }

    public void setCreateSubscriptionCalled(boolean createSubscriptionCalled) {
        isCreateSubscriptionCalled = createSubscriptionCalled;
    }

    @Override
    public void createSubscription(SubscriptionCreationReportRequest subscriptionCreationReportRequest) {
        if (verify()) {
            behavior.createSubscription(subscriptionCreationReportRequest);
            isCreateSubscriptionCalled = true;
        }
    }

    @Override
    public void updateSubscriptionStateChange(SubscriptionStateChangeReportRequest subscriptionStateChangeReportRequest) {
        if (verify()) {
            behavior.updateSubscriptionStateChange(subscriptionStateChangeReportRequest);
            isUpdateSubscriptionCalled = true;
        }
    }

    @Override
    public SubscriberLocation getLocation(String district, String block, String panchayat) {
        if (verify()) {
            return behavior.getLocation(district, block, panchayat);
        }
        return null;
    }

    @Override
    public void reportCampaignMessageDelivery(CampaignMessageDeliveryReportRequest campaignMessageDeliveryReportRequest) {
        if (verify()) {
            behavior.reportCampaignMessageDelivery(campaignMessageDeliveryReportRequest);
            isReportCampaignMessageDeliveryCalled = true;
        }
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
