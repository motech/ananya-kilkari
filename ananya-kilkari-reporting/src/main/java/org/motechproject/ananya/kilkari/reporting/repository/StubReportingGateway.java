package org.motechproject.ananya.kilkari.reporting.repository;

import org.motechproject.ananya.kilkari.reporting.profile.TestProfile;
import org.motechproject.ananya.reports.kilkari.contract.request.*;
import org.motechproject.ananya.reports.kilkari.contract.response.LocationResponse;
import org.motechproject.ananya.reports.kilkari.contract.response.SubscriberResponse;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
@TestProfile
public class StubReportingGateway implements ReportingGateway {
    private ReportingGateway behavior;

    @Override
    public LocationResponse getLocation(String state, String district, String block, String panchayat) {
        if (verify())
            return behavior.getLocation(state, district, block, panchayat);
        return null;
    }

    @Override
    public SubscriberResponse getSubscriber(String subscriptionId) {
        if (verify())
            return behavior.getSubscriber(subscriptionId);
        return null;
    }

    @Override
    public void reportSubscriptionCreation(SubscriptionReportRequest subscriptionReportRequest) {
        if (verify())
            behavior.reportSubscriptionCreation(subscriptionReportRequest);
    }

    @Override
    public void reportSubscriptionStateChange(SubscriptionStateChangeRequest subscriptionStateChangeRequest) {
        if (verify())
            behavior.reportSubscriptionStateChange(subscriptionStateChangeRequest);
    }

    @Override
    public void reportCampaignMessageDeliveryStatus(CallDetailsReportRequest callDetailsReportRequest) {
        if (verify())
            behavior.reportCampaignMessageDeliveryStatus(callDetailsReportRequest);
    }

    @Override
    public void reportSubscriberDetailsChange(String subscriptionId, SubscriberReportRequest subscriberReportRequest) {
        if (verify())
            behavior.reportSubscriberDetailsChange(subscriptionId, subscriberReportRequest);
    }

    @Override
    public void reportChangeMsisdnForSubscriber(SubscriberChangeMsisdnReportRequest reportRequest) {
        if (verify())
            behavior.reportChangeMsisdnForSubscriber(reportRequest);
    }

    @Override
    public void reportCampaignScheduleAlertReceived(CampaignScheduleAlertRequest campaignScheduleAlertRequest) {
        if (verify())
            behavior.reportCampaignScheduleAlertReceived(campaignScheduleAlertRequest);
    }

    @Override
    public void reportCampaignChange(CampaignChangeReportRequest campaignChangeReportRequest, String subscriptionId) {
        if (verify())
            behavior.reportCampaignChange(campaignChangeReportRequest, subscriptionId);
    }

    @Override
    public void reportCareRequest(SubscriberCareReportRequest subscriberCareReportRequest) {
        if (verify())
            behavior.reportCareRequest(subscriberCareReportRequest);
    }

    @Override
    public List<SubscriberResponse> getSubscribersByMsisdn(String msisdn) {
        if (verify())
            return behavior.getSubscribersByMsisdn(msisdn);
        return Collections.EMPTY_LIST;
    }

    public void setBehavior(ReportingGateway behavior) {
        this.behavior = behavior;
    }

    private boolean verify() {
        if (behavior == null) {
            System.err.println(String.format("WARNING: %s: You need to set behavior before calling this method. Use setBehavior method.", StubReportingGateway.class.getCanonicalName()));
            return false;
        }
        return true;
    }

	@Override
	public void reportChangeReferredByFlwMsisdnForSubscriber(SubscriptionChangeReferredFLWMsisdnReportRequest reportRequest) {
		 if (verify())
	            behavior.reportChangeReferredByFlwMsisdnForSubscriber( reportRequest);
		
	}

	@Override
	public void reportSubscriberDetailsForChangeSubscription(
			String subscriptionId,SubscriberChangeSubscriptionReportRequest request) {
		 if (verify())
	            behavior.reportSubscriberDetailsForChangeSubscription(subscriptionId, request);
		
	}

	@Override
	public void reportChangeSubscription(
			ChangeSubscriptionReportRequest changeSubscriptionReportRequest) {
		 if (verify())
	            behavior.reportChangeSubscription(changeSubscriptionReportRequest);
		
	}
}
