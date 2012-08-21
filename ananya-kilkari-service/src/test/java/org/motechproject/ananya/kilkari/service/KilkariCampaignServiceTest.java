package org.motechproject.ananya.kilkari.service;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.motechproject.ananya.kilkari.factory.OBDServiceOptionFactory;
import org.motechproject.ananya.kilkari.handlers.callback.obd.ServiceOptionHandler;
import org.motechproject.ananya.kilkari.message.service.CampaignMessageAlertService;
import org.motechproject.ananya.kilkari.message.service.InboxService;
import org.motechproject.ananya.kilkari.messagecampaign.service.MessageCampaignService;
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
import org.motechproject.ananya.kilkari.subscription.builder.SubscriptionBuilder;
import org.motechproject.ananya.kilkari.subscription.domain.Operator;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionStatus;
import org.motechproject.ananya.kilkari.subscription.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.subscription.validators.DateUtils;
import org.motechproject.ananya.reports.kilkari.contract.request.CallDetailsReportRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class KilkariCampaignServiceTest {

    private KilkariCampaignService kilkariCampaignService;

    @Mock
    private MessageCampaignService messageCampaignService;
    @Mock
    private KilkariSubscriptionService kilkariSubscriptionService;
    @Mock
    private ReportingService reportingService;
    @Mock
    private CallDetailsRequestPublisher callDetailsRequestPublisher;
    @Mock
    private InboxService inboxService;
    @Mock
    private OBDServiceOptionFactory obdServiceOptionFactory;
    @Mock
    private ServiceOptionHandler serviceOptionHandler;
    @Mock
    private CallDetailsRequestValidator successfulCallDetailsRequestValidator;
    @Mock
    private CampaignMessageAlertService campaignMessageAlertService;
    @Mock
    private OBDService obdService;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        initMocks(this);
        kilkariCampaignService = new KilkariCampaignService(messageCampaignService, kilkariSubscriptionService,
                campaignMessageAlertService, reportingService, callDetailsRequestPublisher,
                inboxService, obdServiceOptionFactory, successfulCallDetailsRequestValidator,
                obdService);
    }

    @Test
    public void shouldGetMessageTimings() {
        String msisdn = "1234567890";
        List<Subscription> subscriptions = new ArrayList<>();

        DateTime now = DateTime.now();
        DateTime subscriptionStartDate1 = now.plusWeeks(2);
        DateTime subscriptionStartDate2 = now.plusWeeks(3);
        Subscription subscription1 = new SubscriptionBuilder().withDefaults().withCreationDate(now)
                .withStartDate(subscriptionStartDate1).withStatus(SubscriptionStatus.ACTIVE).build();
        Subscription subscription2 = new SubscriptionBuilder().withDefaults().withCreationDate(now)
                .withStartDate(subscriptionStartDate2).withStatus(SubscriptionStatus.ACTIVE).build();
        subscriptions.add(subscription1);
        subscriptions.add(subscription2);

        List<DateTime> dateTimes = new ArrayList<>();
        dateTimes.add(now);

        when(kilkariSubscriptionService.findByMsisdn(msisdn)).thenReturn(subscriptions);

        when(messageCampaignService.getMessageTimings(
                subscription1.getSubscriptionId(),
                subscription1.getCreationDate(),
                subscription1.endDate())).thenReturn(dateTimes);
        when(messageCampaignService.getMessageTimings(
                subscription2.getSubscriptionId(),
                subscription2.getCreationDate(),
                subscription2.endDate())).thenReturn(dateTimes);

        Map<String, List<DateTime>> messageTimings = kilkariCampaignService.getMessageTimings(msisdn);

        verify(messageCampaignService).getMessageTimings(
                eq(subscription1.getSubscriptionId()),
                eq(subscription1.getCreationDate()),
                eq(subscription1.endDate()));

        verify(messageCampaignService).getMessageTimings(
                eq(subscription2.getSubscriptionId()),
                eq(subscription2.getCreationDate()),
                eq(subscription2.endDate()));

        assertThat(messageTimings.size(), is(2));
        assertThat(messageTimings, hasEntry(subscription1.getSubscriptionId(), dateTimes));
        assertThat(messageTimings, hasEntry(subscription2.getSubscriptionId(), dateTimes));
    }

    @Test
    public void shouldPublishObdCallbackRequest() {
        OBDSuccessfulCallDetailsWebRequest obdSuccessfulCallDetailsRequest = new OBDSuccessfulCallDetailsWebRequest();

        kilkariCampaignService.publishSuccessfulCallRequest(obdSuccessfulCallDetailsRequest);

        verify(callDetailsRequestPublisher).publishSuccessfulCallRequest(obdSuccessfulCallDetailsRequest);
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

        when(successfulCallDetailsRequestValidator.validate(any(OBDSuccessfulCallDetailsRequest.class))).thenReturn(new Errors());
        when(obdServiceOptionFactory.getHandler(ServiceOption.HELP)).thenReturn(serviceOptionHandler);
        when(obdSuccessfulCallDetailsWebRequest.validate()).thenReturn(new Errors());
        when(obdService.processSuccessfulCallDelivery(any(OBDSuccessfulCallDetailsRequest.class))).thenReturn(true);

        kilkariCampaignService.processSuccessfulMessageDelivery(obdSuccessfulCallDetailsWebRequest);

        InOrder inOrder = Mockito.inOrder(obdSuccessfulCallDetailsWebRequest, successfulCallDetailsRequestValidator, reportingService);
        inOrder.verify(obdSuccessfulCallDetailsWebRequest).validate();
        inOrder.verify(successfulCallDetailsRequestValidator).validate(any(OBDSuccessfulCallDetailsRequest.class));

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
        when(successfulCallDetailsRequestValidator.validate(any(OBDSuccessfulCallDetailsRequest.class))).thenReturn(new Errors());
        when(obdService.processSuccessfulCallDelivery(Mockito.any(OBDSuccessfulCallDetailsRequest.class))).thenReturn(false);

        kilkariCampaignService.processSuccessfulMessageDelivery(obdSuccessfulCallDetailsRequest);

        verify(obdServiceOptionFactory, never()).getHandler(ServiceOption.HELP);
    }

    @Test
    public void shouldProcessInvalidCallRecords() {
        InvalidOBDRequestEntries invalidOBDRequestEntries = new InvalidOBDRequestEntries();

        kilkariCampaignService.processInvalidOBDRequestEntries(invalidOBDRequestEntries);

        verify(obdService).processInvalidOBDRequestEntries(invalidOBDRequestEntries);
    }

    @Test
    public void shouldScheduleUnsubscriptionWhenPackIsCompletedAndWhenStatusIsNotDeactivated() {
        String subscriptionId = "abcd1234";

        Subscription subscription = new Subscription("9988776655", SubscriptionPack.BARI_KILKARI, DateTime.now().minusWeeks(1), DateTime.now());
        subscription.setStatus(SubscriptionStatus.ACTIVE);

        when(kilkariSubscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(subscription);

        kilkariCampaignService.processCampaignCompletion(subscriptionId);

        verify(kilkariSubscriptionService).processSubscriptionCompletion(subscription);
    }

    @Test
    public void shouldNotScheduleUnsubscriptionWhenPackIsCompletedAndStatusIsDeactivated() {
        String subscriptionId = "abcd1234";
        Subscription subscription = new Subscription("9988776655", SubscriptionPack.BARI_KILKARI, DateTime.now().minusWeeks(1), DateTime.now());
        subscription.setStatus(SubscriptionStatus.PENDING_DEACTIVATION);

        when(kilkariSubscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(subscription);

        kilkariCampaignService.processCampaignCompletion(subscriptionId);

        verify(kilkariSubscriptionService, never()).processSubscriptionCompletion(subscription);
    }

    @Test
    public void shouldPublishCallDeliveryFailureRecords() {
        FailedCallReports failedCallReports = Mockito.mock(FailedCallReports.class);

        kilkariCampaignService.processCallDeliveryFailureRequest(failedCallReports);

        verify(obdService).processCallDeliveryFailure(failedCallReports);
    }

    @Test
    public void shouldCallCampaignMessageAlertServiceAndUpdateInboxToHoldLastScheduledMessage() {
        DateTime creationDate = DateTime.now();
        String messageId = "WEEK1";
        String campaignName = MessageCampaignService.FIFTEEN_MONTHS_CAMPAIGN_KEY;
        Operator operator = Operator.AIRTEL;
        String msisdn = "9988776655";
        Subscription subscription = new Subscription(msisdn, SubscriptionPack.BARI_KILKARI, creationDate, DateTime.now());
        subscription.setOperator(operator);
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        String subscriptionId = subscription.getSubscriptionId();
        DateTime expiryDate = creationDate.plusWeeks(1);

        when(kilkariSubscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(subscription);
        when(messageCampaignService.getCampaignStartDate(subscriptionId, campaignName)).thenReturn(creationDate);

        kilkariCampaignService.scheduleWeeklyMessage(subscriptionId, campaignName);

        verify(campaignMessageAlertService).scheduleCampaignMessageAlert(eq(subscriptionId), eq(messageId), Mockito.argThat(dateMatches(expiryDate)), eq(msisdn), eq(operator.name()));
        verify(inboxService).newMessage(subscriptionId, messageId);
    }

    @Test
    public void shouldCallCampaignMessageAlertServiceAndNotUpdateInboxWhenSubscriptionIsNotActive() {
        DateTime creationDate = DateTime.now();
        String msisdn = "9988776655";
        Subscription subscription = new Subscription(msisdn, SubscriptionPack.BARI_KILKARI, creationDate, DateTime.now());
        Operator operator = Operator.AIRTEL;
        subscription.setOperator(operator);
        subscription.setStatus(SubscriptionStatus.PENDING_ACTIVATION);
        String subscriptionId = subscription.getSubscriptionId();
        String messageId = "WEEK1";
        String campaignName = MessageCampaignService.FIFTEEN_MONTHS_CAMPAIGN_KEY;
        DateTime expiryDate = creationDate.plusWeeks(1);

        when(kilkariSubscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(subscription);
        when(messageCampaignService.getCampaignStartDate(subscriptionId, campaignName)).thenReturn(creationDate);

        kilkariCampaignService.scheduleWeeklyMessage(subscriptionId, campaignName);

        verify(campaignMessageAlertService).scheduleCampaignMessageAlert(eq(subscriptionId), eq(messageId), Mockito.argThat(dateMatches(expiryDate)), eq(msisdn), eq(operator.name()));
        verify(inboxService, never()).newMessage(subscriptionId, messageId);
    }


    private Matcher<DateTime> dateMatches(final DateTime expiryDate) {
        return new TypeSafeMatcher<DateTime>() {
            @Override
            public boolean matchesSafely(DateTime dateTime) {
                return expiryDate.toLocalDate().equals(dateTime.toLocalDate());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("ExpiryDate expected :" + expiryDate);

            }
        };
    }

    @Test
    public void shouldNotUpdateInboxDuringActivationWhenMessageHasNotAlreadyBeenScheduled() {
        String msisdn = "1234567890";
        Subscription subscription = new Subscription(msisdn, SubscriptionPack.BARI_KILKARI, DateTime.now(), DateTime.now());
        subscription.setStatus(SubscriptionStatus.NEW);

        String subscriptionId = subscription.getSubscriptionId();
        Operator operator = Operator.AIRTEL;
        subscription.setOperator(operator);

        when(kilkariSubscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(subscription);

        kilkariCampaignService.activateSchedule(subscriptionId);

        verify(inboxService, never()).newMessage(anyString(), anyString());
    }

    @Test
    public void shouldUpdateInboxDuringActivationWhenMessageHasAlreadyBeenScheduled() {
        String msisdn = "1234567890";
        Subscription subscription = new Subscription(msisdn, SubscriptionPack.BARI_KILKARI, DateTime.now(), DateTime.now());
        subscription.setStatus(SubscriptionStatus.NEW);

        String subscriptionId = subscription.getSubscriptionId();
        Operator operator = Operator.AIRTEL;
        subscription.setOperator(operator);
        String messageId = "mesasgeId";

        when(kilkariSubscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(subscription);
        when(campaignMessageAlertService.scheduleCampaignMessageAlertForActivation(subscriptionId, msisdn, operator.name())).thenReturn(messageId);

        kilkariCampaignService.activateSchedule(subscriptionId);

        verify(inboxService).newMessage(subscriptionId, messageId);
    }

    @Test
    public void shouldNotUpdateInboxDuringRenewalWhenMessageHasNotAlreadyBeenScheduled() {
        String msisdn = "1234567890";
        Subscription subscription = new Subscription(msisdn, SubscriptionPack.BARI_KILKARI, DateTime.now(), DateTime.now());
        subscription.setStatus(SubscriptionStatus.NEW);
        String subscriptionId = subscription.getSubscriptionId();
        Operator operator = Operator.AIRTEL;
        subscription.setOperator(operator);

        when(kilkariSubscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(subscription);

        kilkariCampaignService.renewSchedule(subscriptionId);

        verify(inboxService, never()).newMessage(anyString(), anyString());
    }

    @Test
    public void shouldNotUpdateInboxDuringRenewalnWhenMessageHasAlreadyBeenScheduled() {
        String msisdn = "1234567890";
        Subscription subscription = new Subscription(msisdn, SubscriptionPack.BARI_KILKARI, DateTime.now(), DateTime.now());
        subscription.setStatus(SubscriptionStatus.NEW);

        String subscriptionId = subscription.getSubscriptionId();
        Operator operator = Operator.AIRTEL;
        subscription.setOperator(operator);
        String messageId = "mesasgeId";

        when(kilkariSubscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(subscription);
        when(campaignMessageAlertService.scheduleCampaignMessageAlertForActivation(subscriptionId, msisdn, operator.name())).thenReturn(messageId);

        kilkariCampaignService.renewSchedule(subscriptionId);

        verify(inboxService, never()).newMessage(anyString(), anyString());
    }

    @Test
    public void shouldCallCampaignMessageAlertServiceOnActivation() {
        String msisdn = "1234567890";
        Subscription subscription = new Subscription(msisdn, SubscriptionPack.BARI_KILKARI, DateTime.now(), DateTime.now());
        subscription.setStatus(SubscriptionStatus.NEW);
        String subscriptionId = subscription.getSubscriptionId();
        Operator operator = Operator.AIRTEL;
        subscription.setOperator(operator);

        when(kilkariSubscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(subscription);
        kilkariCampaignService.activateSchedule(subscriptionId);

        verify(campaignMessageAlertService).scheduleCampaignMessageAlertForActivation(subscriptionId, msisdn, operator.name());
    }

    @Test
    public void shouldCallCampaignMessageAlertServiceOnRenewal() {
        String msisdn = "1234567890";
        Subscription subscription = new Subscription(msisdn, SubscriptionPack.BARI_KILKARI, DateTime.now(), DateTime.now());
        subscription.setStatus(SubscriptionStatus.NEW);

        String subscriptionId = subscription.getSubscriptionId();
        Operator operator = Operator.AIRTEL;
        subscription.setOperator(operator);

        when(kilkariSubscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(subscription);
        kilkariCampaignService.renewSchedule(subscriptionId);

        verify(campaignMessageAlertService).scheduleCampaignMessageAlertForRenewal(subscriptionId, msisdn, operator.name());
    }

    @Test
    public void shouldPublishInboxCallDetails() {
        InboxCallDetailsWebRequest inboxCallDetailsWebRequest = new InboxCallDetailsWebRequest();

        kilkariCampaignService.publishInboxCallDetailsRequest(inboxCallDetailsWebRequest);

        verify(callDetailsRequestPublisher).publishInboxCallDetailsRequest(inboxCallDetailsWebRequest);
    }

    @Test
    public void shouldValidateSubscriptionIdIfRequestValidationIsSuccessful() {
        String pack = "choti_kilkari";
        String msisdn = "1234567890";
        String subscriptionId = "subscriptionId";
        InboxCallDetailsWebRequest inboxCallDetailsWebRequest = new InboxCallDetailsWebRequest(msisdn, "WEEK12", new CallDurationWebRequest("22-11-2011 11-55-35", "23-12-2012 12-59-34"), pack, subscriptionId);
        when(kilkariSubscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(null);
        expectedException.expect(ValidationException.class);
        expectedException.expectMessage("Invalid inbox call details request: Subscription not found");

        kilkariCampaignService.processInboxCallDetailsRequest(inboxCallDetailsWebRequest);
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
        when(kilkariSubscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(subscription);
        when(subscription.getSubscriptionId()).thenReturn(subscriptionId);

        kilkariCampaignService.processInboxCallDetailsRequest(inboxCallDetailsWebRequest);

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

        kilkariCampaignService.processInboxCallDetailsRequest(inboxCallDetailsWebRequest);
    }
}