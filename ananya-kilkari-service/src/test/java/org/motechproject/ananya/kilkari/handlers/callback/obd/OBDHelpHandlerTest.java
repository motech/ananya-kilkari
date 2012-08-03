package org.motechproject.ananya.kilkari.handlers.callback.obd;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.domain.SubscriberCareRequest;
import org.motechproject.ananya.kilkari.obd.domain.ServiceOption;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallDetailsRequest;
import org.motechproject.ananya.kilkari.service.SubscriberCareService;
import org.motechproject.ananya.kilkari.subscription.domain.Channel;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class OBDHelpHandlerTest {
    @Mock
    private SubscriberCareService subscriberService;
    private OBDHelpHandler obdHelpHandler;

    @Before
    public void setUp() {
        initMocks(this);
        obdHelpHandler = new OBDHelpHandler(subscriberService);
    }

    @Test
    public void shouldCreateASubscriberCareRequest() {
        String msisdn = "1234567890";
        String serviceOption = ServiceOption.HELP.name();
        OBDSuccessfulCallDetailsRequest obdSuccessfulCallDetailsRequest = new OBDSuccessfulCallDetailsRequest();
        obdSuccessfulCallDetailsRequest.setMsisdn(msisdn);
        obdSuccessfulCallDetailsRequest.setServiceOption(serviceOption);

        obdHelpHandler.process(obdSuccessfulCallDetailsRequest);

        ArgumentCaptor<SubscriberCareRequest> subscriberCareRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriberCareRequest.class);
        verify(subscriberService).createSubscriberCareRequest(subscriberCareRequestArgumentCaptor.capture());
        SubscriberCareRequest subscriberCareRequest = subscriberCareRequestArgumentCaptor.getValue();

        assertEquals(msisdn, subscriberCareRequest.getMsisdn());
        assertEquals(serviceOption, subscriberCareRequest.getReason());
        assertEquals(Channel.IVR.name(), subscriberCareRequest.getChannel());
    }
}
