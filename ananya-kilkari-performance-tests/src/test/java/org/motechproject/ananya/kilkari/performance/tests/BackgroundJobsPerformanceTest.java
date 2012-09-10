package org.motechproject.ananya.kilkari.performance.tests;

import org.junit.runner.RunWith;
import org.motechproject.ananya.kilkari.performance.tests.service.SubscriptionService;
import org.motechproject.ananya.kilkari.performance.tests.utils.BasePerformanceTest;
import org.motechproject.ananya.kilkari.performance.tests.utils.TimedRunner;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionStatus;
import org.motechproject.performance.tests.LoadPerfBefore;
import org.motechproject.performance.tests.LoadPerfStaggered;
import org.motechproject.performance.tests.LoadRunner;

import java.util.ArrayList;
import java.util.List;


@RunWith(LoadRunner.class)
public class BackgroundJobsPerformanceTest extends BasePerformanceTest {

    private final static int numberOfSubscribers = 25000;
    private static List createdSubscriptionsList = new ArrayList<Subscription>();
    private SubscriptionService subscriptionService = new SubscriptionService();
    private static int index=-1;
    private static String lockName="lock";

    public BackgroundJobsPerformanceTest(String name) {
        super(name);
    }

    @LoadPerfBefore(priority = 1,concurrentUsers = 100)
    public void firstBefore() {
        for (int i = 0; i < 250; i++) {
            subscriptionService.createASubscription();
        }
    }

    @LoadPerfBefore(priority = 2,concurrentUsers = 1)
    public void secondBefore() {
        assertSubscriptionCreation();
        createdSubscriptionsList = subscriptionService.getAll();
    }

    @LoadPerfStaggered(totalNumberOfUsers = numberOfSubscribers,minMaxRandomBatchSizes = {"5","10"}, minDelayInMillis = 1000,delayVariation = 120000)
    public void shouldActivateBulkSubscriptionsOverADay() {
        Subscription subscription = getASubscriptionToBeActivated();
        subscriptionService.activate(subscription);

    }

    public int getIndex(){
        synchronized (lockName){
        return ++index;
        }
        
    }
    private Subscription getASubscriptionToBeActivated() {
            return (Subscription) createdSubscriptionsList.get(getIndex());
    }


    public void assertSubscriptionCreation() {
        new TimedRunner<Boolean>(10, 1000) {
            @Override
            protected Boolean run() {
                boolean isComplete = true;
                SubscriptionService subscriptionService = new SubscriptionService();
                List<Subscription> subscriptions = subscriptionService.getAll();
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
