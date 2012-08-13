package org.motechproject.ananya.kilkari.handlers.callback.obd;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.subscription.service.request.SubscriberCareRequest;
import org.motechproject.ananya.kilkari.obd.domain.ServiceOption;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallDetailsRequest;
import org.motechproject.ananya.kilkari.service.KilkariSubscriberCareService;
import org.motechproject.ananya.kilkari.subscription.domain.Channel;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class OBDHelpHandlerTest {
    @Mock
    private KilkariSubscriberCareService kilkariSubscriberService;
    private OBDHelpHandler obdHelpHandler;

    @Before
    public void setUp() {
        initMocks(this);
        obdHelpHandler = new OBDHelpHandler(kilkariSubscriberService);
    }

    @Test
    public void shouldCreateASubscriberCareRequest() {
        String msisdn = "1234567890";
        String serviceOption = ServiceOption.HELP.name();
        DateTime createdAt = DateTime.now().minusMinutes(42);
        OBDSuccessfulCallDetailsRequest obdSuccessfulCallDetailsRequest = new OBDSuccessfulCallDetailsRequest(null, ServiceOption.HELP, msisdn, null, null, createdAt);
        obdHelpHandler.process(obdSuccessfulCallDetailsRequest);

        ArgumentCaptor<SubscriberCareRequest> subscriberCareRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriberCareRequest.class);
        verify(kilkariSubscriberService).createSubscriberCareRequest(subscriberCareRequestArgumentCaptor.capture());
        SubscriberCareRequest subscriberCareRequest = subscriberCareRequestArgumentCaptor.getValue();

        assertEquals(msisdn, subscriberCareRequest.getMsisdn());
        assertEquals(serviceOption, subscriberCareRequest.getReason());
        assertEquals(Channel.IVR.name(), subscriberCareRequest.getChannel());
        assertEquals(createdAt, subscriberCareRequest.getCreatedAt());
    }
}
