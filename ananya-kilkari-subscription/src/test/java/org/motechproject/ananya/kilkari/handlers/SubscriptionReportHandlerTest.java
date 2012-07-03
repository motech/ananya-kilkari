package org.motechproject.ananya.kilkari.handlers;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.domain.*;
import org.motechproject.ananya.kilkari.service.ReportingService;
import org.motechproject.scheduler.domain.MotechEvent;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class SubscriptionReportHandlerTest {
    @Mock
    private ReportingService reportingService;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldInvokeReportingServiceToCreateASubscription() {
        final String msisdn = "msisdn";
        final String pack = SubscriptionPack.TWELVE_MONTHS.name();
        final String channel = Channel.IVR.name();
        final String subscriptionId = "abcd1234";

        SubscriptionCreationReportRequest subscriptionCreationReportRequest = new SubscriptionCreationReportRequest(msisdn, pack, channel, subscriptionId, DateTime.now());
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("0", subscriptionCreationReportRequest);

        new SubscriptionReportHandler(reportingService).handleSubscriptionCreation(new MotechEvent(SubscriptionEventKeys.REPORT_SUBSCRIPTION_CREATION, parameters));

        verify(reportingService).createSubscription(subscriptionCreationReportRequest);
    }

    @Test
    public void shouldInvokeReportingServiceToUpdateASubscription() {
        final String subscriptionId = "abcd1234";
        final String status = SubscriptionStatus.ACTIVE.name();
        final String reason = "my own reason";

        SubscriptionStateChangeReportRequest subscriptionStateChangeReportRequest = new SubscriptionStateChangeReportRequest(subscriptionId, status, DateTime.now(), reason);
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("0", subscriptionStateChangeReportRequest);

        new SubscriptionReportHandler(reportingService).handleSubscriptionStateChange(new MotechEvent(SubscriptionEventKeys.REPORT_SUBSCRIPTION_STATE_CHANGE, parameters));

        verify(reportingService).updateSubscriptionStateChange(subscriptionStateChangeReportRequest);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionsRaisedByReportingServiceToCreateASubscription() {
        HashMap<String, Object> parameters = new HashMap<String, Object>() {{
            put("0", null);
        }};

        doThrow(new RuntimeException()).when(reportingService).createSubscription(any(SubscriptionCreationReportRequest.class));

        new SubscriptionReportHandler(reportingService).handleSubscriptionCreation(new MotechEvent(SubscriptionEventKeys.REPORT_SUBSCRIPTION_CREATION, parameters));
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionsRaisedByReportingServiceToUpdateASubscription() {
        HashMap<String, Object> parameters = new HashMap<String, Object>() {{
            put("0", null);
        }};

        doThrow(new RuntimeException()).when(reportingService).updateSubscriptionStateChange(any(SubscriptionStateChangeReportRequest.class));

        new SubscriptionReportHandler(reportingService).handleSubscriptionStateChange(new MotechEvent(SubscriptionEventKeys.REPORT_SUBSCRIPTION_STATE_CHANGE, parameters));
    }
}
