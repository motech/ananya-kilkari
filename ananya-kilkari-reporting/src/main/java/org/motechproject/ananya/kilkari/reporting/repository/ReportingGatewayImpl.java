package org.motechproject.ananya.kilkari.reporting.repository;

import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.motechproject.ananya.kilkari.contract.request.CallDetailsRequest;
import org.motechproject.ananya.kilkari.contract.request.SubscriptionReportRequest;
import org.motechproject.ananya.kilkari.contract.request.SubscriptionStateChangeRequest;
import org.motechproject.ananya.kilkari.contract.response.LocationResponse;
import org.motechproject.ananya.kilkari.reporting.domain.*;
import org.motechproject.ananya.kilkari.reporting.profile.ProductionProfile;
import org.motechproject.http.client.service.HttpClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.motechproject.ananya.kilkari.reporting.domain.URLPath.*;

@Repository
@ProductionProfile
public class ReportingGatewayImpl implements ReportingGateway {
    private RestTemplate restTemplate;
    private HttpClientService httpClientService;
    private Properties kilkariProperties;

    @Autowired
    public ReportingGatewayImpl(RestTemplate kilkariRestTemplate, HttpClientService httpClientService, @Qualifier("kilkariProperties") Properties kilkariProperties) {
        this.restTemplate = kilkariRestTemplate;
        this.httpClientService = httpClientService;
        this.kilkariProperties = kilkariProperties;
    }

    @Override
    public LocationResponse getLocation(String district, String block, String panchayat) {
        List<NameValuePair> locationParameters = constructParameterMap(district, block, panchayat);
        String url = constructGetLocationUrl(locationParameters);
        try {
            return restTemplate.getForEntity(url, LocationResponse.class).getBody();
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode().equals(HttpStatus.NOT_FOUND))
                return null;
            throw ex;
        }
    }

    @Override
    public void reportSubscriptionCreation(SubscriptionReportRequest subscriptionReportRequest) {
        String url = String.format("%s%s", getBaseUrl(), CREATE_SUBSCRIPTION_PATH);
        httpClientService.post(url, subscriptionReportRequest);
    }

    @Override
    public void reportSubscriptionStateChange(SubscriptionStateChangeRequest subscriptionStateChangeRequest) {
        String subscriptionId = subscriptionStateChangeRequest.getSubscriptionId();
        String url = String.format("%s%s/%s", getBaseUrl(), SUBSCRIPTION_STATE_CHANGE_PATH, subscriptionId);
        httpClientService.put(url, subscriptionStateChangeRequest);
    }

    @Override
    public void reportCampaignMessageDeliveryStatus(CallDetailsRequest callDetailsRequest) {
        String url = String.format("%s%s", getBaseUrl(), CALL_DETAILS_PATH);
        httpClientService.post(url, callDetailsRequest);
    }

    @Override
    public void reportSubscriberDetailsChange(String subscriptionId, SubscriberReportRequest request) {
        String url = String.format("%s%s/%s", getBaseUrl(), SUBSCRIBER_UPDATE_PATH, subscriptionId);
        httpClientService.put(url, request);
    }

    private String constructGetLocationUrl(List<NameValuePair> params) {
        String url = String.format("%s%s", getBaseUrl(), GET_LOCATION_PATH);
        return String.format("%s?%s", url, URLEncodedUtils.format(params, HTTP.UTF_8));
    }

    private List<NameValuePair> constructParameterMap(String district, String block, String panchayat) {
        List<NameValuePair> locationParameters = new ArrayList<>();
        if (StringUtils.isNotBlank(district)) locationParameters.add(new BasicNameValuePair("district", district));
        if (StringUtils.isNotBlank(block)) locationParameters.add(new BasicNameValuePair("block", block));
        if (StringUtils.isNotBlank(panchayat)) locationParameters.add(new BasicNameValuePair("panchayat", panchayat));
        return locationParameters;
    }

    private String getBaseUrl() {
        String baseUrl = kilkariProperties.getProperty("reporting.service.base.url");
        return baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
    }
}
