package org.motechproject.ananya.kilkari.performance.tests;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.motechproject.ananya.kilkari.builder.SubscriptionWebRequestBuilder;
import org.motechproject.ananya.kilkari.performance.tests.domain.BaseResponse;
import org.motechproject.ananya.kilkari.performance.tests.service.api.SubscriptionApiService;
import org.motechproject.ananya.kilkari.performance.tests.utils.BasePerformanceTest;
import org.motechproject.ananya.kilkari.performance.tests.utils.HttpUtils;
import org.motechproject.ananya.kilkari.request.SubscriptionWebRequest;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.service.request.Location;
import org.motechproject.performance.tests.LoadPerf;
import org.motechproject.performance.tests.LoadRunner;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static org.motechproject.ananya.kilkari.performance.tests.utils.TestUtils.getRandomMsisdn;

@RunWith(LoadRunner.class)
public class SubscriptionPerformanceTest extends BasePerformanceTest {

    private List<Location> locationList;

    @Autowired
    private HttpUtils httpUtils;

    @Autowired
    private SubscriptionApiService subscriptionApiService;

    private Random random = new Random();

    public SubscriptionPerformanceTest(String testName) {
        super(testName);
    }

    @Before
    public void setUp() {
        setupLocations();
    }

    private void setupLocations() {
        locationList = new ArrayList<Location>();
        locationList.add(new Location("Bihar", "Begusarai", "Bachhwara", "Kadarabad"));
        locationList.add(new Location("Bihar", "Begusarai", "Bachhwara", "Godhana"));
        locationList.add(new Location("Bihar", "Begusarai", "Bakhri", "Aakha"));
        locationList.add(new Location("Bihar", "Begusarai", "Bakhri", "Bagban"));
        locationList.add(new Location("Bihar", "Begusarai", "Bakhri", "Bahuwara"));
        locationList.add(new Location("Bihar", "Begusarai", "Bakhri", "Bakhari East"));
        locationList.add(new Location("Bihar", "Begusarai", "Begusarai", "Suja"));
        locationList.add(new Location("Bihar", "Begusarai", "Begusarai", "Ulao"));
        locationList.add(new Location("Bihar", "Begusarai", "Bhagwanpur", "Banwaripur"));
        locationList.add(new Location("Bihar", "Begusarai", "Bhagwanpur", "Bhitsari"));
        locationList.add(new Location("Bihar", "Begusarai", "Bhagwanpur", "Chandaur"));
        locationList.add(new Location("Bihar", "Begusarai", "Bhagwanpur", "Damodarpur"));
    }

    @LoadPerf(concurrentUsers = 100)
    public void shouldCreateAnIvrSubscription() throws InterruptedException {
        String expectedStatus = "PENDING_ACTIVATION";
        Map<String, String> parametersMap = constructParameters();

        BaseResponse baseResponse = httpUtils.httpGetKilkariWithJsonResponse(parametersMap, "subscription");
        assertEquals("SUCCESS", baseResponse.getStatus());

        Subscription subscription = subscriptionApiService.getSubscriptionData(parametersMap.get("msisdn"), expectedStatus);
        assertNotNull(subscription);
    }

    @LoadPerf(concurrentUsers = 5)
    public void shouldCreateIvrSubscriptionsForBulkUsers() throws InterruptedException {
        for (int i = 0; i < 100; i++) {
            shouldCreateAnIvrSubscription();
        }
    }

    @LoadPerf(concurrentUsers = 300)
    public void shouldCreateACallCenterSubscription() throws InterruptedException {
        Map<String, String> parametersMap = constructParameters();

        SubscriptionWebRequest subscriptionWebRequest = getSubscriptionWebRequest();

        BaseResponse baseResponse = httpUtils.httpPostKilkariWithJsonResponse(parametersMap, subscriptionWebRequest, "subscription");
        assertEquals("SUCCESS", baseResponse.getStatus());
    }

    /*
     * randomly returns regular, early or late subscription
     * regular subscription - 70%, early subscription 15%, late subscription 15%
     */
    private SubscriptionWebRequest getSubscriptionWebRequest() {
        double probability = Math.random();
        if (probability <= 0.7) return getRegularSubscription();
        if (probability > 0.7 && probability < 0.85) return getEarlySubscription();
        return getLateSubscription();
    }

    private SubscriptionWebRequest getRegularSubscription() {
        Location location = locationList.get(random.nextInt(locationList.size()));
        return new SubscriptionWebRequestBuilder()
                .withDefaults()
                .withLocation(location.getState(), location.getDistrict(), location.getBlock(), location.getPanchayat())
                .withMsisdn(getRandomMsisdn())
                .build();
    }

    private SubscriptionWebRequest getEarlySubscription() {
        SubscriptionWebRequest earlySubscription = getRegularSubscription();
        earlySubscription.setDateOfBirth(DateTime.now().plusMonths(3).plusDays(random.nextInt(100)).toString("dd-MM-yyyy"));
        return earlySubscription;
    }

    private SubscriptionWebRequest getLateSubscription() {
        SubscriptionWebRequest lateSubscription = getRegularSubscription();
        // Date of Birth range is : -30 - DateTime.now - +30
        lateSubscription.setDateOfBirth(DateTime.now().plusDays(-30 + random.nextInt(60)).toString("dd-MM-yyyy"));
        return lateSubscription;
    }

    private Map<String, String> constructParameters() {
        Map<String, String> parametersMap = new HashMap<>();
        parametersMap.put("msisdn", getRandomMsisdn());
        parametersMap.put("channel", "IVR");
        parametersMap.put("pack", "bari_kilkari");
        return parametersMap;
    }
}
