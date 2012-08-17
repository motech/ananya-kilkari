package org.motechproject.ananya.kilkari.performance.tests.testThreads;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.motechproject.ananya.kilkari.performance.tests.domain.BaseResponse;
import org.motechproject.ananya.kilkari.performance.tests.domain.SubscriberResponse;
import org.motechproject.ananya.kilkari.performance.tests.utils.AutowiredProperties;
import org.motechproject.ananya.kilkari.performance.tests.utils.SpringIntegrationTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.motechproject.ananya.kilkari.performance.tests.utils.HttpUtils.constructUrl;
import static org.motechproject.ananya.kilkari.performance.tests.utils.HttpUtils.fromJsonWithResponse;

public class IvrPerformanceTestThread extends SpringIntegrationTest {

    private RestTemplate restTemplate;

    public IvrPerformanceTestThread(String testName) {
        super(testName);
    }

    @Before
    public void setUp() throws SQLException {
        restTemplate = new RestTemplate();
    }

    public void shouldCreateAnIvrSubscription() throws InterruptedException {

        String channel = "IVR";
        String msisdn = "1" + RandomStringUtils.randomNumeric(9);
        String pack = "bari_kilkari";
        String expectedStatus = "Pending Subscription";

        Map<String, String> parametersMap = new HashMap<>();
        parametersMap.put("msisdn", msisdn);
        parametersMap.put("channel", channel);
        parametersMap.put("pack", pack);

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(constructUrl(baseUrl(), "subscription", parametersMap), String.class);

        BaseResponse baseResponse = fromJsonWithResponse(responseEntity.getBody(), BaseResponse.class);
        assertEquals("SUCCESS", baseResponse.getStatus());

        SubscriberResponse response = AutowiredProperties.subscriptionService.getSubscriptionData(msisdn, channel, expectedStatus);
        assertEquals(1, response.getSubscriptionDetails().size());
    }
}
