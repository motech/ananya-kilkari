package org.motechproject.ananya.kilkari.reporting.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.reporting.domain.ReportingEventKeys;
import org.motechproject.ananya.kilkari.reporting.domain.SubscriptionCreationReportRequest;
import org.motechproject.ananya.kilkari.reporting.domain.SubscriptionDetails;
import org.motechproject.ananya.kilkari.reporting.domain.SubscriptionStateChangeReportRequest;
import org.motechproject.scheduler.context.EventContext;

import static org.junit.Assert.assertEquals;
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
        String pack = "TWELVE_MONTHS";
        String channel = "IVR";
        SubscriptionDetails subscriptionDetails = new SubscriptionDetails("1234567890", pack, DateTime.now(), "ACTIVE", "subscriptionId");

        reportingPublisher.reportSubscriptionCreation(new SubscriptionCreationReportRequest(subscriptionDetails, channel, 0, null, null, null, null));

        ArgumentCaptor<SubscriptionCreationReportRequest> subscriptionReportRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionCreationReportRequest.class);
        ArgumentCaptor<String> eventArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(eventContext).send(eventArgumentCaptor.capture(), subscriptionReportRequestArgumentCaptor.capture());
        SubscriptionCreationReportRequest subscriptionCreationReportRequest = subscriptionReportRequestArgumentCaptor.getValue();
        String eventName = eventArgumentCaptor.getValue();

        assertEquals(ReportingEventKeys.REPORT_SUBSCRIPTION_CREATION, eventName);
        assertEquals("1234567890", subscriptionCreationReportRequest.getMsisdn());
        assertEquals(pack, subscriptionCreationReportRequest.getPack());
        assertEquals(channel, subscriptionCreationReportRequest.getChannel());
        assertEquals(subscriptionDetails.getSubscriptionId(), subscriptionCreationReportRequest.getSubscriptionId());
    }

    @Test
    public void shouldPublishSubscriptionStateChangeEventIntoQueue() {
        String subscriptionId = "ABCD1234";
        String status = "ACTIVE";
        String reason = "my own reason";
        String operator = "AIRTEL";
        SubscriptionStateChangeReportRequest subscriptionStateChangeReportRequest = new SubscriptionStateChangeReportRequest(subscriptionId, status, DateTime.now(), reason, operator);

        reportingPublisher.reportSubscriptionStateChange(subscriptionStateChangeReportRequest);

        ArgumentCaptor<SubscriptionStateChangeReportRequest> subscriptionStateChangeReportRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionStateChangeReportRequest.class);
        ArgumentCaptor<String> eventArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(eventContext).send(eventArgumentCaptor.capture(), subscriptionStateChangeReportRequestArgumentCaptor.capture());

        SubscriptionStateChangeReportRequest actualSubscriptionStateChangeReportRequest = subscriptionStateChangeReportRequestArgumentCaptor.getValue();
        String eventName = eventArgumentCaptor.getValue();

        assertEquals(ReportingEventKeys.REPORT_SUBSCRIPTION_STATE_CHANGE, eventName);
        assertEquals(subscriptionId, actualSubscriptionStateChangeReportRequest.getSubscriptionId());
        assertEquals(status, actualSubscriptionStateChangeReportRequest.getSubscriptionStatus());
        assertEquals(reason, actualSubscriptionStateChangeReportRequest.getReason());
        assertEquals(operator, actualSubscriptionStateChangeReportRequest.getOperator());
    }
}

