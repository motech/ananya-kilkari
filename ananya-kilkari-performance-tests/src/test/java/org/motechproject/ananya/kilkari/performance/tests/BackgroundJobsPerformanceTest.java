package org.motechproject.ananya.kilkari.performance.tests;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.kilkari.performance.tests.domain.BaseResponse;
import org.motechproject.ananya.kilkari.performance.tests.service.SubscriptionService;
import org.motechproject.ananya.kilkari.performance.tests.utils.HttpUtils;
import org.motechproject.ananya.kilkari.performance.tests.utils.TimedRunner;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;


public class BackgroundJobsPerformanceTest {

    private final static int numberOfSubscribers = 5000;

    @Before
    public void setUp() {
        for (int i = 0; i < numberOfSubscribers; i++) {
            createASubscription();
        }
        assertSubscriptionCreation();
    }


    @Test
    public void should() {

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

    private void createASubscription() {
        Map<String, String> parametersMap = constructParameters();

        BaseResponse baseResponse = HttpUtils.httpGetWithJsonResponse(parametersMap, "subscription");
        assertEquals("SUCCESS", baseResponse.getStatus());
    }

    private String getRandomMsisdn() {
        return "9" + RandomStringUtils.randomNumeric(9);
    }

    private Map<String, String> constructParameters() {
        Map<String, String> parametersMap = new HashMap<>();
        parametersMap.put("msisdn", getRandomMsisdn());
        parametersMap.put("channel", "IVR");
        parametersMap.put("pack", "bari_kilkari");
        return parametersMap;

    }

}
