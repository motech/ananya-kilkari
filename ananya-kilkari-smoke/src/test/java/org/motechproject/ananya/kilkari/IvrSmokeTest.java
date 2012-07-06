package org.motechproject.ananya.kilkari;

import com.google.gson.Gson;
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

public class IvrSmokeTest {
    RestTemplate restTemplate;
    private static final String KILKARI_URL = "http://localhost:8080/ananya-kilkari";

    @Before
    public void setUp() {
        restTemplate = new RestTemplate();
    }

    @Test(timeout = 10000)
    public void shouldPostHttpRequestAndMakeVerifyEntriesInReportDbAndCouchDb() throws InterruptedException {
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

        SubscriberResponse response = getReportDataAsync(msisdn, channel);
        assertEquals(1, response.getSubscriptionDetails().size());
        SubscriptionDetails subscriptionDetails = response.getSubscriptionDetails().get(0);
        assertEquals(pack, subscriptionDetails.getPack());
        assertEquals("PENDING_ACTIVATION", subscriptionDetails.getStatus());
    }

    private SubscriberResponse getReportDataAsync(String msisdn, String channel) throws InterruptedException {
        SubscriberResponse response;
        Map<String, String> parametersMap = new HashMap<>();
        parametersMap.put("msisdn", msisdn);
        parametersMap.put("channel", channel);

        while (true) {
            ResponseEntity<String> subscriberResponseResponseEntity = restTemplate.getForEntity(constructUrl(KILKARI_URL, "subscriber", parametersMap), String.class);
            response = fromJson(subscriberResponseResponseEntity.getBody().replace("var response = ", ""), SubscriberResponse.class);
            if (response != null)
                return response;
            Thread.sleep(100);
        }
    }

    private String constructUrl(String url, String path, Map<String, String> parametersMap) {
        url += "/" + path + "?";
        for (String key : parametersMap.keySet()) {
            url += key + "=" + parametersMap.get(key) + "&";
        }
        return url;
    }

    private <T> T fromJson(String jsonString, Class<T> subscriberResponseClass) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, subscriberResponseClass);
    }
}