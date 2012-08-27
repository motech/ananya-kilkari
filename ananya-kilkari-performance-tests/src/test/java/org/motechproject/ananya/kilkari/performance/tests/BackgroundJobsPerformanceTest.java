package org.motechproject.ananya.kilkari.performance.tests;

import org.apache.commons.lang.math.RandomUtils;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.kilkari.performance.tests.service.SubscriptionService;
import org.motechproject.ananya.kilkari.performance.tests.utils.TimedRunner;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionStatus;

import java.util.List;


public class BackgroundJobsPerformanceTest {

    private final static int numberOfSubscribers = 25000;
    private SubscriptionService subscriptionService = new SubscriptionService();

    @Before
    public void setUp() {
        for (int i = 0; i < numberOfSubscribers; i++) {
            subscriptionService.createASubscription();
        }
        assertSubscriptionCreation();
    }

    @Test
    public void shouldActivateBulkSubscriptionsOverADay() throws InterruptedException {
        List<Subscription> subscriptions = subscriptionService.getAll();
        while (!subscriptions.isEmpty()) {
            int waitTimeInMillis = RandomUtils.nextInt(120000);
            System.out.println("Waiting for " + waitTimeInMillis + "millis.");
            Thread.sleep(waitTimeInMillis);
            activateABatch(subscriptions);
        }
    }

    private int getRandom(int min, int max) {
        return min + RandomUtils.nextInt(max);
    }

    private void activateABatch(List<Subscription> subscriptions) {
        int numberOfThreads = getRandom(5, 5);
        int endPointer = numberOfThreads > subscriptions.size() ? subscriptions.size() : numberOfThreads;
        List<Subscription> subscriptionList = subscriptions.subList(0, endPointer);
        System.out.println("Executing batch of : " + numberOfThreads);
        batchActivate(subscriptionList);
        subscriptions.removeAll(subscriptionList);
        System.out.println("Remaining Subscriptions : " + subscriptions.size());
    }

    private void batchActivate(List<Subscription> subscriptionList) {
        for (final Subscription subscription : subscriptionList) {
            new Thread() {
                @Override
                public void run() {
                    subscriptionService.activate(subscription);
                }
            }.run();
        }
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
