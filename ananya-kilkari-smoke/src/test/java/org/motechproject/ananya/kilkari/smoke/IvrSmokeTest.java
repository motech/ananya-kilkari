package org.motechproject.ananya.kilkari.smoke;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.kilkari.smoke.domain.BaseResponse;
import org.motechproject.ananya.kilkari.smoke.domain.SubscriberResponse;
import org.motechproject.ananya.kilkari.smoke.domain.SubscriptionDetails;
import org.motechproject.ananya.kilkari.smoke.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.motechproject.ananya.kilkari.smoke.utils.TestUtils.constructUrl;
import static org.motechproject.ananya.kilkari.smoke.utils.TestUtils.fromJsonWithResponse;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationKilkariSmokeContext.xml")
public class IvrSmokeTest extends BaseSmokeTest {
    @Autowired
    private SubscriptionService subscriptionService;
    private RestTemplate restTemplate;

    @Before
    public void setUp() throws SQLException {
        restTemplate = new RestTemplate();
    }

    @Test
    public void shouldPostHttpRequestAndVerifyEntriesInReportDbAndCouchDb() throws InterruptedException, SQLException {
        String channel = "IVR";
        String msisdn = "9000000001";
        String pack = "bari_kilkari";
        String expectedStatus = "Pending Subscription";

        Map<String, String> parametersMap = new HashMap<>();
        parametersMap.put("msisdn", msisdn);
        parametersMap.put("channel", channel);
        parametersMap.put("pack", pack);

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(constructUrl(baseUrl(), "subscription", parametersMap), String.class);

        BaseResponse baseResponse = fromJsonWithResponse(responseEntity.getBody(), BaseResponse.class);
        assertEquals("SUCCESS", baseResponse.getStatus());

        SubscriberResponse response = subscriptionService.getSubscriptionData(msisdn, channel, expectedStatus);
        assertEquals(1, response.getSubscriptionDetails().size());
        assertKilkariData(pack, expectedStatus, response);
    }

    private void assertKilkariData(String pack, String expectedStatus, SubscriberResponse response) {
        SubscriptionDetails subscriptionDetails = response.getSubscriptionDetails().get(0);
        assertEquals(pack.toUpperCase(), subscriptionDetails.getPack().toUpperCase());
        assertEquals(expectedStatus, subscriptionDetails.getStatus());
    }
}