package org.motechproject.ananya.kilkari.reporting.gateway;

import org.motechproject.ananya.kilkari.reporting.domain.SubscriberLocation;
import org.motechproject.ananya.kilkari.reporting.domain.SubscriptionCreationReportRequest;
import org.motechproject.ananya.kilkari.reporting.domain.SubscriptionStateChangeReportRequest;
import org.motechproject.ananya.kilkari.reporting.gateway.ReportingGateway;
import org.motechproject.ananya.kilkari.reporting.profile.TestProfile;
import org.springframework.stereotype.Service;

@Service
@TestProfile
public class StubReportingGateway implements ReportingGateway {

    private ReportingGateway behavior;
    private boolean isCreateSubscriptionCalled;

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

    public void setBehavior(ReportingGateway behavior) {
        this.behavior = behavior;
    }

    private boolean verify() {
        if (behavior == null) {
            System.err.println("WARNING: You need to set behavior before calling this method. Use setBehavior method.");
            return false;
        }
        return true;
    }
}
