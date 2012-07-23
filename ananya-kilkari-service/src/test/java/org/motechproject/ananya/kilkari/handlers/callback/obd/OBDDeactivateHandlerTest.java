package org.motechproject.ananya.kilkari.handlers.callback.obd;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallRequest;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallRequestWrapper;
import org.motechproject.ananya.kilkari.subscription.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.domain.DeactivationRequest;
import org.motechproject.ananya.kilkari.subscription.service.SubscriptionService;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class OBDDeactivateHandlerTest {

    @Mock
    SubscriptionService subscriptionService;

    @Test
    public void shouldProcessDeactivationRequest() {
        OBDDeactivateHandler obdDeactivateHandler = new OBDDeactivateHandler(subscriptionService);
        Channel channel = Channel.IVR;
        String subscriptionId = "subscriptionId";
        OBDSuccessfulCallRequestWrapper successfulCallRequestWrapper = new OBDSuccessfulCallRequestWrapper(new OBDSuccessfulCallRequest(), subscriptionId, DateTime.now(), channel);

        obdDeactivateHandler.process(successfulCallRequestWrapper);

        ArgumentCaptor<DeactivationRequest> captor = ArgumentCaptor.forClass(DeactivationRequest.class);
        verify(subscriptionService).requestDeactivation(captor.capture());
        DeactivationRequest actualRequest = captor.getValue();
        assertEquals(actualRequest.getChannel(), channel);
        assertEquals(actualRequest.getSubscriptionId(), subscriptionId);
    }
}