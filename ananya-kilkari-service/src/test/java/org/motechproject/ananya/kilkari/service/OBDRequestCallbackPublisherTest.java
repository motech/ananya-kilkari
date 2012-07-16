package org.motechproject.ananya.kilkari.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.kilkari.obd.contract.OBDRequest;
import org.motechproject.ananya.kilkari.obd.contract.OBDRequestWrapper;
import org.motechproject.ananya.kilkari.obd.domain.OBDEventKeys;
import org.motechproject.scheduler.context.EventContext;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class OBDRequestCallbackPublisherTest {

    @Mock
    private EventContext eventContext;

    private OBDRequestCallbackPublisher obdRequestCallbackPublisher;

    @Before
    public void setUp() {
        obdRequestCallbackPublisher = new OBDRequestCallbackPublisher(eventContext);
    }

    @Test
    public void shouldPublishCallBackRequests() {
        OBDRequestWrapper obdRequestWrapper = new OBDRequestWrapper(new OBDRequest(), "subscriptionID", DateTime.now());
        obdRequestCallbackPublisher.publishObdCallbackRequest(obdRequestWrapper);

        verify(eventContext).send(OBDEventKeys.PROCESS_CALLBACK_REQUEST, obdRequestWrapper);
    }
}
