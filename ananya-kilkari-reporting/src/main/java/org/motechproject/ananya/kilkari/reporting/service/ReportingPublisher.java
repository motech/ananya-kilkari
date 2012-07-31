package org.motechproject.ananya.kilkari.reporting.service;

import org.motechproject.ananya.kilkari.reporting.domain.*;
import org.motechproject.scheduler.context.EventContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ReportingPublisher {
    @Autowired
    private EventContext eventContext;

    @Autowired
    public ReportingPublisher(@Qualifier("eventContext") EventContext eventContext) {
        this.eventContext = eventContext;
    }

    public void reportSubscriptionCreation(SubscriptionCreationReportRequest subscriptionCreationReportRequest) {
        eventContext.send(ReportingEventKeys.REPORT_SUBSCRIPTION_CREATION, subscriptionCreationReportRequest);
    }

    public void reportSubscriptionStateChange(SubscriptionStateChangeReportRequest subscriptionStateChangeReportRequest) {
        eventContext.send(ReportingEventKeys.REPORT_SUBSCRIPTION_STATE_CHANGE, subscriptionStateChangeReportRequest);
    }

    public void reportCampaignMessageDeliveryStatus(CampaignMessageDeliveryReportRequest campaignMessageDeliveryReportRequest) {
        eventContext.send(ReportingEventKeys.REPORT_CAMPAIGN_MESSAGE_DELIVERY_STATUS, campaignMessageDeliveryReportRequest);
    }

    public void reportSubscriberDetailsChange(SubscriberUpdateReportRequest subscriberUpdateReportRequest) {
        eventContext.send(ReportingEventKeys.REPORT_SUBSCRIBER_DETAILS_UPDATE, subscriberUpdateReportRequest);
    }
}
