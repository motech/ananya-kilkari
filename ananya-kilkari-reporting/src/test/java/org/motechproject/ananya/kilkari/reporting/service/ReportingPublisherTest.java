package org.motechproject.ananya.kilkari.reporting.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.reporting.domain.*;
import org.motechproject.scheduler.context.EventContext;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class ReportingPublisherTest {
    @Mock
    private EventContext eventContext;

    private ReportingPublisher reportingPublisher;

    @Before
    public void setUp() {
        initMocks(this);
        reportingPublisher = new ReportingPublisher(eventContext);
    }

    @Test
    public void shouldPublishReportSubscriptionCreationEventIntoQueue() {
        SubscriptionCreationReportRequest creationReportRequest = mock(SubscriptionCreationReportRequest.class);

        reportingPublisher.reportSubscriptionCreation(creationReportRequest);

        verify(eventContext).send(ReportingEventKeys.REPORT_SUBSCRIPTION_CREATION, creationReportRequest);
    }

    @Test
    public void shouldPublishSubscriptionStateChangeEventIntoQueue() {
        SubscriptionStateChangeReportRequest stateChangeReportRequest = mock(SubscriptionStateChangeReportRequest.class);

        reportingPublisher.reportSubscriptionStateChange(stateChangeReportRequest);

        verify(eventContext).send(ReportingEventKeys.REPORT_SUBSCRIPTION_STATE_CHANGE, stateChangeReportRequest);
    }

    @Test
    public void shouldPublishCampaignMessageDeliveredEventIntoQueue() {
        CampaignMessageDeliveryReportRequest messageDeliveryReportRequest = mock(CampaignMessageDeliveryReportRequest.class);

        reportingPublisher.reportCampaignMessageDeliveryStatus(messageDeliveryReportRequest);

        verify(eventContext).send(ReportingEventKeys.REPORT_CAMPAIGN_MESSAGE_DELIVERY_STATUS, messageDeliveryReportRequest);
    }

    @Test
    public void shouldPublishSubscriberUpdateEventIntoQueue() {
        SubscriberUpdateReportRequest subscriberUpdateReportRequest = mock(SubscriberUpdateReportRequest.class);

        reportingPublisher.reportSubscriberDetailsChange(subscriberUpdateReportRequest);

        verify(eventContext).send(ReportingEventKeys.REPORT_SUBSCRIBER_DETAILS_UPDATE, subscriberUpdateReportRequest);
    }
}

