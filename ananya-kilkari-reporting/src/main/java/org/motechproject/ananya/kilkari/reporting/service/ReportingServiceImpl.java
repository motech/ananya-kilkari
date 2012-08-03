package org.motechproject.ananya.kilkari.reporting.service;

import org.motechproject.ananya.kilkari.reporting.domain.*;
import org.motechproject.ananya.kilkari.reporting.profile.ProductionProfile;
import org.motechproject.ananya.kilkari.reporting.repository.ReportingGateway;
import org.motechproject.http.client.service.HttpClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Properties;

import static org.motechproject.ananya.kilkari.reporting.domain.URLPath.*;

@Service
@ProductionProfile
public class ReportingServiceImpl implements ReportingService {
    private ReportingGateway reportGateway;
    private HttpClientService httpClientService;
    private Properties kilkariProperties;

    @Autowired
    public ReportingServiceImpl(ReportingGateway reportGateway, HttpClientService httpClientService, @Qualifier("kilkariProperties") Properties kilkariProperties) {
        this.reportGateway = reportGateway;
        this.httpClientService = httpClientService;
        this.kilkariProperties = kilkariProperties;
    }

    @Override
    public SubscriberLocation getLocation(String district, String block, String panchayat) {
        return reportGateway.getLocation(district, block, panchayat);
    }

    @Override
    public void reportSubscriptionCreation(SubscriptionCreationReportRequest subscriptionCreationReportRequest) {
        String url = String.format("%s%s", getBaseUrl(), CREATE_SUBSCRIPTION_PATH);
        httpClientService.post(url, subscriptionCreationReportRequest);
    }

    @Override
    public void reportSubscriptionStateChange(SubscriptionStateChangeReportRequest subscriptionStateChangeReportRequest) {
        String subscriptionId = subscriptionStateChangeReportRequest.getSubscriptionId();
        String url = String.format("%s%s/%s", getBaseUrl(), SUBSCRIPTION_STATE_CHANGE_PATH, subscriptionId);
        httpClientService.put(url, subscriptionStateChangeReportRequest);
    }

    @Override
    public void reportCampaignMessageDeliveryStatus(CampaignMessageDeliveryReportRequest campaignMessageDeliveryReportRequest) {
        String url = String.format("%s%s", getBaseUrl(), CALL_DETAILS_PATH);
        httpClientService.post(url, campaignMessageDeliveryReportRequest);
    }

    @Override
    public void reportSubscriberDetailsChange(SubscriberReportRequest request) {
        String url = String.format("%s%s/%s", getBaseUrl(), SUBSCRIBER_UPDATE_PATH, request.getSubscriptionId());
        SubscriberRequest subscriberRequest = new SubscriberRequest(request.getCreatedAt(), request.getBeneficiaryName(),
                request.getBeneficiaryAge(), request.getExpectedDateOfDelivery(),
                request.getDateOfBirth(), request.getLocation());
        httpClientService.put(url, subscriberRequest);
    }

    private String getBaseUrl() {
        String baseUrl = kilkariProperties.getProperty("reporting.service.base.url");
        return baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
    }
}
