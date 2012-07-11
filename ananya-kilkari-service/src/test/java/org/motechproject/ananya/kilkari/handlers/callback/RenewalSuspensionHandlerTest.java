package org.motechproject.ananya.kilkari.handlers.callback;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.request.CallbackRequest;
import org.motechproject.ananya.kilkari.request.CallbackRequestWrapper;
import org.motechproject.ananya.kilkari.service.SubscriptionService;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class RenewalSuspensionHandlerTest {
    private RenewalSuspensionHandler renewalSuspensionHandler;

    @Mock
    private SubscriptionService subscriptionService;

    @Before
    public void setup(){
        initMocks(this);
        renewalSuspensionHandler = new RenewalSuspensionHandler();
        renewalSuspensionHandler.setSubscriptionService(subscriptionService);
    }

    @Test
    public void shouldInvokeSubscriptionServiceWithSuspendForTheGivenSubscription() {
        final String subId = "subId";
        final DateTime renewalDate = DateTime.now();
        final String graceCount = "0";
        final String reason = "Low Balance";
        final CallbackRequest callbackRequest = new CallbackRequest();
        callbackRequest.setGraceCount(graceCount);
        callbackRequest.setReason(reason);

        renewalSuspensionHandler.perform(new CallbackRequestWrapper(callbackRequest, subId, renewalDate));

        verify(subscriptionService).suspendSubscription(subId, renewalDate, reason, Integer.valueOf(graceCount));
    }
}
