package org.motechproject.ananya.kilkari.subscription.service;

import junit.framework.Assert;
import org.joda.time.DateTime;
import org.joda.time.Weeks;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.contract.request.SubscriberReportRequest;
import org.motechproject.ananya.kilkari.contract.request.SubscriptionChangePackRequest;
import org.motechproject.ananya.kilkari.contract.request.SubscriptionReportRequest;
import org.motechproject.ananya.kilkari.contract.request.SubscriptionStateChangeRequest;
import org.motechproject.ananya.kilkari.contract.response.SubscriberResponse;
import org.motechproject.ananya.kilkari.message.repository.AllInboxMessages;
import org.motechproject.ananya.kilkari.message.service.CampaignMessageAlertService;
import org.motechproject.ananya.kilkari.message.service.InboxService;
import org.motechproject.ananya.kilkari.messagecampaign.request.MessageCampaignRequest;
import org.motechproject.ananya.kilkari.messagecampaign.service.MessageCampaignService;
import org.motechproject.ananya.kilkari.obd.service.CampaignMessageService;
import org.motechproject.ananya.kilkari.reporting.service.ReportingServiceImpl;
import org.motechproject.ananya.kilkari.subscription.builder.SubscriptionBuilder;
import org.motechproject.ananya.kilkari.subscription.builder.SubscriptionRequestBuilder;
import org.motechproject.ananya.kilkari.subscription.domain.*;
import org.motechproject.ananya.kilkari.subscription.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.subscription.repository.AllSubscriptions;
import org.motechproject.ananya.kilkari.subscription.repository.KilkariPropertiesData;
import org.motechproject.ananya.kilkari.subscription.repository.OnMobileSubscriptionGateway;
import org.motechproject.ananya.kilkari.subscription.request.OMSubscriptionRequest;
import org.motechproject.ananya.kilkari.subscription.service.request.ChangeMsisdnRequest;
import org.motechproject.ananya.kilkari.subscription.service.request.ChangePackRequest;
import org.motechproject.ananya.kilkari.subscription.service.request.Location;
import org.motechproject.ananya.kilkari.subscription.service.request.SubscriberRequest;
import org.motechproject.ananya.kilkari.subscription.service.request.SubscriptionRequest;
import org.motechproject.ananya.kilkari.subscription.validators.ChangeMsisdnValidator;
import org.motechproject.ananya.kilkari.subscription.validators.SubscriptionValidator;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.RunOnceSchedulableJob;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class SubscriptionServiceTest {

    private SubscriptionService subscriptionService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Mock
    private AllSubscriptions allSubscriptions;
    @Mock
    private OnMobileSubscriptionManagerPublisher onMobileSubscriptionManagerPublisher;
    @Mock
    private SubscriptionValidator subscriptionValidator;
    @Mock
    private ReportingServiceImpl reportingServiceImpl;
    @Mock
    private AllInboxMessages allInboxMessages;
    @Mock
    private InboxService inboxService;
    @Mock
    private MessageCampaignService messageCampaignService;
    @Mock
    private OnMobileSubscriptionGateway onMobileSubscriptionGateway;
    @Mock
    private CampaignMessageService campaignMessageService;
    @Mock
    private CampaignMessageAlertService campaignMessageAlertService;
    @Mock
    private KilkariPropertiesData kilkariPropertiesData;
    @Mock
    private MotechSchedulerService motechSchedulerService;
    @Mock
    private ChangeMsisdnValidator changeMsisdnValidator;
    @Mock
    private ChangePackProcessor changePackProcessor;

    @Before
    public void setUp() {
        initMocks(this);
        subscriptionService = new SubscriptionService(allSubscriptions, onMobileSubscriptionManagerPublisher, subscriptionValidator, reportingServiceImpl,
                inboxService, messageCampaignService, onMobileSubscriptionGateway, campaignMessageService, campaignMessageAlertService, kilkariPropertiesData, motechSchedulerService, changePackProcessor, changeMsisdnValidator);
    }

    @Test
    public void shouldCreateNewSubscription() {
        String msisdn = "1234567890";
        Channel channel = Channel.IVR;
        SubscriptionPack subscriptionPack = SubscriptionPack.CHOTI_KILKARI;
        ArgumentCaptor<Subscription> subscriptionArgumentCaptor = ArgumentCaptor.forClass(Subscription.class);
        SubscriptionRequest subscription = new SubscriptionRequestBuilder().withDefaults().withMsisdn(msisdn).withPack(subscriptionPack).build();

        Subscription createdSubscription = subscriptionService.createSubscriptionWithReporting(subscription, channel);

        assertNotNull(createdSubscription);
        verify(allSubscriptions).add(subscriptionArgumentCaptor.capture());
        Subscription subscriptionSaved = subscriptionArgumentCaptor.getValue();
        assertEquals(msisdn, subscriptionSaved.getMsisdn());
        assertEquals(subscriptionPack, subscriptionSaved.getPack());
        assertEquals(createdSubscription, subscriptionSaved);
    }

    @Test
    public void shouldValidateSubscriptionRequestOnCreation() {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withDefaults().build();

        subscriptionService.createSubscriptionWithReporting(subscriptionRequest, Channel.CALL_CENTER);

        verify(subscriptionValidator).validate(any(SubscriptionRequest.class));
    }

    @Test
    public void shouldNotCreateSubscriptionIfValidationFails() {
        SubscriptionRequest subscription = new SubscriptionRequestBuilder().withDefaults().build();
        doThrow(new ValidationException("")).when(subscriptionValidator).validate(any(SubscriptionRequest.class));

        try {
            subscriptionService.createSubscriptionWithReporting(subscription, Channel.CALL_CENTER);
        } catch (ValidationException e) {
            //ignore
        }

        verify(allSubscriptions, never()).add(any(Subscription.class));
        verify(messageCampaignService, never()).start(any(MessageCampaignRequest.class), any(Integer.class), any(Integer.class));
        verify(reportingServiceImpl, never()).reportSubscriptionCreation(any(SubscriptionReportRequest.class));
        verify(onMobileSubscriptionManagerPublisher, never()).sendActivationRequest(any(OMSubscriptionRequest.class));
    }

    @Test
    public void shouldPublishReportingEventOnCreatingSubscription() {
        String msisdn = "1234567890";
        Channel channel = Channel.IVR;
        SubscriptionPack subscriptionPack = SubscriptionPack.CHOTI_KILKARI;
        ArgumentCaptor<SubscriptionReportRequest> subscriptionReportRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionReportRequest.class);
        SubscriptionRequest subscription = new SubscriptionRequestBuilder().withDefaults().withMsisdn(msisdn).withPack(subscriptionPack).build();

        subscriptionService.createSubscriptionWithReporting(subscription, channel);

        verify(reportingServiceImpl).reportSubscriptionCreation(subscriptionReportRequestArgumentCaptor.capture());
        SubscriptionReportRequest actualSubscriptionCreationReportRequest = subscriptionReportRequestArgumentCaptor.getValue();
        assertEquals(msisdn, actualSubscriptionCreationReportRequest.getMsisdn().toString());
        assertEquals(subscriptionPack.name(), actualSubscriptionCreationReportRequest.getPack());
        assertEquals(channel.name(), actualSubscriptionCreationReportRequest.getChannel());
    }

    @Test
    public void shouldSendActivationRequestToOMSubscriptionManager_OnCreatingSubscription() {
        String msisdn = "1234567890";
        Channel channel = Channel.IVR;
        SubscriptionPack subscriptionPack = SubscriptionPack.CHOTI_KILKARI;
        ArgumentCaptor<OMSubscriptionRequest> subscriptionActivationRequestArgumentCaptor = ArgumentCaptor.forClass(OMSubscriptionRequest.class);
        SubscriptionRequest subscription = new SubscriptionRequestBuilder().withDefaults().withMsisdn(msisdn).withPack(subscriptionPack).build();

        subscriptionService.createSubscriptionWithReporting(subscription, channel);

        verify(onMobileSubscriptionManagerPublisher).sendActivationRequest(subscriptionActivationRequestArgumentCaptor.capture());
        OMSubscriptionRequest actualOMSubscriptionRequest = subscriptionActivationRequestArgumentCaptor.getValue();
        assertEquals(msisdn, actualOMSubscriptionRequest.getMsisdn());
        assertEquals(subscriptionPack, actualOMSubscriptionRequest.getPack());
        assertEquals(channel, actualOMSubscriptionRequest.getChannel());

    }

    @Test
    public void shouldGetSubscriptionsResponseForAGivenMsisdn() {
        String msisdn = "1234567890";
        ArrayList<Subscription> subscriptionsToBeReturned = new ArrayList<>();
        subscriptionsToBeReturned.add(new Subscription(msisdn, SubscriptionPack.CHOTI_KILKARI, DateTime.now(), SubscriptionStatus.NEW));
        subscriptionsToBeReturned.add(new Subscription(msisdn, SubscriptionPack.BARI_KILKARI, DateTime.now(), SubscriptionStatus.NEW));
        when(allSubscriptions.findByMsisdn(msisdn)).thenReturn(subscriptionsToBeReturned);

        List<Subscription> subscriptions = subscriptionService.findByMsisdn(msisdn);

        assertEquals(2, subscriptions.size());
        assertEquals(msisdn, subscriptions.get(0).getMsisdn());
        assertEquals(SubscriptionPack.CHOTI_KILKARI, subscriptions.get(0).getPack());
        assertEquals(msisdn, subscriptions.get(1).getMsisdn());
        assertEquals(SubscriptionPack.BARI_KILKARI, subscriptions.get(1).getPack());
    }

    @Test
    public void shouldFindInProgressSubscriptionByMsisdnAndPack() {
        String msisdn = "1234567890";
        SubscriptionPack pack = SubscriptionPack.BARI_KILKARI;

        Subscription subscription = new Subscription(msisdn, pack, DateTime.now(), SubscriptionStatus.ACTIVE);
        when(allSubscriptions.findSubscriptionInProgress(msisdn, pack)).thenReturn(subscription);
        assertEquals(subscription, subscriptionService.findSubscriptionInProgress(msisdn, pack));
    }

    @Test
    public void shouldThrowAnExceptionForInvalidMsisdnNumbers() {
        expectedException.expect(ValidationException.class);
        expectedException.expectMessage("Invalid msisdn 12345");

        subscriptionService.findByMsisdn("12345");
    }

    @Test
    public void shouldThrowAnExceptionForNonNumericMsisdn() {
        expectedException.expect(ValidationException.class);
        expectedException.expectMessage("Invalid msisdn 123456789a");

        subscriptionService.findByMsisdn("123456789a");
    }

    @Test
    public void shouldUpdateTheSubscriptionStatusToPendingActivation_WhenActivationIsRequested() {
        String subscriptionId = "abcd1234";
        SubscriptionStatus status = SubscriptionStatus.PENDING_ACTIVATION;
        Subscription mockedSubscription = mock(Subscription.class);
        OMSubscriptionRequest omSubscriptionRequest = new OMSubscriptionRequest("1234567890", SubscriptionPack.BARI_KILKARI, Channel.IVR, subscriptionId);
        when(mockedSubscription.getStatus()).thenReturn(status);
        when(mockedSubscription.getSubscriptionId()).thenReturn(subscriptionId);
        when(allSubscriptions.findBySubscriptionId(subscriptionId)).thenReturn(mockedSubscription);

        subscriptionService.activationRequested(omSubscriptionRequest);

        InOrder order = inOrder(allSubscriptions, mockedSubscription, reportingServiceImpl);
        order.verify(allSubscriptions).findBySubscriptionId(subscriptionId);
        order.verify(mockedSubscription).activationRequestSent();
        order.verify(allSubscriptions).update(mockedSubscription);
        ArgumentCaptor<SubscriptionStateChangeRequest> subscriptionStateChangeReportRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionStateChangeRequest.class);
        order.verify(reportingServiceImpl).reportSubscriptionStateChange(subscriptionStateChangeReportRequestArgumentCaptor.capture());
        SubscriptionStateChangeRequest subscriptionStateChangeReportRequest = subscriptionStateChangeReportRequestArgumentCaptor.getValue();
        verify(onMobileSubscriptionGateway).activateSubscription(omSubscriptionRequest);
        assertEquals(subscriptionId, subscriptionStateChangeReportRequest.getSubscriptionId());
        assertEquals(status.name(), subscriptionStateChangeReportRequest.getSubscriptionStatus());
        assertNull(subscriptionStateChangeReportRequest.getOperator());
    }

    @Test
    public void shouldUpdateTheSubscriptionStatusToDeactivationRequested_WhenDeactivationIsRequested() {
        String subscriptionId = "abcd1234";
        SubscriptionStatus status = SubscriptionStatus.ACTIVE;
        Subscription mockedSubscription = mock(Subscription.class);

        when(mockedSubscription.getStatus()).thenReturn(status);
        when(mockedSubscription.getSubscriptionId()).thenReturn(subscriptionId);
        when(mockedSubscription.isInProgress()).thenReturn(true);
        when(allSubscriptions.findBySubscriptionId(subscriptionId)).thenReturn(mockedSubscription);

        subscriptionService.requestDeactivation(new DeactivationRequest(subscriptionId, Channel.IVR, DateTime.now()));

        InOrder order = inOrder(allSubscriptions, mockedSubscription, reportingServiceImpl);
        order.verify(allSubscriptions).findBySubscriptionId(subscriptionId);
        order.verify(mockedSubscription).deactivationRequestReceived();
        order.verify(allSubscriptions).update(mockedSubscription);
        ArgumentCaptor<SubscriptionStateChangeRequest> subscriptionStateChangeReportRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionStateChangeRequest.class);
        order.verify(reportingServiceImpl).reportSubscriptionStateChange(subscriptionStateChangeReportRequestArgumentCaptor.capture());
        SubscriptionStateChangeRequest subscriptionStateChangeReportRequest = subscriptionStateChangeReportRequestArgumentCaptor.getValue();

        assertEquals(subscriptionId, subscriptionStateChangeReportRequest.getSubscriptionId());
        assertEquals(status.name(), subscriptionStateChangeReportRequest.getSubscriptionStatus());
        assertNull(subscriptionStateChangeReportRequest.getOperator());
    }

    @Test
    public void shouldUpdateTheSubscriptionStatusToPendingDeactivation_WhenDeactivationRequestedIsComplete() {
        String subscriptionId = "abcd1234";
        SubscriptionStatus status = SubscriptionStatus.DEACTIVATION_REQUEST_RECEIVED;
        Subscription mockedSubscription = mock(Subscription.class);
        OMSubscriptionRequest omSubscriptionRequest = new OMSubscriptionRequest("1234567890", SubscriptionPack.BARI_KILKARI, Channel.IVR, subscriptionId);
        when(mockedSubscription.getStatus()).thenReturn(status);
        when(mockedSubscription.getSubscriptionId()).thenReturn(subscriptionId);
        when(allSubscriptions.findBySubscriptionId(subscriptionId)).thenReturn(mockedSubscription);

        subscriptionService.deactivationRequested(omSubscriptionRequest);

        InOrder order = inOrder(allSubscriptions, mockedSubscription, reportingServiceImpl);
        order.verify(allSubscriptions).findBySubscriptionId(subscriptionId);
        order.verify(mockedSubscription).deactivationRequestSent();
        order.verify(allSubscriptions).update(mockedSubscription);
        ArgumentCaptor<SubscriptionStateChangeRequest> subscriptionStateChangeReportRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionStateChangeRequest.class);
        order.verify(reportingServiceImpl).reportSubscriptionStateChange(subscriptionStateChangeReportRequestArgumentCaptor.capture());
        SubscriptionStateChangeRequest subscriptionStateChangeReportRequest = subscriptionStateChangeReportRequestArgumentCaptor.getValue();
        verify(onMobileSubscriptionGateway).deactivateSubscription(omSubscriptionRequest);
        assertEquals(subscriptionId, subscriptionStateChangeReportRequest.getSubscriptionId());
        assertEquals(status.name(), subscriptionStateChangeReportRequest.getSubscriptionStatus());
        assertNull(subscriptionStateChangeReportRequest.getOperator());
    }

    @Test
    public void shouldUpdateTheSubscriptionStatusToActivationFailed_GivenSubscriptionId() {
        SubscriptionStatus status = SubscriptionStatus.ACTIVATION_FAILED;
        Subscription mockedSubscription = mock(Subscription.class);
        String subscriptionId = "abcd1234";
        String operator = Operator.AIRTEL.name();
        String reason = "Activation Failed For some error";

        when(mockedSubscription.getStatus()).thenReturn(status);
        when(mockedSubscription.getSubscriptionId()).thenReturn(subscriptionId);
        when(allSubscriptions.findBySubscriptionId(subscriptionId)).thenReturn(mockedSubscription);

        subscriptionService.activationFailed(subscriptionId, DateTime.now(), reason, operator);

        InOrder order = inOrder(allSubscriptions, mockedSubscription, reportingServiceImpl);
        order.verify(allSubscriptions).findBySubscriptionId(subscriptionId);
        order.verify(mockedSubscription).activationFailed(operator);
        order.verify(allSubscriptions).update(mockedSubscription);
        ArgumentCaptor<SubscriptionStateChangeRequest> subscriptionStateChangeReportRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionStateChangeRequest.class);
        order.verify(reportingServiceImpl).reportSubscriptionStateChange(subscriptionStateChangeReportRequestArgumentCaptor.capture());
        SubscriptionStateChangeRequest subscriptionStateChangeReportRequest = subscriptionStateChangeReportRequestArgumentCaptor.getValue();

        assertEquals(subscriptionId, subscriptionStateChangeReportRequest.getSubscriptionId());
        assertEquals(status.name(), subscriptionStateChangeReportRequest.getSubscriptionStatus());
        assertEquals(reason, subscriptionStateChangeReportRequest.getReason());
        assertEquals(operator, subscriptionStateChangeReportRequest.getOperator());
    }

    @Test
    public void shouldActivateSubscriptionAndScheduleCampaign() {
        Subscription subscription = new SubscriptionBuilder().withDefaults().build();
        String operator = "airtel";
        DateTime activatedOn = DateTime.now();
        String subscriptionId = subscription.getSubscriptionId();
        int deltaDays = 2;
        int deltaMinutes = 30;
        when(allSubscriptions.findBySubscriptionId(subscriptionId)).thenReturn(subscription);
        when(kilkariPropertiesData.getCampaignScheduleDeltaDays()).thenReturn(deltaDays);
        when(kilkariPropertiesData.getCampaignScheduleDeltaMinutes()).thenReturn(deltaMinutes);

        subscriptionService.activate(subscriptionId, activatedOn, operator);

        ArgumentCaptor<Subscription> captor = ArgumentCaptor.forClass(Subscription.class);
        verify(allSubscriptions).update(captor.capture());
        Subscription actualSubscription = captor.getValue();
        assertEquals(SubscriptionStatus.ACTIVE, actualSubscription.getStatus());
        assertEquals(subscriptionId, actualSubscription.getSubscriptionId());

        ArgumentCaptor<SubscriptionStateChangeRequest> subscriptionStateChangeReportRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionStateChangeRequest.class);
        verify(reportingServiceImpl).reportSubscriptionStateChange(subscriptionStateChangeReportRequestArgumentCaptor.capture());
        SubscriptionStateChangeRequest stateChangeReportRequest = subscriptionStateChangeReportRequestArgumentCaptor.getValue();
        assertEquals(operator, stateChangeReportRequest.getOperator());
        assertEquals(subscriptionId, stateChangeReportRequest.getSubscriptionId());
        assertEquals(SubscriptionStatus.ACTIVE.name(), stateChangeReportRequest.getSubscriptionStatus());

        ArgumentCaptor<MessageCampaignRequest> messageCampaignRequestArgumentCaptor = ArgumentCaptor.forClass(MessageCampaignRequest.class);
        ArgumentCaptor<Integer> actualDeltaDays = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> actualDeltaMinutes = ArgumentCaptor.forClass(Integer.class);
        verify(messageCampaignService).start(messageCampaignRequestArgumentCaptor.capture(), actualDeltaDays.capture(), actualDeltaMinutes.capture());
        MessageCampaignRequest actualMessageCampaignRequest = messageCampaignRequestArgumentCaptor.getValue();

        assertEquals(subscription.getSubscriptionId(), actualMessageCampaignRequest.getExternalId());
        assertEquals(subscription.getPack().name(), actualMessageCampaignRequest.getSubscriptionPack());
        assertEquals(deltaDays, actualDeltaDays.getValue().intValue());
        assertEquals(deltaMinutes, actualDeltaMinutes.getValue().intValue());
    }

    @Test
    public void shouldActivateRenewedSubscriptionWhichHadBeenSuspended() {
        final String subscriptionId = "sub123";
        DateTime renewalDate = DateTime.now();
        int graceCount = 2;

        Subscription subscription = new Subscription() {
            public String getSubscriptionId() {
                return subscriptionId;
            }
        };
        subscription.setStatus(SubscriptionStatus.SUSPENDED);
        when(allSubscriptions.findBySubscriptionId(subscriptionId)).thenReturn(subscription);

        subscriptionService.renewSubscription(subscriptionId, renewalDate, graceCount);

        verify(allSubscriptions).update(subscription);
        ArgumentCaptor<SubscriptionStateChangeRequest> subscriptionStateChangeReportRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionStateChangeRequest.class);
        verify(reportingServiceImpl).reportSubscriptionStateChange(subscriptionStateChangeReportRequestArgumentCaptor.capture());
        SubscriptionStateChangeRequest subscriptionStateChangeReportRequest = subscriptionStateChangeReportRequestArgumentCaptor.getValue();

        assertEquals(SubscriptionStatus.ACTIVE, subscription.getStatus());
        assertEquals(subscriptionId, subscriptionStateChangeReportRequest.getSubscriptionId());
        assertEquals(SubscriptionStatus.ACTIVE.name(), subscriptionStateChangeReportRequest.getSubscriptionStatus());
        assertEquals(renewalDate, subscriptionStateChangeReportRequest.getCreatedAt());
        assertEquals((Integer) graceCount, subscriptionStateChangeReportRequest.getGraceCount());
    }

    @Test
    public void shouldSuspendRenewedSubscriptionWhichWasActive() {
        final String subscriptionId = "subId";
        final DateTime renewalDate = DateTime.now();
        final String reason = "Balance Low";
        final int graceCount = 0;
        Subscription subscription = new Subscription() {
            public String getSubscriptionId() {
                return subscriptionId;
            }
        };
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        when(allSubscriptions.findBySubscriptionId(subscriptionId)).thenReturn(subscription);

        subscriptionService.suspendSubscription(subscriptionId, renewalDate, reason, graceCount);

        verify(allSubscriptions).update(subscription);
        ArgumentCaptor<SubscriptionStateChangeRequest> subscriptionStateChangeReportRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionStateChangeRequest.class);
        verify(reportingServiceImpl).reportSubscriptionStateChange(subscriptionStateChangeReportRequestArgumentCaptor.capture());
        SubscriptionStateChangeRequest subscriptionStateChangeReportRequest = subscriptionStateChangeReportRequestArgumentCaptor.getValue();

        assertEquals(SubscriptionStatus.SUSPENDED, subscription.getStatus());
        assertEquals(subscriptionId, subscriptionStateChangeReportRequest.getSubscriptionId());
        assertEquals(SubscriptionStatus.SUSPENDED.name(), subscriptionStateChangeReportRequest.getSubscriptionStatus());
        assertEquals(renewalDate, subscriptionStateChangeReportRequest.getCreatedAt());
        assertEquals((Integer) graceCount, subscriptionStateChangeReportRequest.getGraceCount());
    }

    @Test
    public void shouldDeactivateSubscriptionWithAppropriateReason() {
        final String subscriptionId = "sub123";
        DateTime date = DateTime.now();
        String reason = "balance is low";
        Integer graceCount = 7;
        Subscription subscription = new Subscription("1234567890", SubscriptionPack.BARI_KILKARI, DateTime.now(), SubscriptionStatus.NEW) {
            public String getSubscriptionId() {
                return subscriptionId;
            }
        };
        subscription.setStatus(SubscriptionStatus.SUSPENDED);

        when(allSubscriptions.findBySubscriptionId(subscriptionId)).thenReturn(subscription);

        subscriptionService.deactivateSubscription(subscriptionId, date, reason, graceCount);

        verify(allSubscriptions).update(subscription);
        ArgumentCaptor<SubscriptionStateChangeRequest> subscriptionStateChangeReportRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionStateChangeRequest.class);
        verify(reportingServiceImpl).reportSubscriptionStateChange(subscriptionStateChangeReportRequestArgumentCaptor.capture());
        SubscriptionStateChangeRequest subscriptionStateChangeReportRequest = subscriptionStateChangeReportRequestArgumentCaptor.getValue();

        assertEquals(SubscriptionStatus.DEACTIVATED, subscription.getStatus());
        assertEquals(subscriptionId, subscriptionStateChangeReportRequest.getSubscriptionId());
        assertEquals(SubscriptionStatus.DEACTIVATED.name(), subscriptionStateChangeReportRequest.getSubscriptionStatus());
        assertEquals(date, subscriptionStateChangeReportRequest.getCreatedAt());
        assertEquals(graceCount, subscriptionStateChangeReportRequest.getGraceCount());
    }

    @Test
    public void shouldReturnASubscriptionGivenAnId() {
        String subscriptionID = "subscriptionID";

        subscriptionService.findBySubscriptionId(subscriptionID);

        verify(allSubscriptions).findBySubscriptionId(subscriptionID);
    }

    @Test
    public void shouldProcessDeactivation() {
        String subscriptionId = "subscriptionId";
        String msisdn = "1234567890";
        Subscription subscription = new Subscription(msisdn, SubscriptionPack.NANHI_KILKARI, DateTime.now(), SubscriptionStatus.NEW);
        when(allSubscriptions.findBySubscriptionId(subscriptionId)).thenReturn(subscription);
        Channel channel = Channel.IVR;

        subscriptionService.requestDeactivation(new DeactivationRequest(subscriptionId, channel, DateTime.now()));

        ArgumentCaptor<Subscription> captor = ArgumentCaptor.forClass(Subscription.class);
        ArgumentCaptor<OMSubscriptionRequest> captor1 = ArgumentCaptor.forClass(OMSubscriptionRequest.class);
        verify(allSubscriptions).update(captor.capture());
        Subscription actualSubscription = captor.getValue();
        Assert.assertEquals(msisdn, actualSubscription.getMsisdn());
        Assert.assertEquals(SubscriptionStatus.DEACTIVATION_REQUEST_RECEIVED, actualSubscription.getStatus());

        verify(onMobileSubscriptionManagerPublisher).processDeactivation(captor1.capture());
        OMSubscriptionRequest actualOMSubscriptionRequest = captor1.getValue();
        assertEquals(msisdn, actualOMSubscriptionRequest.getMsisdn());
        assertEquals(channel, actualOMSubscriptionRequest.getChannel());
        assertEquals(subscription.getSubscriptionId(), actualOMSubscriptionRequest.getSubscriptionId());
    }

    @Test
    public void shouldProcessSubscriptionCompletion() {
        final String msisdn = "9988776655";
        final SubscriptionPack pack = SubscriptionPack.CHOTI_KILKARI;
        Subscription subscription = new SubscriptionBuilder().withMsisdn(msisdn).withStatus(SubscriptionStatus.ACTIVE).withStartDate(DateTime.now().minusWeeks(4)).build();
        final String subscriptionId = subscription.getSubscriptionId();
        final OMSubscriptionRequest omSubscriptionRequest = new OMSubscriptionRequest(msisdn, pack, null, subscriptionId);
        when(allSubscriptions.findBySubscriptionId(subscriptionId)).thenReturn(subscription);

        subscriptionService.subscriptionComplete(omSubscriptionRequest);

        ArgumentCaptor<Subscription> subscriptionArgumentCaptor = ArgumentCaptor.forClass(Subscription.class);
        verify(allSubscriptions).update(subscriptionArgumentCaptor.capture());
        Subscription actualSubscription = subscriptionArgumentCaptor.getValue();
        Assert.assertEquals(msisdn, actualSubscription.getMsisdn());
        Assert.assertEquals(SubscriptionStatus.PENDING_COMPLETION, actualSubscription.getStatus());

        ArgumentCaptor<SubscriptionStateChangeRequest> subscriptionStateChangeReportRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionStateChangeRequest.class);
        verify(reportingServiceImpl).reportSubscriptionStateChange(subscriptionStateChangeReportRequestArgumentCaptor.capture());
        SubscriptionStateChangeRequest subscriptionStateChangeReportRequest = subscriptionStateChangeReportRequestArgumentCaptor.getValue();
        assertEquals(subscriptionId, subscriptionStateChangeReportRequest.getSubscriptionId());
        assertEquals(SubscriptionStatus.PENDING_COMPLETION.name(), subscriptionStateChangeReportRequest.getSubscriptionStatus());
        assertEquals("Subscription completed", subscriptionStateChangeReportRequest.getReason());

        ArgumentCaptor<OMSubscriptionRequest> processSubscriptionRequestArgumentCaptor = ArgumentCaptor.forClass(OMSubscriptionRequest.class);
        verify(onMobileSubscriptionGateway).deactivateSubscription(processSubscriptionRequestArgumentCaptor.capture());
        OMSubscriptionRequest OMSubscriptionRequest = processSubscriptionRequestArgumentCaptor.getValue();

        assertEquals(msisdn, OMSubscriptionRequest.getMsisdn());
        assertEquals(null, OMSubscriptionRequest.getChannel());
        assertEquals(pack, OMSubscriptionRequest.getPack());
        assertEquals(subscriptionId, OMSubscriptionRequest.getSubscriptionId());

        verify(campaignMessageAlertService).deleteFor(subscriptionId);
    }

    @Test
    public void shouldScheduleInboxDeletionUponSubscriptionCompletion() {
        String msisdn = "1234567890";
        SubscriptionPack pack = SubscriptionPack.NANHI_KILKARI;
        Subscription subscription = new Subscription(msisdn, pack, DateTime.now(), SubscriptionStatus.NEW);
        String subscriptionId = subscription.getSubscriptionId();
        when(allSubscriptions.findBySubscriptionId(subscriptionId)).thenReturn(subscription);

        subscriptionService.subscriptionComplete(new OMSubscriptionRequest(msisdn, pack, Channel.IVR, subscriptionId));

        verify(inboxService).scheduleInboxDeletion(subscription.getSubscriptionId(), subscription.getCurrentWeeksMessageExpiryDate());
    }

    @Test
    public void shouldNotSendDeactivationRequestAgainIfTheExistingSubscriptionIsAlreadyInDeactivatedState() {
        final String msisdn = "9988776655";
        final Subscription subscription = new SubscriptionBuilder().withDefaults().withStatus(SubscriptionStatus.DEACTIVATED).build();
        final String subscriptionId = subscription.getSubscriptionId();
        final SubscriptionPack pack = SubscriptionPack.CHOTI_KILKARI;
        OMSubscriptionRequest value = new OMSubscriptionRequest(msisdn, pack, null, subscriptionId);
        when(allSubscriptions.findBySubscriptionId(subscriptionId)).thenReturn(subscription);

        subscriptionService.subscriptionComplete(value);

        verify(onMobileSubscriptionGateway, never()).deactivateSubscription(any(OMSubscriptionRequest.class));
        verify(onMobileSubscriptionGateway, never()).deactivateSubscription(any(OMSubscriptionRequest.class));
        verify(inboxService, never()).scheduleInboxDeletion(any(String.class), any(DateTime.class));
        verify(campaignMessageAlertService, never()).deleteFor(any(String.class));
    }

    @Test
    public void shouldScheduleInboxDeletionUponSubscriptionDeactivation() {
        Subscription subscription = new Subscription("1234567890", SubscriptionPack.NANHI_KILKARI, DateTime.now(), SubscriptionStatus.NEW);
        String subscriptionId = subscription.getSubscriptionId();
        when(allSubscriptions.findBySubscriptionId(subscriptionId)).thenReturn(subscription);

        subscriptionService.deactivateSubscription(subscriptionId, DateTime.now(), null, null);

        verify(inboxService).scheduleInboxDeletion(subscriptionId, subscription.getCurrentWeeksMessageExpiryDate());
    }

    @Test
    public void shouldUnScheduleCurrentCampaignAndScheduleNewCampaignForMCOrID() {
        String subscriptionId = "subscriptionId";
        String msisdn = "1234567890";
        SubscriptionPack subscriptionPack = SubscriptionPack.BARI_KILKARI;
        int deltaMinutes = 30;
        DateTime friday = new DateTime(2011, 11, 25, 12, 30, 30);
        DateTime existingCampaignStartDate = friday;
        CampaignChangeReason campaignChangeReason = CampaignChangeReason.MISCARRIAGE;
        DateTime saturday = existingCampaignStartDate.plusMonths(3);
        DateTime rescheduleRequestedDate = saturday;
        DateTime nextAlertDateTime = rescheduleRequestedDate.plusHours(1);

        Subscription subscription = new SubscriptionBuilder().withDefaults().withMsisdn(msisdn).withPack(subscriptionPack).withStartDate(existingCampaignStartDate).build();
        when(allSubscriptions.findBySubscriptionId(subscriptionId)).thenReturn(subscription);
        when(messageCampaignService.getMessageTimings(subscriptionId, rescheduleRequestedDate, rescheduleRequestedDate.plusMonths(1))).thenReturn(Arrays.asList(nextAlertDateTime, new DateTime()));
        when(kilkariPropertiesData.getCampaignScheduleDeltaMinutes()).thenReturn(deltaMinutes);

        subscriptionService.rescheduleCampaign(new CampaignRescheduleRequest(subscriptionId, campaignChangeReason, rescheduleRequestedDate));

        InOrder order = inOrder(messageCampaignService, campaignMessageService, campaignMessageAlertService);
        order.verify(messageCampaignService).getMessageTimings(subscriptionId, rescheduleRequestedDate, rescheduleRequestedDate.plusMonths(1));
        ArgumentCaptor<MessageCampaignRequest> campaignUnEnrollmentRequestArgumentCaptor = ArgumentCaptor.forClass(MessageCampaignRequest.class);
        order.verify(messageCampaignService).stop(campaignUnEnrollmentRequestArgumentCaptor.capture());
        MessageCampaignRequest campaignRequest = campaignUnEnrollmentRequestArgumentCaptor.getValue();
        assertEquals(subscription.getSubscriptionId(), campaignRequest.getExternalId());
        assertEquals(subscription.getPack().name(), campaignRequest.getSubscriptionPack());
        assertEquals(subscription.getStartDate(), campaignRequest.getSubscriptionStartDate());

        order.verify(campaignMessageService).deleteCampaignMessagesFor(subscriptionId);
        order.verify(campaignMessageAlertService).clearMessageId(subscriptionId);

        ArgumentCaptor<MessageCampaignRequest> campaignEnrollmentRequestArgumentCaptor = ArgumentCaptor.forClass(MessageCampaignRequest.class);
        ArgumentCaptor<Integer> deltaDaysCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> deltaMinutesCaptor = ArgumentCaptor.forClass(Integer.class);
        order.verify(messageCampaignService).start(campaignEnrollmentRequestArgumentCaptor.capture(), deltaDaysCaptor.capture(), deltaMinutesCaptor.capture());
        MessageCampaignRequest campaignEnrollmentRequest = campaignEnrollmentRequestArgumentCaptor.getValue();
        assertEquals(subscriptionId, campaignEnrollmentRequest.getExternalId());
        assertEquals(campaignChangeReason.name(), campaignEnrollmentRequest.getSubscriptionPack());
        assertEquals(nextAlertDateTime, campaignEnrollmentRequest.getSubscriptionStartDate());
        assertEquals(0, deltaDaysCaptor.getValue().intValue());
        assertEquals(deltaMinutes, deltaMinutesCaptor.getValue().intValue());
    }

    @Test
    public void shouldThrowExceptionWhenUnScheduleCurrentCampaignIsInvokedForANonActivePack() {
        String subscriptionId = "subscriptionId";
        String message = "some error";
        CampaignChangeReason campaignChangeReason = CampaignChangeReason.MISCARRIAGE;
        Subscription subscription = new SubscriptionBuilder().withDefaults().withMsisdn("1234567890").withPack(SubscriptionPack.BARI_KILKARI).withCreationDate(DateTime.now()).withStatus(SubscriptionStatus.PENDING_COMPLETION).build();
        when(allSubscriptions.findBySubscriptionId(subscriptionId)).thenReturn(subscription);
        doThrow(new ValidationException(message)).when(subscriptionValidator).validateActiveSubscriptionExists(subscriptionId);
        expectedException.expect(ValidationException.class);
        expectedException.expectMessage(message);

        subscriptionService.rescheduleCampaign(new CampaignRescheduleRequest(subscriptionId, campaignChangeReason, DateTime.now()));

        verify(messageCampaignService, never()).stop(any(MessageCampaignRequest.class));
        verify(campaignMessageService, never()).deleteCampaignMessagesFor(any(String.class));
        verify(messageCampaignService, never()).start(any(MessageCampaignRequest.class), 0, 0);
    }

    @Test
    public void shouldThrowExceptionIfValidationFailsForUpdatingSubscriberDetails() {
        SubscriberRequest request = mock(SubscriberRequest.class);
        String message = "some error";
        doThrow(new ValidationException(message)).when(subscriptionValidator).validateSubscriberDetails(request);
        expectedException.expect(ValidationException.class);
        expectedException.expectMessage(message);

        subscriptionService.updateSubscriberDetails(request);

        verify(reportingServiceImpl, never()).reportSubscriberDetailsChange(request.getSubscriptionId(), any(SubscriberReportRequest.class));
    }

    @Test
    public void shouldPublishASubscriberUpdateEvent() {
        String subscriptionId = "subscriptionId";
        Location location = new Location("district", "block", "panchayat");
        DateTime expectedDateOfDelivery = DateTime.now().plusYears(5);
        DateTime dateOfBirth = DateTime.now().minusYears(5);

        subscriptionService.updateSubscriberDetails(new SubscriberRequest(subscriptionId, Channel.CALL_CENTER.name(), DateTime.now(), "name", 23,
                expectedDateOfDelivery, dateOfBirth, location));

        ArgumentCaptor<SubscriberReportRequest> requestCaptor = ArgumentCaptor.forClass(SubscriberReportRequest.class);
        ArgumentCaptor<String> subscriptionIdCaptor = ArgumentCaptor.forClass(String.class);
        verify(reportingServiceImpl).reportSubscriberDetailsChange(subscriptionIdCaptor.capture(), requestCaptor.capture());
        SubscriberReportRequest reportRequest = requestCaptor.getValue();
        String actualSubscriptionId = subscriptionIdCaptor.getValue();

        assertEquals(subscriptionId, actualSubscriptionId);
        assertEquals(expectedDateOfDelivery, reportRequest.getExpectedDateOfDelivery());
        assertEquals(dateOfBirth, reportRequest.getDateOfBirth());
        assertEquals(23, (int) reportRequest.getBeneficiaryAge());
        assertEquals("name", reportRequest.getBeneficiaryName());
        assertEquals("district", reportRequest.getLocation().getDistrict());
        assertEquals("block", reportRequest.getLocation().getBlock());
        assertEquals("panchayat", reportRequest.getLocation().getPanchayat());
    }

    @Test
    public void shouldNotProcessDeactivationRequestWhenSubscriptionIsNotInProgress() {
        String subscriptionId = "subsId";
        Subscription mockedSubscription = mock(Subscription.class);
        when(allSubscriptions.findBySubscriptionId(subscriptionId)).thenReturn(mockedSubscription);

        subscriptionService.requestDeactivation(new DeactivationRequest(subscriptionId, Channel.IVR, DateTime.now()));

        verify(mockedSubscription, never()).deactivationRequestReceived();
        verify(onMobileSubscriptionManagerPublisher, never()).processDeactivation(Matchers.<OMSubscriptionRequest>any());
        verify(reportingServiceImpl, never()).reportSubscriptionStateChange(Matchers.<SubscriptionStateChangeRequest>any());
        verify(allSubscriptions, never()).update(mockedSubscription);
    }

    @Test
    public void shouldBackDateStartDateForALateSubscriptionWhenEDDIsGiven() {
        DateTime edd = DateTime.now().plusWeeks(1);
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withDefaults().withPack(SubscriptionPack.BARI_KILKARI).withExpectedDateOfDelivery(edd).build();

        subscriptionService.createSubscriptionWithReporting(subscriptionRequest, Channel.CALL_CENTER);

        ArgumentCaptor<Subscription> subscriptionArgumentCaptor = ArgumentCaptor.forClass(Subscription.class);
        verify(allSubscriptions).add(subscriptionArgumentCaptor.capture());
        Subscription subscriptionArgumentCaptorValue = subscriptionArgumentCaptor.getValue();
        assertTrue(Weeks.weeksBetween(subscriptionArgumentCaptorValue.getStartDate(), edd).getWeeks() >= 12);
    }

    @Test
    public void shouldBackDateStartDateForALateSubscriptionWhenDOBIsGivenForFifteenMonthsPack() {
        DateTime dob = DateTime.now().minusMonths(1);
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withDefaults().withPack(SubscriptionPack.BARI_KILKARI).withDateOfBirth(dob).build();

        subscriptionService.createSubscriptionWithReporting(subscriptionRequest, Channel.CALL_CENTER);

        ArgumentCaptor<Subscription> subscriptionArgumentCaptor = ArgumentCaptor.forClass(Subscription.class);
        verify(allSubscriptions).add(subscriptionArgumentCaptor.capture());
        Subscription subscriptionArgumentCaptorValue = subscriptionArgumentCaptor.getValue();
        assertTrue(Weeks.weeksBetween(subscriptionArgumentCaptorValue.getStartDate(), dob).getWeeks() >= 12);

    }

    @Test
    public void shouldBackDateStartDateForALateSubscriptionWhenDOBIsGivenForTwelveMonthsPack() {
        DateTime dob = DateTime.now().minusMonths(1);
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withDefaults().withPack(SubscriptionPack.CHOTI_KILKARI).withDateOfBirth(dob).build();

        subscriptionService.createSubscriptionWithReporting(subscriptionRequest, Channel.CALL_CENTER);

        ArgumentCaptor<Subscription> subscriptionArgumentCaptor = ArgumentCaptor.forClass(Subscription.class);
        verify(allSubscriptions).add(subscriptionArgumentCaptor.capture());
        Subscription subscriptionArgumentCaptorValue = subscriptionArgumentCaptor.getValue();
        Assert.assertEquals(dob, subscriptionArgumentCaptorValue.getStartDate());

    }

    @Test
    public void shouldBackDateStartDateForALateSubscriptionWhenDOBIsGivenForSevenMonthsPack() {
        DateTime dob = DateTime.now().minusMonths(1);
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withDefaults().withPack(SubscriptionPack.NANHI_KILKARI).withDateOfBirth(dob).build();

        subscriptionService.createSubscriptionWithReporting(subscriptionRequest, Channel.CALL_CENTER);

        ArgumentCaptor<Subscription> subscriptionArgumentCaptor = ArgumentCaptor.forClass(Subscription.class);
        verify(allSubscriptions).add(subscriptionArgumentCaptor.capture());
        Subscription subscriptionArgumentCaptorValue = subscriptionArgumentCaptor.getValue();
        assertTrue(Weeks.weeksBetween(dob, subscriptionArgumentCaptorValue.getStartDate()).getWeeks() >= 20);

    }

    @Test
    public void shouldBackDateStartDateForALateSubscriptionWhenWeekNumberIsGivenForSevenMonthsPack() {
        Integer weekNumber = 28;
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withDefaults().withPack(SubscriptionPack.NANHI_KILKARI).withWeek(weekNumber).build();

        subscriptionService.createSubscriptionWithReporting(subscriptionRequest, Channel.CALL_CENTER);

        ArgumentCaptor<Subscription> subscriptionArgumentCaptor = ArgumentCaptor.forClass(Subscription.class);
        verify(allSubscriptions).add(subscriptionArgumentCaptor.capture());
        Subscription subscriptionArgumentCaptorValue = subscriptionArgumentCaptor.getValue();
        assertEquals(subscriptionRequest.getCreationDate().minusWeeks(27), subscriptionArgumentCaptorValue.getStartDate());

    }

    @Test
    public void shouldSetDateForAnEarlySubscriptionWhenEDDIsGivenForFifteenMonths() {
        DateTime now = DateTime.now();
        DateTime edd = now.plusMonths(4);
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withDefaults().withPack(SubscriptionPack.BARI_KILKARI).withExpectedDateOfDelivery(edd).build();

        subscriptionService.createSubscriptionWithReporting(subscriptionRequest, Channel.CALL_CENTER);

        ArgumentCaptor<Subscription> subscriptionArgumentCaptor = ArgumentCaptor.forClass(Subscription.class);
        verify(allSubscriptions).add(subscriptionArgumentCaptor.capture());
        Subscription subscription = subscriptionArgumentCaptor.getValue();

        assertTrue(Weeks.weeksBetween(subscription.getStartDate(), edd).getWeeks() >= 12);
        assertEquals(SubscriptionStatus.NEW_EARLY, subscription.getStatus());
    }

    @Test
    public void shouldSetDateForAnEarlySubscriptionWhenEDDIsGivenForTwelveMonths() {
        DateTime now = DateTime.now();
        DateTime edd = now.plusMonths(4);
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withDefaults().withPack(SubscriptionPack.CHOTI_KILKARI).withExpectedDateOfDelivery(edd).build();

        subscriptionService.createSubscriptionWithReporting(subscriptionRequest, Channel.CALL_CENTER);

        ArgumentCaptor<Subscription> subscriptionArgumentCaptor = ArgumentCaptor.forClass(Subscription.class);
        verify(allSubscriptions).add(subscriptionArgumentCaptor.capture());
        Subscription subscription = subscriptionArgumentCaptor.getValue();
        assertEquals(edd, subscription.getStartDate());
        assertEquals(SubscriptionStatus.NEW_EARLY, subscription.getStatus());
    }

    @Test
    public void shouldSetDateForAnEarlySubscriptionWhenEDDIsGivenForSevenMonths() {
        DateTime now = DateTime.now();
        DateTime edd = now.plusMonths(4);
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withDefaults().withPack(SubscriptionPack.NANHI_KILKARI).withExpectedDateOfDelivery(edd).build();

        subscriptionService.createSubscriptionWithReporting(subscriptionRequest, Channel.CALL_CENTER);

        ArgumentCaptor<Subscription> subscriptionArgumentCaptor = ArgumentCaptor.forClass(Subscription.class);
        verify(allSubscriptions).add(subscriptionArgumentCaptor.capture());
        Subscription subscriptionArgumentCaptorValue = subscriptionArgumentCaptor.getValue();
        assertTrue(Weeks.weeksBetween(edd, subscriptionArgumentCaptorValue.getStartDate()).getWeeks() >= 20);
    }

    @Test
    public void shouldSetDateForAnEarlySubscriptionWhenDOBIsGivenForTwelveMonths() {
        DateTime dob = DateTime.now().plusMonths(3);
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withDefaults().withPack(SubscriptionPack.CHOTI_KILKARI).withDateOfBirth(dob).build();

        subscriptionService.createSubscriptionWithReporting(subscriptionRequest, Channel.CALL_CENTER);

        ArgumentCaptor<Subscription> subscriptionArgumentCaptor = ArgumentCaptor.forClass(Subscription.class);
        verify(allSubscriptions).add(subscriptionArgumentCaptor.capture());
        Subscription subscriptionArgumentCaptorValue = subscriptionArgumentCaptor.getValue();
        assertEquals(dob, subscriptionArgumentCaptorValue.getStartDate());

    }

    @Test
    public void shouldSetDateForAnEarlySubscriptionWhenDOBIsGivenForSevenMonths() {
        DateTime dob = DateTime.now().plusMonths(3);
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withDefaults().withPack(SubscriptionPack.NANHI_KILKARI).withDateOfBirth(dob).build();

        subscriptionService.createSubscriptionWithReporting(subscriptionRequest, Channel.CALL_CENTER);

        ArgumentCaptor<Subscription> subscriptionArgumentCaptor = ArgumentCaptor.forClass(Subscription.class);
        verify(allSubscriptions).add(subscriptionArgumentCaptor.capture());
        Subscription subscriptionArgumentCaptorValue = subscriptionArgumentCaptor.getValue();
        assertTrue(Weeks.weeksBetween(dob, subscriptionArgumentCaptorValue.getStartDate()).getWeeks() >= 20);

    }

    @Test
    public void shouldUnScheduleMessageCampaignAndDeleteCampaignMessageAlertOnSuccessfulDeactivationRequest() {
        DateTime createdAt = DateTime.now();
        Subscription subscription = new Subscription("1234567890", SubscriptionPack.CHOTI_KILKARI, createdAt, SubscriptionStatus.NEW);
        String subscriptionId = subscription.getSubscriptionId();
        when(allSubscriptions.findBySubscriptionId(subscriptionId)).thenReturn(subscription);

        subscriptionService.deactivateSubscription(subscriptionId, createdAt.plusWeeks(1), "Balance Low", null);

        ArgumentCaptor<MessageCampaignRequest> campaignRequestArgumentCaptor = ArgumentCaptor.forClass(MessageCampaignRequest.class);
        verify(messageCampaignService).stop(campaignRequestArgumentCaptor.capture());
        MessageCampaignRequest messageCampaignRequest = campaignRequestArgumentCaptor.getValue();
        assertEquals(subscriptionId, messageCampaignRequest.getExternalId());
        assertEquals(subscription.getPack().name(), messageCampaignRequest.getSubscriptionPack());
        assertEquals(createdAt, messageCampaignRequest.getSubscriptionStartDate());
        verify(campaignMessageAlertService).deleteFor(subscriptionId);
    }

    @Test
    public void shouldGivePriorityToWeekNumberOverDOBForALateSubscription() {
        Integer weekNumber = 40;
        DateTime dob = DateTime.now().minusMonths(6);
        SubscriptionPack subscriptionPack = mock(SubscriptionPack.class);
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withDefaults().withPack(subscriptionPack).withDateOfBirth(dob).withWeek(weekNumber).build();
        when(subscriptionPack.getStartDateForWeek(subscriptionRequest.getCreationDate(), weekNumber)).thenReturn(dob);

        subscriptionService.createSubscriptionWithReporting(subscriptionRequest, Channel.CALL_CENTER);

        verify(subscriptionPack).getStartDateForWeek(Matchers.<DateTime>any(), Matchers.<Integer>any());
        verify(subscriptionPack, never()).getStartDate(Matchers.<DateTime>any());
    }

    @Test
    public void shouldScheduleEarlySubscription() {
        DateTime dob = DateTime.now().plusMonths(3);
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withDefaults().withPack(SubscriptionPack.CHOTI_KILKARI).withDateOfBirth(dob).build();

        subscriptionService.createSubscriptionWithReporting(subscriptionRequest, Channel.CALL_CENTER);

        ArgumentCaptor<RunOnceSchedulableJob> runOnceSchedulableJobArgumentCaptor = ArgumentCaptor.forClass(RunOnceSchedulableJob.class);
        verify(motechSchedulerService).safeScheduleRunOnceJob(runOnceSchedulableJobArgumentCaptor.capture());
        ArgumentCaptor<SubscriptionReportRequest> subscriptionReportRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionReportRequest.class);
        verify(reportingServiceImpl).reportSubscriptionCreation(subscriptionReportRequestArgumentCaptor.capture());

        RunOnceSchedulableJob runOnceSchedulableJobArgumentCaptorValue = runOnceSchedulableJobArgumentCaptor.getValue();
        assertEquals(dob.toDate(), runOnceSchedulableJobArgumentCaptorValue.getStartDate());
        assertEquals(SubscriptionEventKeys.EARLY_SUBSCRIPTION, runOnceSchedulableJobArgumentCaptorValue.getMotechEvent().getSubject());
        assertEquals(OMSubscriptionRequest.class, runOnceSchedulableJobArgumentCaptorValue.getMotechEvent().getParameters().get("0").getClass());

        SubscriptionReportRequest subscriptionCreationReportRequest = subscriptionReportRequestArgumentCaptor.getValue();
        assertEquals(subscriptionRequest.getMsisdn(), subscriptionCreationReportRequest.getMsisdn().toString());
        assertEquals(subscriptionRequest.getPack().toString(), subscriptionCreationReportRequest.getPack());
        assertEquals(subscriptionRequest.getSubscriber().getDateOfBirth(), subscriptionCreationReportRequest.getDateOfBirth());
        assertEquals(SubscriptionStatus.NEW_EARLY.toString(), subscriptionCreationReportRequest.getSubscriptionStatus());
    }

    @Test
    public void shouldChangeThePackOfAnSubscription() {
        String msisdn = "1234567890";
        SubscriptionPack requestedPack = SubscriptionPack.NANHI_KILKARI;
        SubscriptionPack currentPack = SubscriptionPack.BARI_KILKARI;
        DateTime dateOfBirth = DateTime.now();

        Subscription existingSubscription = new SubscriptionBuilder().withDefaults().withStatus(SubscriptionStatus.ACTIVE).withPack(currentPack).withMsisdn(msisdn).build();
        String subscriptionId = existingSubscription.getSubscriptionId();
        when(allSubscriptions.findBySubscriptionId(subscriptionId)).thenReturn(existingSubscription);

        ChangePackRequest changePackRequest = new ChangePackRequest(msisdn, subscriptionId, requestedPack, Channel.CALL_CENTER, dateOfBirth.plusWeeks(20), null, dateOfBirth);

        subscriptionService.changePack(changePackRequest);

        InOrder order = inOrder(allSubscriptions, reportingServiceImpl, onMobileSubscriptionManagerPublisher, subscriptionValidator, changePackProcessor);
        order.verify(subscriptionValidator).validateSubscriptionExists(subscriptionId);
        order.verify(changePackProcessor).process(changePackRequest);
        order.verify(allSubscriptions, times(2)).findBySubscriptionId(subscriptionId);
        order.verify(allSubscriptions).update(existingSubscription);

        ArgumentCaptor<OMSubscriptionRequest> omDeactivationRequestCaptor = ArgumentCaptor.forClass(OMSubscriptionRequest.class);
        order.verify(onMobileSubscriptionManagerPublisher).processDeactivation(omDeactivationRequestCaptor.capture());
        OMSubscriptionRequest omDeactivationRequest = omDeactivationRequestCaptor.getValue();

        assertEquals(msisdn, omDeactivationRequest.getMsisdn());
        assertEquals(subscriptionId, omDeactivationRequest.getSubscriptionId());
        assertEquals(currentPack, omDeactivationRequest.getPack());

        ArgumentCaptor<Subscription> subscriptionArgumentCaptor = ArgumentCaptor.forClass(Subscription.class);
        order.verify(allSubscriptions).add(subscriptionArgumentCaptor.capture());
        Subscription subscriptionSaved = subscriptionArgumentCaptor.getValue();
        assertEquals(msisdn, subscriptionSaved.getMsisdn());
        assertEquals(requestedPack, subscriptionSaved.getPack());

        order.verify(onMobileSubscriptionManagerPublisher).sendActivationRequest(any(OMSubscriptionRequest.class));

        ArgumentCaptor<SubscriptionChangePackRequest> reportRequestCaptor = ArgumentCaptor.forClass(SubscriptionChangePackRequest.class);
        order.verify(reportingServiceImpl).reportChangePack(reportRequestCaptor.capture());
        SubscriptionChangePackRequest reportRequest = reportRequestCaptor.getValue();
        assertEquals(msisdn, reportRequest.getMsisdn());
        assertEquals(subscriptionSaved.getSubscriptionId(), reportRequest.getSubscriptionId());
        assertEquals(requestedPack.name(), reportRequest.getPack());
        assertEquals(Channel.CALL_CENTER.name(), reportRequest.getChannel());
        assertEquals(dateOfBirth, reportRequest.getDateOfBirth());
        assertNull(reportRequest.getExpectedDateOfDelivery());
    }

    @Test
    public void shouldChangeMsisdn() {
        String oldMsisdn = "9876543210";
        String newMsisdn = "9876543211";
        ChangeMsisdnRequest changeMsisdnRequest = new ChangeMsisdnRequest(oldMsisdn, newMsisdn);
        changeMsisdnRequest.setPacks(Arrays.asList(SubscriptionPack.NANHI_KILKARI));

        Subscription subscription1 = new Subscription(oldMsisdn, SubscriptionPack.NANHI_KILKARI, DateTime.now().minusWeeks(2).minusHours(1), SubscriptionStatus.ACTIVE);
        Subscription subscription2 = new Subscription(oldMsisdn, SubscriptionPack.CHOTI_KILKARI, DateTime.now(), SubscriptionStatus.ACTIVE);
        String beneficiaryName = "name";
        Integer beneficiaryAge = 25;
        SubscriberResponse subscriberResponse = new SubscriberResponse(beneficiaryName, beneficiaryAge, null, null, null);

        when(allSubscriptions.findSubscriptionsInProgress(oldMsisdn)).thenReturn(Arrays.asList(subscription1, subscription2));
        when(allSubscriptions.findBySubscriptionId(subscription1.getSubscriptionId())).thenReturn(subscription1);
        when(reportingServiceImpl.getSubscriber(subscription1.getSubscriptionId())).thenReturn(subscriberResponse);

        subscriptionService.changeMsisdn(changeMsisdnRequest);

        verify(changeMsisdnValidator).validate(changeMsisdnRequest);

        ArgumentCaptor<SubscriptionStateChangeRequest> subscriptionStateChangeRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionStateChangeRequest.class);
        verify(reportingServiceImpl).reportSubscriptionStateChange(subscriptionStateChangeRequestArgumentCaptor.capture());
        SubscriptionStateChangeRequest subscriptionStateChangeRequest = subscriptionStateChangeRequestArgumentCaptor.getValue();

        assertEquals(subscription1.getSubscriptionId(), subscriptionStateChangeRequest.getSubscriptionId());
        assertEquals(SubscriptionStatus.DEACTIVATION_REQUEST_RECEIVED.toString(), subscriptionStateChangeRequest.getSubscriptionStatus());

        ArgumentCaptor<OMSubscriptionRequest> deactivationRequest = ArgumentCaptor.forClass(OMSubscriptionRequest.class);
        verify(onMobileSubscriptionManagerPublisher).processDeactivation(deactivationRequest.capture());
        OMSubscriptionRequest actualDeactivationRequest = deactivationRequest.getValue();

        assertEquals(oldMsisdn, actualDeactivationRequest.getMsisdn());
        assertEquals(SubscriptionPack.NANHI_KILKARI, actualDeactivationRequest.getPack());
        assertEquals(subscription1.getSubscriptionId(), actualDeactivationRequest.getSubscriptionId());

        ArgumentCaptor<Subscription> subscriptionArgumentCaptor = ArgumentCaptor.forClass(Subscription.class);
        verify(allSubscriptions).add(subscriptionArgumentCaptor.capture());
        Subscription actualSubscription = subscriptionArgumentCaptor.getValue();

        assertEquals(newMsisdn, actualSubscription.getMsisdn());
        assertEquals(SubscriptionPack.NANHI_KILKARI, actualSubscription.getPack());

        ArgumentCaptor<OMSubscriptionRequest> activationRequest = ArgumentCaptor.forClass(OMSubscriptionRequest.class);
        verify(onMobileSubscriptionManagerPublisher).sendActivationRequest(activationRequest.capture());
        OMSubscriptionRequest actualActivationRequest = activationRequest.getValue();

        assertEquals(newMsisdn, actualActivationRequest.getMsisdn());
        assertEquals(SubscriptionPack.NANHI_KILKARI, actualActivationRequest.getPack());

        ArgumentCaptor<SubscriptionReportRequest> subscriptionReportRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionReportRequest.class);
        verify(reportingServiceImpl).reportSubscriptionCreation(subscriptionReportRequestArgumentCaptor.capture());
        SubscriptionReportRequest subscriptionReportRequest = subscriptionReportRequestArgumentCaptor.getValue();

        assertEquals(beneficiaryName, subscriptionReportRequest.getName());
        assertEquals(beneficiaryAge, subscriptionReportRequest.getAgeOfBeneficiary());
    }

    @Test
    public void shouldChangeMsisdnForEarlySubscription() {
        String oldMsisdn = "9876543210";
        String newMsisdn = "9876543211";
        ChangeMsisdnRequest changeMsisdnRequest = new ChangeMsisdnRequest(oldMsisdn, newMsisdn);
        changeMsisdnRequest.setPacks(Arrays.asList(SubscriptionPack.NANHI_KILKARI));

        Subscription subscription1 = new Subscription(oldMsisdn, SubscriptionPack.NANHI_KILKARI, DateTime.now(), SubscriptionStatus.NEW_EARLY);
        Subscription subscription2 = new Subscription(oldMsisdn, SubscriptionPack.CHOTI_KILKARI, DateTime.now(), SubscriptionStatus.ACTIVE);

        when(allSubscriptions.findSubscriptionsInProgress(oldMsisdn)).thenReturn(Arrays.asList(subscription1, subscription2));
        when(allSubscriptions.findBySubscriptionId(subscription1.getSubscriptionId())).thenReturn(subscription1);

        subscriptionService.changeMsisdn(changeMsisdnRequest);

        ArgumentCaptor<Subscription> subscriptionArgumentCaptor = ArgumentCaptor.forClass(Subscription.class);
        verify(allSubscriptions).update(subscriptionArgumentCaptor.capture());
        Subscription updatedSubscription = subscriptionArgumentCaptor.getValue();

        assertEquals(subscription1.getSubscriptionId(), updatedSubscription.getSubscriptionId());
        assertEquals(subscription1.getPack(), updatedSubscription.getPack());
        assertEquals(subscription1.getMsisdn(), updatedSubscription.getMsisdn());

        verifyZeroInteractions(onMobileSubscriptionManagerPublisher);
        verify(reportingServiceImpl).reportChangeMsisdnForSubscriber(subscription1.getSubscriptionId(), newMsisdn);
    }
}
