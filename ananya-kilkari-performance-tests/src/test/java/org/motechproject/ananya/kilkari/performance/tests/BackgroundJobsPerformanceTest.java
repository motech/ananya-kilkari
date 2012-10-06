package org.motechproject.ananya.kilkari.performance.tests;

import org.junit.runner.RunWith;
import org.motechproject.ananya.kilkari.performance.tests.service.api.SubscriptionApiService;
import org.motechproject.ananya.kilkari.performance.tests.utils.BasePerformanceTest;
import org.motechproject.ananya.kilkari.performance.tests.utils.TimedRunner;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionStatus;
import org.motechproject.performance.tests.LoadPerfBefore;
import org.motechproject.performance.tests.LoadPerfStaggered;
import org.motechproject.performance.tests.LoadRunner;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;


@RunWith(LoadRunner.class)
public class BackgroundJobsPerformanceTest extends BasePerformanceTest {

    private final static int numberOfSubscribers = 25000;
    private static List createdSubscriptionsList = new ArrayList<Subscription>();
    @Autowired
    private SubscriptionApiService subscriptionApiService;
    private volatile static int index=-1;

    public BackgroundJobsPerformanceTest(String name) {
        super(name);
    }

    @LoadPerfBefore(priority = 1,concurrentUsers = 100)
    public void firstBefore() {
        for (int i = 0; i < 250; i++) {
            subscriptionApiService.createASubscription();
        }
    }

    @LoadPerfBefore(priority = 2,concurrentUsers = 1)
    public void secondBefore() {
        assertSubscriptionCreation();
        createdSubscriptionsList = subscriptionApiService.getAll();
    }

    @LoadPerfStaggered(totalNumberOfUsers = numberOfSubscribers,minMaxRandomBatchSizes = {"5","10"}, minDelayInMillis = 1000,delayVariation = 120000)
    public void shouldActivateBulkSubscriptionsOverADay() {
        Subscription subscription = getASubscriptionToBeActivated();
        subscriptionApiService.activate(subscription);

    }

    public static synchronized int getNextIndex(){
        return ++index;
    }

    private Subscription getASubscriptionToBeActivated() {
            return (Subscription) createdSubscriptionsList.get(BackgroundJobsPerformanceTest.getNextIndex());
    }


    public void assertSubscriptionCreation() {
        new TimedRunner<Boolean>(10, 1000) {
            @Override
            protected Boolean run() {
                boolean isComplete = true;
                List<Subscription> subscriptions = subscriptionApiService.getAll();
                if (numberOfSubscribers != subscriptions.size()) return null;
                for (Subscription subscription : subscriptions) {
                    if (!SubscriptionStatus.PENDING_ACTIVATION.equals(subscription.getStatus())) {
                        isComplete = false;
                        break;
                    }
                }
                return isComplete ? isComplete : null;

            }
        }.executeWithTimeout();

    }


}
