package org.motechproject.ananya.kilkari.reporting.service;

import org.motechproject.ananya.kilkari.contract.response.LocationResponse;
import org.motechproject.ananya.kilkari.reporting.domain.*;
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
    public void reportSubscriptionCreation(SubscriptionCreationReportRequest subscriptionCreationReportRequest) {
        reportGateway.reportSubscriptionCreation(subscriptionCreationReportRequest);
    }

    @Override
    public void reportSubscriptionStateChange(SubscriptionStateChangeReportRequest subscriptionStateChangeReportRequest) {
        reportGateway.reportSubscriptionStateChange(subscriptionStateChangeReportRequest);
    }

    @Override
    public void reportCampaignMessageDeliveryStatus(CampaignMessageDeliveryReportRequest campaignMessageDeliveryReportRequest) {
        reportGateway.reportCampaignMessageDeliveryStatus(campaignMessageDeliveryReportRequest);
    }

    @Override
    public void reportSubscriberDetailsChange(String subscriptionId, SubscriberReportRequest request) {
        reportGateway.reportSubscriberDetailsChange(subscriptionId, request);
    }
}
