package org.motechproject.ananya.kilkari;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.kilkari.response.BaseResponse;
import org.motechproject.ananya.kilkari.response.SubscriberResponse;
import org.motechproject.ananya.kilkari.response.SubscriptionDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.motechproject.ananya.kilkari.utils.CouchData.getSubscriptionDataFromCouchAsync;
import static org.motechproject.ananya.kilkari.utils.TestUtils.*;

public class IvrSmokeTest {
    RestTemplate restTemplate;

    @Before
    public void setUp() {
        restTemplate = new RestTemplate();
    }

    @Test(timeout = 10000)
    public void shouldPostHttpRequestAndVerifyEntriesInReportDbAndCouchDb() throws InterruptedException {
        String channel = "ivr";
        String msisdn = "9000000001";
        String pack = "TWELVE_MONTHS";

        Map<String, String> parametersMap = new HashMap<>();
        parametersMap.put("msisdn", msisdn);
        parametersMap.put("channel", channel);
        parametersMap.put("pack", pack);

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(constructUrl(KILKARI_URL, "subscription", parametersMap), String.class);

        BaseResponse baseResponse = fromJson(responseEntity.getBody().replace("var response = ", ""), BaseResponse.class);
        assertEquals("SUCCESS", baseResponse.getStatus());

        SubscriberResponse response = getSubscriptionDataFromCouchAsync(msisdn, channel);
        assertEquals(1, response.getSubscriptionDetails().size());
        SubscriptionDetails subscriptionDetails = response.getSubscriptionDetails().get(0);
        assertEquals(pack, subscriptionDetails.getPack());
        assertEquals("PENDING_ACTIVATION", subscriptionDetails.getStatus());
    }
}