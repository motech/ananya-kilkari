package org.motechproject.ananya.kilkari.smoke.service;

import org.motechproject.ananya.kilkari.smoke.SmokeConfig;
import org.motechproject.ananya.kilkari.smoke.domain.SubscriberResponse;
import org.motechproject.ananya.kilkari.smoke.utils.TimedRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.motechproject.ananya.kilkari.smoke.utils.TestUtils.constructUrl;
import static org.motechproject.ananya.kilkari.smoke.utils.TestUtils.fromJsonWithResponse;

@Component
public class SubscriptionService {

    private SmokeConfig smokeConfig;

    @Autowired
    public SubscriptionService(SmokeConfig smokeConfig) {
        this.smokeConfig = smokeConfig;
    }

    public SubscriberResponse getSubscriptionData(final String msisdn, final String channel, final String status) throws InterruptedException {
        return new TimedRunner<SubscriberResponse>(5, 1000) {
            @Override
            protected SubscriberResponse run() {
                Map<String, String> parametersMap = new HashMap<>();
                parametersMap.put("msisdn", msisdn);
                parametersMap.put("channel", channel);
                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<String> subscriberResponseResponseEntity = restTemplate.getForEntity(constructUrl(smokeConfig.baseUrl(), "subscriber", parametersMap), String.class);
                SubscriberResponse subscriberResponse = fromJsonWithResponse(subscriberResponseResponseEntity.getBody(), SubscriberResponse.class);
                return subscriberResponse.getSubscriptionDetails().isEmpty()
                        || !subscriberResponse.getSubscriptionDetails().get(0).getStatus().equals(status)
                        ? null : subscriberResponse;
            }
        }.executeWithTimeout();
    }
}
