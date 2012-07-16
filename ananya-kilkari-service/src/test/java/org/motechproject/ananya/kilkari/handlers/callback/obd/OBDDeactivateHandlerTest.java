package org.motechproject.ananya.kilkari.handlers.callback.obd;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.kilkari.obd.contract.OBDRequest;
import org.motechproject.ananya.kilkari.obd.contract.OBDRequestWrapper;
import org.motechproject.ananya.kilkari.subscription.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.service.SubscriptionService;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class OBDDeactivateHandlerTest {

    @Mock
    SubscriptionService subscriptionService;

    @Test
    public void shouldProcessDeactivationRequest() {
        OBDDeactivateHandler obdDeactivateHandler = new OBDDeactivateHandler(subscriptionService);
        OBDRequestWrapper obdRequestWrapper = new OBDRequestWrapper(new OBDRequest(), "subscriptionId", DateTime.now());

        obdDeactivateHandler.process(obdRequestWrapper);

        verify(subscriptionService).requestDeactivation(obdRequestWrapper.getSubscriptionId(), Channel.IVR);
    }
}
