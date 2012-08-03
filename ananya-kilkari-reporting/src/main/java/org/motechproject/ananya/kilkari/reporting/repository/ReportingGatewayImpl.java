package org.motechproject.ananya.kilkari.reporting.repository;

import org.motechproject.ananya.kilkari.reporting.domain.*;
import org.motechproject.ananya.kilkari.reporting.profile.ProductionProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Properties;

@Service
@ProductionProfile
public class ReportingGatewayImpl implements ReportingGateway {

    private RestTemplate restTemplate;
    private Properties kilkariProperties;
    private final static Logger logger = LoggerFactory.getLogger(ReportingGatewayImpl.class);

    @Autowired
    public ReportingGatewayImpl(RestTemplate kilkariRestTemplate, @Qualifier("kilkariProperties") Properties kilkariProperties) {
        this.restTemplate = kilkariRestTemplate;
        this.kilkariProperties = kilkariProperties;
    }

    @Override
    public void createSubscription(SubscriptionCreationReportRequest subscriptionCreationReportRequest) {
        String url = String.format("%s%s", getBaseUrl(), CREATE_SUBSCRIPTION_PATH);
        try {
            restTemplate.postForLocation(url, subscriptionCreationReportRequest, String.class, new HashMap<String, String>());
        } catch (HttpClientErrorException ex) {
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
        } catch (HttpClientErrorException ex) {
            logger.error(String.format("Reporting subscription state change failed with errorCode: %s, error: %s", ex.getStatusCode(), ex.getResponseBodyAsString()));
            throw ex;
        }
    }

    @Override
    public SubscriberLocation getLocation(String district, String block, String panchayat) {
        HashMap<String, String> locationParameters = new HashMap<String, String>();
        locationParameters.put("district", district);
        locationParameters.put("block", block);
        locationParameters.put("panchayat", panchayat);
        String url = constructGetLocationUrl(locationParameters);
        try {
            return restTemplate.getForEntity(url, SubscriberLocation.class).getBody();
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode().equals(HttpStatus.NOT_FOUND))
                return null;
            logger.error(String.format("Reporting subscription state change failed with errorCode: %s, error: %s", ex.getStatusCode(), ex.getResponseBodyAsString()));
            throw ex;
        }
    }

    @Override
    public void reportCampaignMessageDelivery(CampaignMessageDeliveryReportRequest campaignMessageDeliveryReportRequest) {
        String url = String.format("%s%s", getBaseUrl(), CALL_DETAILS_PATH);
        try {
            restTemplate.postForLocation(url, campaignMessageDeliveryReportRequest, new HashMap<String, String>());
        } catch (HttpClientErrorException ex) {
            logger.error(String.format("Reporting campaign message delivery failed with errorCode: %s, error: %s", ex.getStatusCode(), ex.getResponseBodyAsString()));
            throw ex;
        }
    }

    @Override
    public void updateSubscriberDetails(SubscriberReportRequest request) {
        String url = String.format("%s%s/%s", getBaseUrl(), SUBSCRIBER_UPDATE_PATH, request.getSubscriptionId());
        try {
            SubscriberRequest subscriberRequest = new SubscriberRequest(request.getCreatedAt(), request.getBeneficiaryName(),
                    request.getBeneficiaryAge(), request.getExpectedDateOfDelivery(),
                    request.getDateOfBirth(), request.getLocation());
            restTemplate.put(url, subscriberRequest, new HashMap<String, String>());
        } catch (HttpClientErrorException ex) {
            logger.error(String.format("Updating subscriber details failed with errorCode: %s, error: %s", ex.getStatusCode(), ex.getResponseBodyAsString()));
            throw ex;
        }
    }

    private String constructGetLocationUrl(HashMap<String, String> params) {
        String url = String.format("%s%s", getBaseUrl(), GET_LOCATION_PATH);
        boolean paramAdded = false;
        for (String paramName : params.keySet()) {
            String paramValue = params.get(paramName);
            if (paramValue == null) {
                continue;
            }
            url = String.format("%s%s%s=%s", url, (paramAdded ? "&" : "?"), paramName, paramValue);
            paramAdded = true;
        }
        return url;
    }

    private String getBaseUrl() {
        String baseUrl = kilkariProperties.getProperty("reporting.service.base.url");
        return baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
    }
}
