package org.motechproject.ananya.kilkari.service;

import org.apache.log4j.Logger;
import org.motechproject.ananya.kilkari.domain.SubscriptionReportRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Properties;

@Service
public class ReportingService {

    public static final String CREATE_SUBSCRIPTION_PATH = "subscription";
    private RestTemplate restTemplate;
    private Properties kilkariProperties;
    private Logger logger = Logger.getLogger(ReportingService.class);

    @Autowired
    public ReportingService(@Qualifier("kilkariRestTemplate") RestTemplate restTemplate, @Qualifier("kilkariProperties") Properties kilkariProperties) {
        this.restTemplate = restTemplate;
        this.kilkariProperties = kilkariProperties;
    }

    public void createSubscription(SubscriptionReportRequest subscriptionReportRequest) {
        String baseUrl = kilkariProperties.getProperty("reporting.service.base.url");
        String url = (baseUrl.endsWith("/")) ? String.format("%s%s", baseUrl, CREATE_SUBSCRIPTION_PATH) : String.format("%s/%s", baseUrl, CREATE_SUBSCRIPTION_PATH);
        try {
            restTemplate.postForLocation(url, subscriptionReportRequest, String.class, new HashMap<String, String>());
        } catch  (HttpClientErrorException ex) {
            logger.error(String.format("Reporting subscription creation failed with errorCode: %s, error: %s", ex.getStatusCode(), ex.getResponseBodyAsString()));
            throw ex;
        }
    }
}
