package org.motechproject.ananya.kilkari.reporting.handlers;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.reporting.domain.ReportingEventKeys;
import org.motechproject.ananya.kilkari.reporting.domain.SubscriptionCreationReportRequest;
import org.motechproject.ananya.kilkari.reporting.domain.SubscriptionDetails;
import org.motechproject.ananya.kilkari.reporting.domain.SubscriptionStateChangeReportRequest;
import org.motechproject.ananya.kilkari.reporting.repository.ReportingGateway;
import org.motechproject.scheduler.domain.MotechEvent;

import java.util.HashMap;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class SubscriptionReportHandlerTest {
    @Mock
    private ReportingGateway reportingGateway;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldInvokeReportingServiceToCreateASubscription() {
        SubscriptionCreationReportRequest subscriptionCreationReportRequest = new SubscriptionCreationReportRequest(new SubscriptionDetails(), null, 0, null, null, null, null);
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("0", subscriptionCreationReportRequest);

        new SubscriptionReportHandler(reportingGateway).handleSubscriptionCreation(new MotechEvent(ReportingEventKeys.REPORT_SUBSCRIPTION_CREATION, parameters));

        verify(reportingGateway).createSubscription(subscriptionCreationReportRequest);
    }

    @Test
    public void shouldInvokeReportingServiceToUpdateASubscription() {
        SubscriptionStateChangeReportRequest subscriptionStateChangeReportRequest = new SubscriptionStateChangeReportRequest("abcd1234", "ACTIVE", DateTime.now(), "my own reason", "AIRTEL");
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("0", subscriptionStateChangeReportRequest);

        new SubscriptionReportHandler(reportingGateway).handleSubscriptionStateChange(new MotechEvent(ReportingEventKeys.REPORT_SUBSCRIPTION_STATE_CHANGE, parameters));

        verify(reportingGateway).updateSubscriptionStateChange(subscriptionStateChangeReportRequest);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionsRaisedByReportingServiceToCreateASubscription() {
        HashMap<String, Object> parameters = new HashMap<String, Object>() {{
            put("0", null);
        }};

        doThrow(new RuntimeException()).when(reportingGateway).createSubscription(any(SubscriptionCreationReportRequest.class));

        new SubscriptionReportHandler(reportingGateway).handleSubscriptionCreation(new MotechEvent(ReportingEventKeys.REPORT_SUBSCRIPTION_CREATION, parameters));
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionsRaisedByReportingServiceToUpdateASubscription() {
        HashMap<String, Object> parameters = new HashMap<String, Object>() {{
            put("0", null);
        }};

        doThrow(new RuntimeException()).when(reportingGateway).updateSubscriptionStateChange(any(SubscriptionStateChangeReportRequest.class));

        new SubscriptionReportHandler(reportingGateway).handleSubscriptionStateChange(new MotechEvent(ReportingEventKeys.REPORT_SUBSCRIPTION_STATE_CHANGE, parameters));
    }
}
