package org.motechproject.ananya.kilkari.reporting.service;

import org.motechproject.ananya.kilkari.reporting.profile.ProductionProfile;
import org.motechproject.ananya.kilkari.reporting.repository.ReportingGateway;
import org.motechproject.ananya.reports.kilkari.contract.request.*;
import org.motechproject.ananya.reports.kilkari.contract.response.LocationResponse;
import org.motechproject.ananya.reports.kilkari.contract.response.SubscriberResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ProductionProfile
public class ReportingServiceImpl implements ReportingService {
    private ReportingGateway reportGateway;

    @Autowired
    public ReportingServiceImpl(ReportingGateway reportGateway) {
        this.reportGateway = reportGateway;
    }

    @Override
    public LocationResponse getLocation(String state, String district, String block, String panchayat) {
        return reportGateway.getLocation(state, district, block, panchayat);
    }

    @Override
    public SubscriberResponse getSubscriber(String subscriptionId) {
        return reportGateway.getSubscriber(subscriptionId);
    }

    @Override
    public void reportSubscriptionCreation(SubscriptionReportRequest subscriptionReportRequest) {
        reportGateway.reportSubscriptionCreation(subscriptionReportRequest);
    }

    @Override
    public void reportSubscriptionStateChange(SubscriptionStateChangeRequest subscriptionStateChangeRequest) {
        reportGateway.reportSubscriptionStateChange(subscriptionStateChangeRequest);
    }

    @Override
    public void reportCampaignMessageDeliveryStatus(CallDetailsReportRequest callDetailsReportRequest) {
        reportGateway.reportCampaignMessageDeliveryStatus(callDetailsReportRequest);
    }

    @Override
    public void reportSubscriberDetailsChange(String subscriptionId, SubscriberReportRequest request) {
        reportGateway.reportSubscriberDetailsChange(subscriptionId, request);
    }

    @Override
    public void reportChangeMsisdnForEarlySubscription(SubscriberChangeMsisdnReportRequest reportRequest) {
        reportGateway.reportChangeMsisdnForSubscriber(reportRequest);
    }

    @Override
    public void reportCampaignScheduleAlertReceived(CampaignScheduleAlertRequest campaignScheduleAlertRequest) {
        reportGateway.reportCampaignScheduleAlertReceived(campaignScheduleAlertRequest);
    }

    @Override
    public List<SubscriberResponse> getSubscribersByMsisdn(String msisdn) {
        return reportGateway.getSubscribersByMsisdn(msisdn);
    }

    @Override
    public void reportCampaignChange(CampaignChangeReportRequest campaignChangeReportRequest, String subscriptionId) {
        reportGateway.reportCampaignChange(campaignChangeReportRequest, subscriptionId);
    }

    @Override
    public void reportCareRequest(SubscriberCareReportRequest subscriberCareReportRequest) {
        reportGateway.reportCareRequest(subscriberCareReportRequest);
    }

	@Override
	public void reportChangeReferredByFlwMsisdn(SubscriptionChangeReferredFLWMsisdnReportRequest reportRequest) {
		 reportGateway.reportChangeReferredByFlwMsisdnForSubscriber(reportRequest);
	}
}
