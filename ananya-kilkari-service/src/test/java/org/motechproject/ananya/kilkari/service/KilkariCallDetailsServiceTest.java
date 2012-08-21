package org.motechproject.ananya.kilkari.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.*;
import org.motechproject.ananya.kilkari.factory.OBDServiceOptionFactory;
import org.motechproject.ananya.kilkari.handlers.callback.obd.ServiceOptionHandler;
import org.motechproject.ananya.kilkari.obd.domain.ServiceOption;
import org.motechproject.ananya.kilkari.obd.service.OBDService;
import org.motechproject.ananya.kilkari.obd.service.request.FailedCallReports;
import org.motechproject.ananya.kilkari.obd.service.request.InvalidOBDRequestEntries;
import org.motechproject.ananya.kilkari.obd.service.request.OBDSuccessfulCallDetailsRequest;
import org.motechproject.ananya.kilkari.obd.service.validator.Errors;
import org.motechproject.ananya.kilkari.reporting.service.ReportingService;
import org.motechproject.ananya.kilkari.request.CallDurationWebRequest;
import org.motechproject.ananya.kilkari.request.InboxCallDetailsWebRequest;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallDetailsWebRequest;
import org.motechproject.ananya.kilkari.service.validator.CallDetailsRequestValidator;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.subscription.service.SubscriptionService;
import org.motechproject.ananya.kilkari.subscription.validators.DateUtils;
import org.motechproject.ananya.reports.kilkari.contract.request.CallDetailsReportRequest;

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
    @Mock
    private SubscriptionService subscriptionService;
    @Mock
    private ReportingService reportingService;
    @Mock
    private CallDetailsRequestPublisher callDetailsRequestPublisher;


    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private KilkariCallDetailsService kilkariCallDetailsService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        kilkariCallDetailsService = new KilkariCallDetailsService(obdService, subscriptionService,
                reportingService, callDetailsRequestValidator, obdServiceOptionFactory, callDetailsRequestPublisher);
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

    @Test
    public void shouldValidateSubscriptionIdIfRequestValidationIsSuccessful() {
        String pack = "choti_kilkari";
        String msisdn = "1234567890";
        String subscriptionId = "subscriptionId";
        InboxCallDetailsWebRequest inboxCallDetailsWebRequest = new InboxCallDetailsWebRequest(msisdn, "WEEK12", new CallDurationWebRequest("22-11-2011 11-55-35", "23-12-2012 12-59-34"), pack, subscriptionId);
        when(subscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(null);
        expectedException.expect(ValidationException.class);
        expectedException.expectMessage("Invalid inbox call details request: Subscription not found");

        kilkariCallDetailsService.processInboxCallDetailsRequest(inboxCallDetailsWebRequest);
    }

    @Test
    public void shouldReportInboxCallDetails() {
        String pack = "choti_kilkari";
        String msisdn = "1234567890";
        String subscriptionId = "subscriptionId";
        String campaignId = "WEEK12";
        String startTime = "22-11-2011 11-55-35";
        String endTime = "23-12-2012 12-59-34";
        InboxCallDetailsWebRequest inboxCallDetailsWebRequest = new InboxCallDetailsWebRequest(msisdn, campaignId, new CallDurationWebRequest(startTime, endTime), pack, subscriptionId);
        Subscription subscription = Mockito.mock(Subscription.class);
        when(subscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(subscription);
        when(subscription.getSubscriptionId()).thenReturn(subscriptionId);

        kilkariCallDetailsService.processInboxCallDetailsRequest(inboxCallDetailsWebRequest);

        ArgumentCaptor<CallDetailsReportRequest> captor = ArgumentCaptor.forClass(CallDetailsReportRequest.class);
        verify(reportingService).reportCampaignMessageDeliveryStatus(captor.capture());
        CallDetailsReportRequest callDetailsReportRequest = captor.getValue();

        Assert.assertEquals(msisdn, callDetailsReportRequest.getMsisdn());
        Assert.assertEquals("INBOX", callDetailsReportRequest.getCallSource());
        Assert.assertEquals(campaignId, callDetailsReportRequest.getCampaignId());
        Assert.assertNull(callDetailsReportRequest.getRetryCount());
        Assert.assertNull(callDetailsReportRequest.getServiceOption());
        Assert.assertEquals(DateUtils.parseDateTime(startTime), callDetailsReportRequest.getStartTime());
        Assert.assertEquals(DateUtils.parseDateTime(endTime), callDetailsReportRequest.getEndTime());
        Assert.assertEquals("SUCCESS", callDetailsReportRequest.getStatus());
        Assert.assertEquals(subscriptionId, callDetailsReportRequest.getSubscriptionId());

        Assert.assertNull(callDetailsReportRequest.getServiceOption());
    }

    @Test
    public void shouldValidateInboxCallDetailsRequest() {
        InboxCallDetailsWebRequest inboxCallDetailsWebRequest = Mockito.mock(InboxCallDetailsWebRequest.class);
        Errors errors = new Errors();
        String errorString = "some error";
        errors.add(errorString);
        when(inboxCallDetailsWebRequest.validate()).thenReturn(errors);
        expectedException.expect(ValidationException.class);
        expectedException.expectMessage("Invalid inbox call details request: " + errorString);

        kilkariCallDetailsService.processInboxCallDetailsRequest(inboxCallDetailsWebRequest);
    }

    @Test
    public void shouldPublishObdCallbackRequest() {
        OBDSuccessfulCallDetailsWebRequest obdSuccessfulCallDetailsRequest = new OBDSuccessfulCallDetailsWebRequest();

        kilkariCallDetailsService.publishSuccessfulCallRequest(obdSuccessfulCallDetailsRequest);

        verify(callDetailsRequestPublisher).publishSuccessfulCallRequest(obdSuccessfulCallDetailsRequest);
    }

    @Test
    public void shouldPublishInboxCallDetails() {
        InboxCallDetailsWebRequest inboxCallDetailsWebRequest = new InboxCallDetailsWebRequest();

        kilkariCallDetailsService.publishInboxCallDetailsRequest(inboxCallDetailsWebRequest);

        verify(callDetailsRequestPublisher).publishInboxCallDetailsRequest(inboxCallDetailsWebRequest);
    }
}
