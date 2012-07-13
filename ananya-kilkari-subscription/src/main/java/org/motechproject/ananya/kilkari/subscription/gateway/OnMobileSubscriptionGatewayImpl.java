package org.motechproject.ananya.kilkari.subscription.gateway;

import org.motechproject.ananya.kilkari.reporting.profile.ProductionProfile;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionActivationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@ProductionProfile
public class OnMobileSubscriptionGatewayImpl implements OnMobileSubscriptionGateway {

    private RestTemplate restTemplate;
    private OnMobileEndpoints onMobileEndpoints;
    private final static Logger LOGGER = LoggerFactory.getLogger(OnMobileSubscriptionGatewayImpl.class);

    @Autowired
    public OnMobileSubscriptionGatewayImpl(@Qualifier("kilkariRestTemplate") RestTemplate restTemplate,
                                           OnMobileEndpoints onMobileEndpoints) {
        this.restTemplate = restTemplate;
        this.onMobileEndpoints = onMobileEndpoints;
    }

    public void activateSubscription(SubscriptionActivationRequest subscriptionActivationRequest) {
        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put("msisdn", subscriptionActivationRequest.getMsisdn());
        urlVariables.put("srvkey", subscriptionActivationRequest.getPack().name());
        urlVariables.put("mode", subscriptionActivationRequest.getChannel().name());
        urlVariables.put("refid", subscriptionActivationRequest.getSubscriptionId());
        urlVariables.put("user", onMobileEndpoints.username());
        urlVariables.put("pass", onMobileEndpoints.password());

        try {
            restTemplate.getForEntity(onMobileEndpoints.activateSubscriptionURL(), String.class, urlVariables);
        } catch (HttpClientErrorException ex) {
            LOGGER.error(String.format("OnMobile subscription request failed with errorCode: %s, error: %s", ex.getStatusCode(), ex.getResponseBodyAsString()));
            throw ex;
        }
    }
}