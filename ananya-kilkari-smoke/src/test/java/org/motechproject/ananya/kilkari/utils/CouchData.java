package org.motechproject.ananya.kilkari.utils;

import org.motechproject.ananya.kilkari.response.SubscriberResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.motechproject.ananya.kilkari.utils.TestUtils.KILKARI_URL;
import static org.motechproject.ananya.kilkari.utils.TestUtils.constructUrl;
import static org.motechproject.ananya.kilkari.utils.TestUtils.fromJson;

public class CouchData {
    public static SubscriberResponse getSubscriptionDataFromCouchAsync(String msisdn, String channel) throws InterruptedException {
        SubscriberResponse response;
        RestTemplate restTemplate = new RestTemplate();

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

}
