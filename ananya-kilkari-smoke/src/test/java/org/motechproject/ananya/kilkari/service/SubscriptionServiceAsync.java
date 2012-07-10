package org.motechproject.ananya.kilkari.service;

import org.motechproject.ananya.kilkari.domain.kilkari.SubscriberResponse;
import org.motechproject.ananya.kilkari.utils.TimedRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.motechproject.ananya.kilkari.utils.TestUtils.*;

public class SubscriptionServiceAsync {
    public SubscriberResponse getSubscriptionData(final String msisdn, final String channel) throws InterruptedException {
        return new TimedRunner(5, 1000) {
            @Override
            protected SubscriberResponse run() {
                Map<String, String> parametersMap = new HashMap<>();
                parametersMap.put("msisdn", msisdn);
                parametersMap.put("channel", channel);
                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<String> subscriberResponseResponseEntity = restTemplate.getForEntity(constructUrl(KILKARI_URL, "subscriber", parametersMap), String.class);
                SubscriberResponse subscriberResponse = fromJson(subscriberResponseResponseEntity.getBody().replace("var response = ", ""), SubscriberResponse.class);
                return subscriberResponse.getSubscriptionDetails().isEmpty()
                        || !subscriberResponse.getSubscriptionDetails().get(0).getStatus().equals("PENDING_ACTIVATION")
                        ? null : subscriberResponse;
            }
        }.executeWithTimeout();
    }
}
