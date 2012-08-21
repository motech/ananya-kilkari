package org.motechproject.ananya.kilkari.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.motechproject.ananya.kilkari.factory.OBDServiceOptionFactory;
import org.motechproject.ananya.kilkari.handlers.callback.obd.ServiceOptionHandler;
import org.motechproject.ananya.kilkari.obd.domain.ServiceOption;
import org.motechproject.ananya.kilkari.obd.service.OBDService;
import org.motechproject.ananya.kilkari.obd.service.request.FailedCallReports;
import org.motechproject.ananya.kilkari.obd.service.request.InvalidOBDRequestEntries;
import org.motechproject.ananya.kilkari.obd.service.request.OBDSuccessfulCallDetailsRequest;
import org.motechproject.ananya.kilkari.obd.service.validator.Errors;
import org.motechproject.ananya.kilkari.request.CallDurationWebRequest;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallDetailsWebRequest;
import org.motechproject.ananya.kilkari.service.validator.CallDetailsRequestValidator;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class KilkariCallDetailsServiceTest {

    @Mock
    private OBDService obdService;
    @Mock
    private CallDetailsRequestValidator callDetailsRequestValidator;
    @Mock
    private OBDServiceOptionFactory obdServiceOptionFactory;
    @Mock
    private ServiceOptionHandler serviceOptionHandler;


    private KilkariCallDetailsService kilkariCallDetailsService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        kilkariCallDetailsService = new KilkariCallDetailsService(obdService, callDetailsRequestValidator, obdServiceOptionFactory);
    }

    @Test
    public void shouldProcessInvalidCallRecords() {
        InvalidOBDRequestEntries invalidOBDRequestEntries = new InvalidOBDRequestEntries();

        kilkariCallDetailsService.processInvalidOBDRequestEntries(invalidOBDRequestEntries);

        verify(obdService).processInvalidOBDRequestEntries(invalidOBDRequestEntries);
    }

    @Test
    public void shouldPublishCallDeliveryFailureRecords() {
        FailedCallReports failedCallReports = Mockito.mock(FailedCallReports.class);

        kilkariCallDetailsService.processCallDeliveryFailureRequest(failedCallReports);

        verify(obdService).processCallDeliveryFailure(failedCallReports);
    }

    @Test
    public void shouldProcessSuccessfulCampaignMessageDelivery() {
        OBDSuccessfulCallDetailsWebRequest obdSuccessfulCallDetailsWebRequest = Mockito.mock(OBDSuccessfulCallDetailsWebRequest.class);

        String subscriptionId = "subscriptionId";
        String campaignId = "WEEK1";
        String msisdn = "1234567890";
        String serviceOption = ServiceOption.HELP.name();
        String startTime = "25-12-2012 12-13-14";
        String endTime = "27-12-2012 12-15-19";
        CallDurationWebRequest callDurationWebRequest = new CallDurationWebRequest(startTime, endTime);

        when(obdSuccessfulCallDetailsWebRequest.getMsisdn()).thenReturn(msisdn);
        when(obdSuccessfulCallDetailsWebRequest.getCampaignId()).thenReturn(campaignId);
        when(obdSuccessfulCallDetailsWebRequest.getServiceOption()).thenReturn(serviceOption);
        when(obdSuccessfulCallDetailsWebRequest.getCallDurationWebRequest()).thenReturn(callDurationWebRequest);
        when(obdSuccessfulCallDetailsWebRequest.getSubscriptionId()).thenReturn(subscriptionId);

        when(callDetailsRequestValidator.validate(any(OBDSuccessfulCallDetailsRequest.class))).thenReturn(new Errors());
        when(obdServiceOptionFactory.getHandler(ServiceOption.HELP)).thenReturn(serviceOptionHandler);
        when(obdSuccessfulCallDetailsWebRequest.validate()).thenReturn(new Errors());
        when(obdService.processSuccessfulCallDelivery(any(OBDSuccessfulCallDetailsRequest.class))).thenReturn(true);

        kilkariCallDetailsService.processSuccessfulMessageDelivery(obdSuccessfulCallDetailsWebRequest);

        InOrder inOrder = Mockito.inOrder(obdSuccessfulCallDetailsWebRequest, callDetailsRequestValidator);
        inOrder.verify(obdSuccessfulCallDetailsWebRequest).validate();
        inOrder.verify(callDetailsRequestValidator).validate(any(OBDSuccessfulCallDetailsRequest.class));

        verify(serviceOptionHandler).process(any(OBDSuccessfulCallDetailsRequest.class));
    }

    @Test
    public void shouldNotProcessSuccessfulCampaignMessageDeliveryIfThereIsNoSubscriptionAvailable() {
        String subscriptionId = "subscriptionId";
        String campaignId = "WEEK1";
        String msisdn = "1234567890";
        String serviceOption = ServiceOption.HELP.name();
        String startTime = "25-12-2012 12-13-14";
        String endTime = "27-12-2012 12-15-19";
        CallDurationWebRequest callDurationWebRequest = new CallDurationWebRequest(startTime, endTime);
        OBDSuccessfulCallDetailsWebRequest obdSuccessfulCallDetailsRequest = new OBDSuccessfulCallDetailsWebRequest(msisdn, campaignId, callDurationWebRequest, serviceOption);

        obdSuccessfulCallDetailsRequest.setSubscriptionId(subscriptionId);
        when(callDetailsRequestValidator.validate(any(OBDSuccessfulCallDetailsRequest.class))).thenReturn(new Errors());
        when(obdService.processSuccessfulCallDelivery(Mockito.any(OBDSuccessfulCallDetailsRequest.class))).thenReturn(false);

        kilkariCallDetailsService.processSuccessfulMessageDelivery(obdSuccessfulCallDetailsRequest);

        verify(obdServiceOptionFactory, never()).getHandler(ServiceOption.HELP);
    }
}
