package org.motechproject.ananya.kilkari.handlers.callback.obd;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.kilkari.obd.service.request.OBDSuccessfulCallDetailsRequest;
import org.motechproject.ananya.kilkari.obd.domain.Channel;
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
        DateTime createdAt = DateTime.now().minusMinutes(42);
        OBDSuccessfulCallDetailsRequest obdSuccessfulCallDetailsRequest =  new OBDSuccessfulCallDetailsRequest(subscriptionId, null, null, null, null, createdAt);

        obdDeactivateHandler.process(obdSuccessfulCallDetailsRequest);

        ArgumentCaptor<DeactivationRequest> captor = ArgumentCaptor.forClass(DeactivationRequest.class);
        verify(subscriptionService).requestDeactivation(captor.capture());
        DeactivationRequest actualRequest = captor.getValue();
        assertEquals(channel, actualRequest.getChannel());
        assertEquals(subscriptionId, actualRequest.getSubscriptionId());
        assertEquals(createdAt, actualRequest.getCreatedAt());
    }
}
