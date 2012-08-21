package org.motechproject.ananya.kilkari.performance.tests.service;

import org.motechproject.ananya.kilkari.performance.tests.utils.BaseConfiguration;
import org.motechproject.ananya.kilkari.performance.tests.utils.BasePerformanceTest;
import org.motechproject.ananya.kilkari.performance.tests.utils.ContextUtils;
import org.motechproject.ananya.kilkari.performance.tests.utils.TimedRunner;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;

import java.util.List;

public class SubscriptionService {

    public Subscription getSubscriptionData(final String msisdn, final String status) throws InterruptedException {
        return new TimedRunner<Subscription>(100, 1000) {
            @Override
            protected Subscription run() {

                List<Subscription> subscriptionList = ContextUtils.getConfiguration().getAllSubscriptions().findByMsisdn(msisdn);
                if(subscriptionList.size()!=0 && subscriptionList.get(0).getStatus().name().equals(status))
                    return subscriptionList.get(0);
                return null;
            }
        }.executeWithTimeout();
    }
}
