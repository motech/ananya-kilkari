package org.motechproject.ananya.kilkari.service;

import org.motechproject.ananya.kilkari.domain.SubscriberLocation;
import org.motechproject.ananya.kilkari.domain.SubscriptionCreationReportRequest;
import org.motechproject.ananya.kilkari.domain.SubscriptionStateChangeReportRequest;
import org.motechproject.ananya.kilkari.profile.ProductionProfile;
import org.motechproject.ananya.kilkari.profile.ProductionProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Properties;

@Service
@ProductionProfile
public class ReportingServiceImpl implements ReportingService {

    private RestTemplate restTemplate;
    private Properties kilkariProperties;
    private final static Logger logger = LoggerFactory.getLogger(ReportingServiceImpl.class);

    @Autowired
    public ReportingServiceImpl(@Qualifier("kilkariRestTemplate") RestTemplate restTemplate, @Qualifier("kilkariProperties") Properties kilkariProperties) {
        this.restTemplate = restTemplate;
        this.kilkariProperties = kilkariProperties;
    }

    @Override
    public void createSubscription(SubscriptionCreationReportRequest subscriptionCreationReportRequest) {
        String url = String.format("%s%s", getBaseUrl(), CREATE_SUBSCRIPTION_PATH);
        try {
            restTemplate.postForLocation(url, subscriptionCreationReportRequest, String.class, new HashMap<String, String>());
        } catch  (HttpClientErrorException ex) {
            logger.error(String.format("Reporting subscription creation failed with errorCode: %s, error: %s", ex.getStatusCode(), ex.getResponseBodyAsString()));
            throw ex;
        }
    }

    @Override
    public void updateSubscriptionStateChange(SubscriptionStateChangeReportRequest subscriptionStateChangeReportRequest) {
        String subscriptionId = subscriptionStateChangeReportRequest.getSubscriptionId();
        String url = String.format("%s%s/%s", getBaseUrl(), SUBSCRIPTION_STATE_CHANGE_PATH, subscriptionId);
        try {
            restTemplate.put(url, subscriptionStateChangeReportRequest, new HashMap<String, String>());
        } catch  (HttpClientErrorException ex) {
            logger.error(String.format("Reporting subscription state change failed with errorCode: %s, error: %s", ex.getStatusCode(), ex.getResponseBodyAsString()));
            throw ex;
        }

    }

    @Override
    public SubscriberLocation getLocation(String district, String block, String panchayat) {
        String url = String.format("%s%s", getBaseUrl(), GET_LOCATION_PATH);
        HashMap<String, String> locationParameters = new HashMap<String, String>();
        locationParameters.put("district",district);
        locationParameters.put("block",block);
        locationParameters.put("panchayat",panchayat);
        try {
            return restTemplate.getForEntity(url, SubscriberLocation.class, locationParameters).getBody();
        } catch  (HttpClientErrorException ex) {
            logger.error(String.format("Reporting subscription state change failed with errorCode: %s, error: %s", ex.getStatusCode(), ex.getResponseBodyAsString()));
            throw ex;
        }
    }

    private String getBaseUrl() {
        String baseUrl = kilkariProperties.getProperty("reporting.service.base.url");
        return baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
    }
}
