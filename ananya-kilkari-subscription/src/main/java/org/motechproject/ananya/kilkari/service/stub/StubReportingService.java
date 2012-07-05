package org.motechproject.ananya.kilkari.service.stub;

import org.motechproject.ananya.kilkari.domain.SubscriberLocation;
import org.motechproject.ananya.kilkari.domain.SubscriptionCreationReportRequest;
import org.motechproject.ananya.kilkari.domain.SubscriptionStateChangeReportRequest;
import org.motechproject.ananya.kilkari.profile.Test;
import org.motechproject.ananya.kilkari.service.ReportingService;
import org.springframework.stereotype.Service;

@Service
@Test
public class StubReportingService implements ReportingService {

    private ReportingService behavior;

    @Override
    public void createSubscription(SubscriptionCreationReportRequest subscriptionCreationReportRequest) {
        if (verify()) {
            behavior.createSubscription(subscriptionCreationReportRequest);
        }
    }

    @Override
    public void updateSubscriptionStateChange(SubscriptionStateChangeReportRequest subscriptionStateChangeReportRequest) {
        if (verify()) {
            behavior.updateSubscriptionStateChange(subscriptionStateChangeReportRequest);
        }
    }

    @Override
    public SubscriberLocation getLocation(String district, String block, String panchayat) {
        if (verify()) {
            return behavior.getLocation(district, block, panchayat);
        }
        return null;
    }

    public void setBehavior(ReportingService behavior) {
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
