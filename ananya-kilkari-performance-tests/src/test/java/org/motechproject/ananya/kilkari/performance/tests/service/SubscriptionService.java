package org.motechproject.ananya.kilkari.performance.tests.service;

import org.motechproject.ananya.kilkari.performance.tests.domain.SubscriberResponse;
import org.motechproject.ananya.kilkari.performance.tests.utils.HttpUtils;
import org.motechproject.ananya.kilkari.performance.tests.utils.TimedRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Component
public class SubscriptionService {

    private Properties performanceProperties;

    @Autowired
    public SubscriptionService(Properties performanceProperties) {

        this.performanceProperties = performanceProperties;
    }

    public SubscriberResponse getSubscriptionData(final String msisdn, final String channel, final String status) throws InterruptedException {
        return new TimedRunner<SubscriberResponse>(100, 1000) {
            @Override
            protected SubscriberResponse run() {
                Map<String, String> parametersMap = new HashMap<>();
                parametersMap.put("msisdn", msisdn);
                parametersMap.put("channel", channel);
                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<String> subscriberResponseResponseEntity = restTemplate.getForEntity(constructUrl(parametersMap), String.class);
                SubscriberResponse subscriberResponse = HttpUtils.fromJsonWithResponse(subscriberResponseResponseEntity.getBody(), SubscriberResponse.class);
                return subscriberResponse.getSubscriptionDetails().isEmpty()
                        || !subscriberResponse.getSubscriptionDetails().get(0).getStatus().equals(status)
                        ? null : subscriberResponse;
            }
        }.executeWithTimeout();
    }

    private String constructUrl(Map<String, String> parametersMap) {
        return HttpUtils.constructUrl(getBaseUrl(), "subscriber", parametersMap);
    }

    private String getBaseUrl() {
        return performanceProperties.getProperty("baseurl");
    }
}
