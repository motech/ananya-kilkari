package org.motechproject.ananya.kilkari.performance.tests.service;

import org.motechproject.ananya.kilkari.performance.tests.domain.Subscription;
import org.motechproject.ananya.kilkari.performance.tests.utils.BaseConfiguration;
import org.motechproject.ananya.kilkari.performance.tests.utils.TimedRunner;

import java.util.List;

public class SubscriptionService {

    public Subscription getSubscriptionData(final String msisdn, final String status) throws InterruptedException {
        return new TimedRunner<Subscription>(100, 1000) {
            @Override
            protected Subscription run() {

                List<Subscription> subscriptionList = BaseConfiguration.getAllSubscriptions().findByMsisdn(msisdn);
                if(subscriptionList.size()!=0 && subscriptionList.get(0).getStatus().equals(status))
                    return subscriptionList.get(0);
                return null;
            }
        }.executeWithTimeout();
    }
}
