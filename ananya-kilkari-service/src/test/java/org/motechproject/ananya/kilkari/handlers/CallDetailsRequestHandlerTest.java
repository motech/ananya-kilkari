package org.motechproject.ananya.kilkari.handlers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.kilkari.factory.OBDServiceOptionFactory;
import org.motechproject.ananya.kilkari.handlers.callback.obd.ServiceOptionHandler;
import org.motechproject.ananya.kilkari.obd.domain.ServiceOption;
import org.motechproject.ananya.kilkari.obd.service.CampaignMessageService;
import org.motechproject.ananya.kilkari.obd.service.validator.Errors;
import org.motechproject.ananya.kilkari.request.InboxCallDetailsWebRequest;
import org.motechproject.ananya.kilkari.obd.service.request.OBDSuccessfulCallDetailsRequest;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallDetailsWebRequest;
import org.motechproject.ananya.kilkari.service.CallDetailsEventKeys;
import org.motechproject.ananya.kilkari.service.KilkariCallDetailsService;
import org.motechproject.ananya.kilkari.service.KilkariCampaignService;
import org.motechproject.ananya.kilkari.service.validator.CallDetailsRequestValidator;
import org.motechproject.event.MotechEvent;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CallDetailsRequestHandlerTest {

    @Mock
    private OBDServiceOptionFactory obdServiceOptionFactory;
    @Mock
    private KilkariCampaignService kilkariCampaignService;
    @Mock
    private ServiceOptionHandler serviceOptionHandler;
    @Mock
    private CallDetailsRequestValidator successfulCallDetailsRequestValidator;
    @Mock
    private CampaignMessageService campaignMessageService;
    @Mock
    private KilkariCallDetailsService kilkariCallDetailsService;

    private CallDetailsRequestHandler callDetailsRequestHandler;

    @Before
    public void setUp() {
        callDetailsRequestHandler = new CallDetailsRequestHandler(kilkariCallDetailsService);
    }

    @Test
    public void shouldHandleAOBDCallBackRequest() {
        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        OBDSuccessfulCallDetailsWebRequest obdSuccessfulCallDetailsRequest = new OBDSuccessfulCallDetailsWebRequest(null, null, null, ServiceOption.HELP.name());
        obdSuccessfulCallDetailsRequest.setSubscriptionId("subscriptionId");
        stringObjectHashMap.put("0", obdSuccessfulCallDetailsRequest);

        callDetailsRequestHandler.handleOBDCallbackRequest(new MotechEvent(CallDetailsEventKeys.PROCESS_OBD_SUCCESSFUL_CALL_REQUEST_SUBJECT, stringObjectHashMap));

        verify(kilkariCallDetailsService).processSuccessfulMessageDelivery(obdSuccessfulCallDetailsRequest);
    }

    @Test
    public void shouldHandleAOBDCallBackRequestWithDeactivation() {
        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        OBDSuccessfulCallDetailsWebRequest obdSuccessfulCallDetailsRequest = new OBDSuccessfulCallDetailsWebRequest(null, null, null, ServiceOption.UNSUBSCRIBE.name());
        obdSuccessfulCallDetailsRequest.setSubscriptionId("subscriptionId");
        stringObjectHashMap.put("0", obdSuccessfulCallDetailsRequest);

        callDetailsRequestHandler.handleOBDCallbackRequest(new MotechEvent(CallDetailsEventKeys.PROCESS_OBD_SUCCESSFUL_CALL_REQUEST_SUBJECT, stringObjectHashMap));

        verify(kilkariCallDetailsService).processSuccessfulMessageDelivery(obdSuccessfulCallDetailsRequest);
    }

    @Test
    public void shouldNotThrowExceptionIfHandlerIsNotThereForServiceOption() {
        Map<String, Object> map = new HashMap<>();
        OBDSuccessfulCallDetailsWebRequest obdSuccessfulCallDetailsRequest = new OBDSuccessfulCallDetailsWebRequest(null, null, null, "");
        obdSuccessfulCallDetailsRequest.setSubscriptionId("subscriptionId");
        map.put("0", obdSuccessfulCallDetailsRequest);
        when(successfulCallDetailsRequestValidator.validate(any(OBDSuccessfulCallDetailsRequest.class))).thenReturn(new Errors());

        callDetailsRequestHandler.handleOBDCallbackRequest(new MotechEvent(CallDetailsEventKeys.PROCESS_OBD_SUCCESSFUL_CALL_REQUEST_SUBJECT, map));
    }

    @Test
    public void shouldInvokeKilkariCampaignServiceToProcessInboxCallDetails() {
        HashMap<String, Object> parameters = new HashMap<>();
        InboxCallDetailsWebRequest inboxCallDetailsWebRequest = mock(InboxCallDetailsWebRequest.class);
        parameters.put("0", inboxCallDetailsWebRequest);

        callDetailsRequestHandler.handleInboxCallDetailsRequest(new MotechEvent(CallDetailsEventKeys.PROCESS_INBOX_CALL_REQUEST_SUBJECT, parameters));

        verify(kilkariCallDetailsService).processInboxCallDetailsRequest(inboxCallDetailsWebRequest);
    }
}