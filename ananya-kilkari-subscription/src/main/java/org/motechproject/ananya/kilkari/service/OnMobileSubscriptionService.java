package org.motechproject.ananya.kilkari.service;

import org.apache.log4j.Logger;
import org.motechproject.ananya.kilkari.domain.SubscriptionActivationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Properties;

@Service
public class OnMobileSubscriptionService {

    public static final String ACTIVATE_SUBSCRIPTION_PATH = "ActivateSubscription";
    private RestTemplate restTemplate;
    private Properties kilkariProperties;
    private Logger logger = Logger.getLogger(OnMobileSubscriptionService.class);

    @Autowired
    public OnMobileSubscriptionService(@Qualifier("kilkariRestTemplate") RestTemplate restTemplate, @Qualifier("kilkariProperties") Properties kilkariProperties) {
        this.restTemplate = restTemplate;
        this.kilkariProperties = kilkariProperties;
    }

    public void activateSubscription(SubscriptionActivationRequest subscriptionActivationRequest) {
        String baseUrl = kilkariProperties.getProperty("omsm.base.url");
        String url = (baseUrl.endsWith("/")) ? String.format("%s%s", baseUrl, ACTIVATE_SUBSCRIPTION_PATH) : String.format("%s/%s", baseUrl, ACTIVATE_SUBSCRIPTION_PATH);
        String username = kilkariProperties.getProperty("omsm.username");
        String password = kilkariProperties.getProperty("omsm.password");

        HashMap<String, String> urlVariables = new HashMap<>();
        urlVariables.put("msisdn", subscriptionActivationRequest.getMsisdn());
        urlVariables.put("srvkey", subscriptionActivationRequest.getPack().name());
        urlVariables.put("mode", subscriptionActivationRequest.getChannel().name());
        urlVariables.put("refid", subscriptionActivationRequest.getSubscriptionId());
        urlVariables.put("user", username);
        urlVariables.put("pass", password);

        try {
            restTemplate.getForEntity(url, String.class, urlVariables);
        } catch  (HttpClientErrorException ex) {
            logger.error(String.format("OnMobile subscription request failed with errorCode: %s, error: %s", ex.getStatusCode(), ex.getResponseBodyAsString()));
            throw ex;
        }
    }
}
