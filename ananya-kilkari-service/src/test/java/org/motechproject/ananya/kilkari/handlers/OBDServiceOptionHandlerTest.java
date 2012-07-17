package org.motechproject.ananya.kilkari.handlers;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.kilkari.factory.OBDServiceOptionFactory;
import org.motechproject.ananya.kilkari.handlers.callback.obd.ServiceOptionHandler;
import org.motechproject.ananya.kilkari.obd.domain.OBDEventKeys;
import org.motechproject.ananya.kilkari.obd.domain.ServiceOption;
import org.motechproject.ananya.kilkari.request.OBDRequest;
import org.motechproject.ananya.kilkari.request.OBDRequestWrapper;
import org.motechproject.ananya.kilkari.service.KilkariCampaignService;
import org.motechproject.ananya.kilkari.subscription.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.validators.OBDRequestValidator;
import org.motechproject.scheduler.domain.MotechEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OBDServiceOptionHandlerTest {

    @Mock
    private OBDServiceOptionFactory obdServiceOptionFactory;
    @Mock
    private KilkariCampaignService kilkariCampaignService;
    @Mock
    private ServiceOptionHandler serviceOptionHandler;
    @Mock
    private OBDRequestValidator obdRequestValidator;

    private OBDServiceOptionHandler obdServiceOptionHandler;

    @Before
    public void setUp() {
        obdServiceOptionHandler = new OBDServiceOptionHandler(obdServiceOptionFactory, kilkariCampaignService, obdRequestValidator);
    }

    @Test
    public void shouldHandleAOBDCallBackRequest() {
        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        OBDRequest obdRequest = new OBDRequest();
        obdRequest.setServiceOption(ServiceOption.HELP.name());
        OBDRequestWrapper expectedObdRequest = new OBDRequestWrapper(obdRequest, "subscriptionId", DateTime.now(), Channel.IVR);
        stringObjectHashMap.put("0", expectedObdRequest);
        when(obdServiceOptionFactory.getHandler(ServiceOption.HELP)).thenReturn(serviceOptionHandler);

        obdServiceOptionHandler.handleOBDCallbackRequest(new MotechEvent(OBDEventKeys.PROCESS_CALLBACK_REQUEST, stringObjectHashMap));

        verify(kilkariCampaignService).processSuccessfulMessageDelivery(expectedObdRequest);
        verify(serviceOptionHandler).process(expectedObdRequest);
    }

    @Test
    public void shouldHandleAOBDCallBackRequestWithDeactivation() {
        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        OBDRequest obdRequest = new OBDRequest();
        obdRequest.setServiceOption(ServiceOption.UNSUBSCRIBE.name());
        OBDRequestWrapper expectedObdRequest = new OBDRequestWrapper(obdRequest, "subscriptionId", DateTime.now(), Channel.IVR);
        stringObjectHashMap.put("0", expectedObdRequest);
        when(obdServiceOptionFactory.getHandler(ServiceOption.UNSUBSCRIBE)).thenReturn(serviceOptionHandler);

        obdServiceOptionHandler.handleOBDCallbackRequest(new MotechEvent(OBDEventKeys.PROCESS_CALLBACK_REQUEST, stringObjectHashMap));

        verify(kilkariCampaignService).processSuccessfulMessageDelivery(expectedObdRequest);
        verify(serviceOptionHandler).process(expectedObdRequest);
    }

    @Test(expected = ValidationException.class)
    public void shouldInvalidateTheOBDRquest() {
        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        OBDRequest obdRequest = new OBDRequest();
        obdRequest.setServiceOption("Random");
        OBDRequestWrapper expectedObdRequest = new OBDRequestWrapper(obdRequest, "subscriptionId", DateTime.now(), Channel.IVR);
        stringObjectHashMap.put("0", expectedObdRequest);
        when(obdServiceOptionFactory.getHandler(ServiceOption.HELP)).thenReturn(serviceOptionHandler);
        ArrayList<String> errors = new ArrayList<String>() {{
            add("Invalid service option");
        }};
        when(obdRequestValidator.validate(expectedObdRequest)).thenReturn(errors);

        obdServiceOptionHandler.handleOBDCallbackRequest(new MotechEvent(OBDEventKeys.PROCESS_CALLBACK_REQUEST, stringObjectHashMap));

        verify(kilkariCampaignService, never()).processSuccessfulMessageDelivery(expectedObdRequest);
        verify(serviceOptionHandler, never()).process(expectedObdRequest);
    }

    @Test
    public void shouldNotInvokeFactoryForHandlerIfServiceOptionIsEmpty() {

        Map<String, Object> map = new HashMap<>();
        OBDRequest obdRequest = new OBDRequest();
        obdRequest.setServiceOption("");
        OBDRequestWrapper obdRequestWrapper = new OBDRequestWrapper(obdRequest, "subscriptionId", DateTime.now(), Channel.IVR);
        map.put("0", obdRequestWrapper);

        obdServiceOptionHandler.handleOBDCallbackRequest(new MotechEvent(OBDEventKeys.PROCESS_CALLBACK_REQUEST, map));

        verify(obdServiceOptionFactory, never()).getHandler(any(ServiceOption.class));
    }
}
