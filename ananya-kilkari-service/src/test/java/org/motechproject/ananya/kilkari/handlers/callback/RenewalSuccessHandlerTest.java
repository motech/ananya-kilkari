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

public class RenewalSuccessHandlerTest {
    @Mock
    private SubscriptionService subscriptionService;
    private RenewalSuccessHandler renewalSuccessHandler;

    @Before
    public void setup(){
        initMocks(this);
        renewalSuccessHandler = new RenewalSuccessHandler(subscriptionService);
    }

    @Test
    public void shouldInvokeSubscriptionServiceForGivenSubscriptionToRecordSuccessAndRenewedDate() {
        String subscriptionId = "subId";
        DateTime now = DateTime.now();
        String graceCount = "0";
        final CallbackRequest callbackRequest = new CallbackRequest();
        callbackRequest.setGraceCount(graceCount);
        
        renewalSuccessHandler.perform(new CallbackRequestWrapper(callbackRequest, subscriptionId, now));

        verify(subscriptionService).renewSubscription(subscriptionId, now, Integer.valueOf(graceCount));
    }
}
