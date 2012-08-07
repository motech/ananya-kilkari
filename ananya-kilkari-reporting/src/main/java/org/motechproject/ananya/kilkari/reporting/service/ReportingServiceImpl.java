package org.motechproject.ananya.kilkari.reporting.service;

import org.motechproject.ananya.kilkari.contract.request.CallDetailsReportRequest;
import org.motechproject.ananya.kilkari.contract.request.SubscriberReportRequest;
import org.motechproject.ananya.kilkari.contract.request.SubscriptionReportRequest;
import org.motechproject.ananya.kilkari.contract.request.SubscriptionStateChangeRequest;
import org.motechproject.ananya.kilkari.contract.response.LocationResponse;
import org.motechproject.ananya.kilkari.contract.response.SubscriberResponse;
import org.motechproject.ananya.kilkari.reporting.profile.ProductionProfile;
import org.motechproject.ananya.kilkari.reporting.repository.ReportingGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@ProductionProfile
public class ReportingServiceImpl implements ReportingService {
    private ReportingGateway reportGateway;

    @Autowired
    public ReportingServiceImpl(ReportingGateway reportGateway) {
        this.reportGateway = reportGateway;
    }

    @Override
    public LocationResponse getLocation(String district, String block, String panchayat) {
        return reportGateway.getLocation(district, block, panchayat);
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
    public void reportChangeMsisdnForSubscriber(String subscriptionId, String msisdn) {
        reportGateway.reportChangeMsisdnForSubscriber(subscriptionId,msisdn);
    }
}
