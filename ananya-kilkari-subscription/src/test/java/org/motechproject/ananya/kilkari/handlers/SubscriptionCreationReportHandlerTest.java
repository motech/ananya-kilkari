package org.motechproject.ananya.kilkari.handlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.domain.*;
import org.motechproject.ananya.kilkari.service.OnMobileSubscriptionService;
import org.motechproject.ananya.kilkari.service.ReportingService;
import org.motechproject.ananya.kilkari.service.SubscriptionService;
import org.motechproject.scheduler.domain.MotechEvent;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class SubscriptionCreationReportHandlerTest {
    @Mock
    private ReportingService reportingService;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldInvokeOnMobileSubscriptionServiceToCreateAnActivationRequest() {
        final String msisdn = "msisdn";
        final String pack = SubscriptionPack.TWELVE_MONTHS.name();
        final String channel = Channel.IVR.name();
        final String subscriptionId = "abcd1234";

        SubscriptionReportRequest subscriptionReportRequest = new SubscriptionReportRequest(msisdn, pack, channel, subscriptionId);
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("0", subscriptionReportRequest);

        new SubscriptionCreationReportHandler(reportingService).handleReportSubscription(new MotechEvent(SubscriptionEventKeys.REPORT_SUBSCRIPTION_CREATION, parameters));

        verify(reportingService).createSubscription(subscriptionReportRequest);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionsRaisedByReportingServiceToCreateASubscription() {
        HashMap<String, Object> parameters = new HashMap<String, Object>() {{
            put("0", null);
        }};

        doThrow(new RuntimeException()).when(reportingService).createSubscription(any(SubscriptionReportRequest.class));

        new SubscriptionCreationReportHandler(reportingService).handleReportSubscription(new MotechEvent(SubscriptionEventKeys.REPORT_SUBSCRIPTION_CREATION, parameters));
    }
}
