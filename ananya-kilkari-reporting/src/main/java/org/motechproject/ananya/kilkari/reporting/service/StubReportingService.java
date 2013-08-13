package org.motechproject.ananya.kilkari.reporting.service;

import org.motechproject.ananya.kilkari.reporting.profile.TestProfile;
import org.motechproject.ananya.reports.kilkari.contract.request.*;
import org.motechproject.ananya.reports.kilkari.contract.response.LocationResponse;
import org.motechproject.ananya.reports.kilkari.contract.response.SubscriberResponse;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@TestProfile
public class StubReportingService implements ReportingService {
    private ReportingService behavior;
    private boolean reportCampaignMessageDeliveryCalled;

    @Override
    public LocationResponse getLocation(String state, String district, String block, String panchayat) {
        if (verify()) {
            return behavior.getLocation(state, district, block, panchayat);
        }
        return null;
    }

    @Override
    public SubscriberResponse getSubscriber(String subscriptionId) {
        if (verify()) {
            return behavior.getSubscriber(subscriptionId);
        }
        return null;
    }

    @Override
    public void reportSubscriptionCreation(SubscriptionReportRequest subscriptionReportRequest) {
        if (verify()) {
            behavior.reportSubscriptionCreation(subscriptionReportRequest);
        }
    }

    @Override
    public void reportSubscriptionStateChange(SubscriptionStateChangeRequest subscriptionStateChangeRequest) {
        if (verify()) {
            behavior.reportSubscriptionStateChange(subscriptionStateChangeRequest);
        }
    }

    @Override
    public void reportCampaignMessageDeliveryStatus(CallDetailsReportRequest callDetailsReportRequest) {
        if (verify()) {
            behavior.reportCampaignMessageDeliveryStatus(callDetailsReportRequest);
            reportCampaignMessageDeliveryCalled = true;
        }
    }

    @Override
    public void reportSubscriberDetailsChange(String subscriptionId, SubscriberReportRequest subscriberReportRequest) {
        if (verify()) {
            behavior.reportSubscriberDetailsChange(subscriptionId, subscriberReportRequest);
        }
    }

    @Override
    public void reportChangeMsisdnForEarlySubscription(SubscriberChangeMsisdnReportRequest reportRequest) {
        if (verify()) {
            behavior.reportChangeMsisdnForEarlySubscription(reportRequest);
        }
    }

    @Override
    public void reportCampaignScheduleAlertReceived(CampaignScheduleAlertRequest campaignScheduleAlertRequest) {
        if (verify()) {
            behavior.reportCampaignScheduleAlertReceived(campaignScheduleAlertRequest);
        }
    }

    @Override
    public List<SubscriberResponse> getSubscribersByMsisdn(String msisdn) {
        if (verify()) {
            return behavior.getSubscribersByMsisdn(msisdn);
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public void reportCampaignChange(CampaignChangeReportRequest campaignChangeReportRequest, String subscriptionId) {
        if (verify()) {
            behavior.reportCampaignChange(campaignChangeReportRequest, subscriptionId);
        }
    }

    @Override
    public void reportCareRequest(SubscriberCareReportRequest subscriberCareReportRequest) {
        if (verify()) {
            behavior.reportCareRequest(subscriberCareReportRequest);
        }
    }

    public void setBehavior(ReportingService behavior) {
        this.behavior = behavior;
    }

    public boolean isReportCampaignMessageDeliveryCalled() {
        return reportCampaignMessageDeliveryCalled;
    }

    public void setReportCampaignMessageDeliveryCalled(boolean reportCampaignMessageDeliveryCalled) {
        this.reportCampaignMessageDeliveryCalled = reportCampaignMessageDeliveryCalled;
    }

    private boolean verify() {
        if (behavior == null) {
            System.err.println(String.format("WARNING: %s: You need to set behavior before calling this method. Use setBehavior method.", StubReportingService.class.getCanonicalName()));
            return false;
        }
        return true;
    }

	@Override
	public void reportChangeReferredByFlwMsisdn(SubscriptionChangeReferredFLWMsisdnReportRequest reportRequest) {
		if (verify()) {
            behavior.reportChangeReferredByFlwMsisdn(reportRequest);
        }
	}
}
