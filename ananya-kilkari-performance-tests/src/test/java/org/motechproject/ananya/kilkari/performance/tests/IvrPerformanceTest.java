package org.motechproject.ananya.kilkari.performance.tests;

import org.apache.commons.lang.RandomStringUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.junit.runner.RunWith;
import org.motechproject.ananya.kilkari.performance.tests.domain.BaseResponse;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.performance.tests.service.SubscriptionService;
import org.motechproject.ananya.kilkari.performance.tests.utils.BasePerformanceTest;
import org.motechproject.ananya.kilkari.performance.tests.utils.HttpUtils;
import org.motechproject.ananya.kilkari.performance.tests.utils.runner.LoadTest;
import org.motechproject.ananya.kilkari.performance.tests.utils.runner.LoadRunner;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;

import java.util.HashMap;
import java.util.Map;


@RunWith(LoadRunner.class)
public class IvrPerformanceTest extends BasePerformanceTest {

    public IvrPerformanceTest(String testName) {
        super(testName);
    }

    @LoadTest(concurrentUsers = 10)
    public void shouldCreateAnIvrSubscription() throws InterruptedException {

        SubscriptionService subscriptionService= new SubscriptionService();
        DateTime beforeTest = DateTime.now();
        String expectedStatus = "PENDING_ACTIVATION";
        Map<String, String> parametersMap = constructParameters();

        BaseResponse baseResponse = HttpUtils.httpGetWithJsonResponse(parametersMap, "subscription");
        assertEquals("SUCCESS", baseResponse.getStatus());

        Subscription subscription = subscriptionService.getSubscriptionData(parametersMap.get("msisdn"), expectedStatus);
        assertNotNull(subscription);
        DateTime afterTest = DateTime.now();
        Period p = new Period(beforeTest, afterTest);
        System.out.println(p.getMillis()+" ms");
    }


    private Map<String, String> constructParameters() {
        Map<String, String> parametersMap = new HashMap<>();
        parametersMap.put("msisdn", "1" + RandomStringUtils.randomNumeric(9));
        parametersMap.put("channel", "IVR");
        parametersMap.put("pack", "bari_kilkari");
        return parametersMap;
    }
}
