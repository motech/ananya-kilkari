package org.motechproject.ananya.kilkari.service;

import org.motechproject.ananya.kilkari.domain.SubscriptionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Properties;

@Service
public class OnMobileSubscriptionService {

    public static final String ACTIVATE_SUBSCRIPTION_PATH = "ActivateSubscription";
    private RestTemplate restTemplate;
    private Properties kilkariProperties;

    @Autowired
    public OnMobileSubscriptionService(@Qualifier("kilkariRestTemplate") RestTemplate restTemplate, @Qualifier("kilkariProperties") Properties kilkariProperties) {
        this.restTemplate = restTemplate;
        this.kilkariProperties = kilkariProperties;
    }

    public void activateSubscription(SubscriptionRequest subscriptionRequest) {
        String baseUrl = kilkariProperties.getProperty("omsm.base.url");
        String url = (baseUrl.endsWith("/")) ? String.format("%s%s", baseUrl, ACTIVATE_SUBSCRIPTION_PATH) : String.format("%s/%s", baseUrl, ACTIVATE_SUBSCRIPTION_PATH);
        String referenceId = kilkariProperties.getProperty("omsm.reference.id");
        String username = kilkariProperties.getProperty("omsm.username");
        String password = kilkariProperties.getProperty("omsm.password");

        HashMap<String, String> urlVariables = new HashMap<>();
        urlVariables.put("msisdn", subscriptionRequest.getMsisdn());
        urlVariables.put("srvkey", subscriptionRequest.getPack());
        urlVariables.put("mode", subscriptionRequest.getChannel());
        urlVariables.put("refid", referenceId);
        urlVariables.put("user", username);
        urlVariables.put("pass", password);
        restTemplate.getForEntity(url, String.class, urlVariables);
    }
}
