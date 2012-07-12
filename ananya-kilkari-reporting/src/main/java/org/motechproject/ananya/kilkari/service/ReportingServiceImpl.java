package org.motechproject.ananya.kilkari.service;

import org.motechproject.ananya.kilkari.domain.SubscriberLocation;
import org.motechproject.ananya.kilkari.domain.SubscriptionCreationReportRequest;
import org.motechproject.ananya.kilkari.domain.SubscriptionStateChangeReportRequest;
import org.motechproject.ananya.kilkari.gateway.ReportingGateway;
import org.motechproject.ananya.kilkari.profile.ProductionProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@ProductionProfile
public class ReportingServiceImpl implements ReportingService {

    private ReportingGateway reportGateway;
    private ReportingPublisher reportingPublisher;

    @Autowired
    public ReportingServiceImpl(ReportingGateway reportGateway, ReportingPublisher reportingPublisher) {
        this.reportGateway = reportGateway;
        this.reportingPublisher = reportingPublisher;
    }

    @Override
    public SubscriberLocation getLocation(String district, String block, String panchayat) {
        return reportGateway.getLocation(district, block, panchayat);
    }

    @Override
    public void reportSubscriptionCreation(SubscriptionCreationReportRequest subscriptionCreationReportRequest) {
        reportingPublisher.reportSubscriptionCreation(subscriptionCreationReportRequest);
    }

    @Override
    public void reportSubscriptionStateChange(SubscriptionStateChangeReportRequest subscriptionStateChangeReportRequest) {
        reportingPublisher.reportSubscriptionStateChange(subscriptionStateChangeReportRequest);
    }
}
