package org.motechproject.ananya.kilkari.handlers;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.kilkari.factory.OBDServiceOptionFactory;
import org.motechproject.ananya.kilkari.handlers.callback.obd.ServiceOptionHandler;
import org.motechproject.ananya.kilkari.obd.domain.InvalidCallRecord;
import org.motechproject.ananya.kilkari.obd.domain.OBDEventKeys;
import org.motechproject.ananya.kilkari.obd.domain.ServiceOption;
import org.motechproject.ananya.kilkari.obd.request.InvalidOBDRequestEntries;
import org.motechproject.ananya.kilkari.obd.request.InvalidOBDRequestEntry;
import org.motechproject.ananya.kilkari.obd.service.CallRecordsService;
import org.motechproject.ananya.kilkari.obd.service.CampaignMessageService;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallDetailsRequest;
import org.motechproject.ananya.kilkari.service.KilkariCampaignService;
import org.motechproject.ananya.kilkari.subscription.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.validators.Errors;
import org.motechproject.ananya.kilkari.validators.OBDSuccessfulCallRequestValidator;
import org.motechproject.scheduler.domain.MotechEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OBDRequestHandlerTest {

    @Mock
    private OBDServiceOptionFactory obdServiceOptionFactory;
    @Mock
    private KilkariCampaignService kilkariCampaignService;
    @Mock
    private ServiceOptionHandler serviceOptionHandler;
    @Mock
    private OBDSuccessfulCallRequestValidator successfulCallRequestValidator;
    @Mock
    private CampaignMessageService campaignMessageService;
    @Mock
    private CallRecordsService callRecordsService;

    private OBDRequestHandler obdRequestHandler;

    @Before
    public void setUp() {
        obdRequestHandler = new OBDRequestHandler(kilkariCampaignService, callRecordsService);
    }

    @Test
    public void shouldHandleAOBDCallBackRequest() {
        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        OBDSuccessfulCallDetailsRequest obdSuccessfulCallDetailsRequest = new OBDSuccessfulCallDetailsRequest();
        obdSuccessfulCallDetailsRequest.setServiceOption(ServiceOption.HELP.name());
        obdSuccessfulCallDetailsRequest.setSubscriptionId("subscriptionId");
        stringObjectHashMap.put("0", obdSuccessfulCallDetailsRequest);

        obdRequestHandler.handleOBDCallbackRequest(new MotechEvent(OBDEventKeys.PROCESS_SUCCESSFUL_CALL_REQUEST_SUBJECT, stringObjectHashMap));

        verify(kilkariCampaignService).processSuccessfulMessageDelivery(obdSuccessfulCallDetailsRequest);
    }

    @Test
    public void shouldHandleAOBDCallBackRequestWithDeactivation() {
        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        OBDSuccessfulCallDetailsRequest obdSuccessfulCallDetailsRequest = new OBDSuccessfulCallDetailsRequest();
        obdSuccessfulCallDetailsRequest.setServiceOption(ServiceOption.UNSUBSCRIBE.name());
        obdSuccessfulCallDetailsRequest.setSubscriptionId("subscriptionId");
        stringObjectHashMap.put("0", obdSuccessfulCallDetailsRequest);

        obdRequestHandler.handleOBDCallbackRequest(new MotechEvent(OBDEventKeys.PROCESS_SUCCESSFUL_CALL_REQUEST_SUBJECT, stringObjectHashMap));

        verify(kilkariCampaignService).processSuccessfulMessageDelivery(obdSuccessfulCallDetailsRequest);
    }

    @Test
    public void shouldNotThrowExceptionIfHandlerIsNotThereForServiceOption() {
        Map<String, Object> map = new HashMap<>();
        OBDSuccessfulCallDetailsRequest obdSuccessfulCallDetailsRequest = new OBDSuccessfulCallDetailsRequest();
        obdSuccessfulCallDetailsRequest.setServiceOption("");
        obdSuccessfulCallDetailsRequest.setSubscriptionId("subscriptionId");
        map.put("0", obdSuccessfulCallDetailsRequest);
        when(successfulCallRequestValidator.validate(obdSuccessfulCallDetailsRequest)).thenReturn(new Errors());

        obdRequestHandler.handleOBDCallbackRequest(new MotechEvent(OBDEventKeys.PROCESS_INVALID_CALL_RECORDS_REQUEST_SUBJECT, map));
    }

    @Test
    public void shouldProcessInvalidCallRecords() {
        String msisdn1 = "msisdn1";
        String campaign1 = "campaign1";
        String desc1 = "desc1";
        String operator1 = "operator1";
        String sub1 = "sub1";

        InvalidOBDRequestEntries invalidOBDRequestEntries = Mockito.mock(InvalidOBDRequestEntries.class);
        ArrayList<InvalidOBDRequestEntry> invalidCallRecordRequestObjects = new ArrayList<InvalidOBDRequestEntry>();
        InvalidOBDRequestEntry invalidOBDRequestEntry1 = Mockito.mock(InvalidOBDRequestEntry.class);
        when(invalidOBDRequestEntry1.getMsisdn()).thenReturn(msisdn1);
        when(invalidOBDRequestEntry1.getCampaignId()).thenReturn(campaign1);
        when(invalidOBDRequestEntry1.getDescription()).thenReturn(desc1);
        when(invalidOBDRequestEntry1.getOperator()).thenReturn(operator1);
        when(invalidOBDRequestEntry1.getSubscriptionId()).thenReturn(sub1);

        InvalidOBDRequestEntry invalidOBDRequestEntry2 = Mockito.mock(InvalidOBDRequestEntry.class);
        invalidCallRecordRequestObjects.add(invalidOBDRequestEntry1);
        invalidCallRecordRequestObjects.add(invalidOBDRequestEntry2);


        when(invalidOBDRequestEntries.getInvalidOBDRequestEntryList()).thenReturn(invalidCallRecordRequestObjects);

        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("0", invalidOBDRequestEntries);

        obdRequestHandler.handleInvalidCallRecordsRequest(new MotechEvent(OBDEventKeys.PROCESS_INVALID_CALL_RECORDS_REQUEST_SUBJECT, parameters));

        ArgumentCaptor<ArrayList> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(callRecordsService).processInvalidCallRecords(captor.capture());

        ArrayList actualInvalidCallRecords = captor.getValue();
        assertEquals(2, actualInvalidCallRecords.size());
        InvalidCallRecord actualInvalidCallRecord1 = (InvalidCallRecord) actualInvalidCallRecords.get(0);
        assertEquals(campaign1, actualInvalidCallRecord1.getCampaignId());
        assertEquals(sub1, actualInvalidCallRecord1.getSubscriptionId());
        assertEquals(msisdn1, actualInvalidCallRecord1.getMsisdn());
        assertEquals(operator1, actualInvalidCallRecord1.getOperator());
        assertEquals(desc1, actualInvalidCallRecord1.getDescription());
    }
}