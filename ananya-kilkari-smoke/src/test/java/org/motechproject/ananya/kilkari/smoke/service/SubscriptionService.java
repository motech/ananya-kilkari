package org.motechproject.ananya.kilkari.smoke.service;

import org.motechproject.ananya.kilkari.smoke.domain.kilkari.SubscriberResponse;
import org.motechproject.ananya.kilkari.smoke.utils.TimedRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.motechproject.ananya.kilkari.smoke.utils.TestUtils.*;

public class SubscriptionService {
    public SubscriberResponse getSubscriptionData(final String msisdn, final String channel, final String status) throws InterruptedException {
        return new TimedRunner<SubscriberResponse>(5, 1000) {
            @Override
            protected SubscriberResponse run() {
                Map<String, String> parametersMap = new HashMap<>();
                parametersMap.put("msisdn", msisdn);
                parametersMap.put("channel", channel);
                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<String> subscriberResponseResponseEntity = restTemplate.getForEntity(constructUrl(KILKARI_URL, "subscriber", parametersMap), String.class);
                SubscriberResponse subscriberResponse = fromJson(subscriberResponseResponseEntity.getBody().replace("var response = ", ""), SubscriberResponse.class);
                return subscriberResponse.getSubscriptionDetails().isEmpty()
                        || !subscriberResponse.getSubscriptionDetails().get(0).getStatus().equals(status)
                        ? null : subscriberResponse;
            }
        }.executeWithTimeout();
    }
}
