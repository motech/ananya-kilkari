package org.motechproject.ananya.kilkari.subscription.repository;

import org.motechproject.ananya.kilkari.reporting.profile.ProductionProfile;
import org.motechproject.ananya.kilkari.subscription.request.OMSubscriptionRequest;
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

    public void activateSubscription(OMSubscriptionRequest omsubscriptionrequest) {
        Map<String, String> urlVariables = constructRequestParams(omsubscriptionrequest);
        sendRequest(onMobileEndpoints.activateSubscriptionURL(), urlVariables);
    }

    public void deactivateSubscription(OMSubscriptionRequest omSubscriptionRequest) {
        Map<String, String> urlVariables = constructRequestParams(omSubscriptionRequest);
        sendRequest(onMobileEndpoints.deactivateSubscriptionURL(), urlVariables);
    }

    private void sendRequest(String url, Map<String, String> urlVariables) {
        try {
            restTemplate.getForEntity(url, String.class, urlVariables);
        } catch (HttpClientErrorException ex) {
            LOGGER.error(String.format("OnMobile subscription request failed with errorCode: %s, error: %s", ex.getStatusCode(), ex.getResponseBodyAsString()));
            throw ex;
        }
    }

    private Map<String, String> constructRequestParams(OMSubscriptionRequest omSubscriptionRequest) {
        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put("msisdn", omSubscriptionRequest.getMsisdn());
        urlVariables.put("srvkey", omSubscriptionRequest.getPack().name());
        urlVariables.put("mode", omSubscriptionRequest.getChannel().name());
        urlVariables.put("refid", omSubscriptionRequest.getSubscriptionId());
        urlVariables.put("user", onMobileEndpoints.username());
        urlVariables.put("pass", onMobileEndpoints.password());
        return urlVariables;
    }
}