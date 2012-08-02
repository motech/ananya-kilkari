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
import org.motechproject.ananya.kilkari.message.service.CampaignMessageAlertService;
import org.motechproject.ananya.kilkari.messagecampaign.request.MessageCampaignRequest;
import org.motechproject.ananya.kilkari.messagecampaign.service.MessageCampaignService;
import org.motechproject.ananya.kilkari.obd.service.CampaignMessageService;
import org.motechproject.ananya.kilkari.reporting.domain.SubscriberReportRequest;
import org.motechproject.ananya.kilkari.reporting.domain.SubscriptionCreationReportRequest;
import org.motechproject.ananya.kilkari.reporting.domain.SubscriptionStateChangeReportRequest;
import org.motechproject.ananya.kilkari.reporting.service.ReportingServiceImpl;
import org.motechproject.ananya.kilkari.subscription.builder.SubscriptionBuilder;
import org.motechproject.ananya.kilkari.subscription.builder.SubscriptionRequestBuilder;
import org.motechproject.ananya.kilkari.subscription.domain.*;
import org.motechproject.ananya.kilkari.subscription.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.subscription.repository.AllInboxMessages;
import org.motechproject.ananya.kilkari.subscription.repository.AllSubscriptions;
import org.motechproject.ananya.kilkari.subscription.repository.KilkariPropertiesData;
import org.motechproject.ananya.kilkari.subscription.repository.OnMobileSubscriptionGateway;
import org.motechproject.ananya.kilkari.subscription.request.OMSubscriptionRequest;
import org.motechproject.ananya.kilkari.subscription.service.request.Location;
import org.motechproject.ananya.kilkari.subscription.service.request.SubscriberUpdateRequest;
import org.motechproject.ananya.kilkari.subscription.service.request.SubscriptionRequest;
import org.motechproject.ananya.kilkari.subscription.validators.SubscriptionValidator;
import org.motechproject.model.DayOfWeek;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.RunOnceSchedulableJob;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.*;
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
    private KilkariInboxService kilkariInboxService;
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

    @Before
    public void setUp() {
        initMocks(this);
        subscriptionService = new SubscriptionService(allSubscriptions, onMobileSubscriptionManagerPublisher, subscriptionValidator, reportingServiceImpl, kilkariInboxService, messageCampaignService, onMobileSubscriptionGateway, campaignMessageService, campaignMessageAlertService, kilkariPropertiesData, motechSchedulerService);
    }

    @Test
    public void shouldCreateNewSubscription() {
        String msisdn = "1234567890";
        Channel channel = Channel.IVR;
        SubscriptionPack subscriptionPack = SubscriptionPack.TWELVE_MONTHS;
        ArgumentCaptor<org.motechproject.ananya.kilkari.subscription.domain.Subscription> subscriptionArgumentCaptor = ArgumentCaptor.forClass(org.motechproject.ananya.kilkari.subscription.domain.Subscription.class);
        SubscriptionRequest subscription = new SubscriptionRequestBuilder().withDefaults().withMsisdn(msisdn).withPack(subscriptionPack).build();

        Subscription createdSubscription = subscriptionService.createSubscription(subscription, channel);

        assertNotNull(createdSubscription);
        verify(allSubscriptions).add(subscriptionArgumentCaptor.capture());
        org.motechproject.ananya.kilkari.subscription.domain.Subscription subscriptionSaved = subscriptionArgumentCaptor.getValue();
        assertEquals(msisdn, subscriptionSaved.getMsisdn());
        assertEquals(subscriptionPack, subscriptionSaved.getPack());
        assertEquals(createdSubscription, subscriptionSaved);
    }

    @Test
    public void shouldValidateSubscriptionRequestOnCreation() {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withDefaults().build();

        subscriptionService.createSubscription(subscriptionRequest, Channel.CALL_CENTER);

        verify(subscriptionValidator).validate(any(SubscriptionRequest.class));
    }

    @Test
    public void shouldNotCreateSubscriptionIfValidationFails() {
        SubscriptionRequest subscription = new SubscriptionRequestBuilder().withDefaults().build();
        doThrow(new ValidationException("")).when(subscriptionValidator).validate(any(SubscriptionRequest.class));

        try {
            subscriptionService.createSubscription(subscription, Channel.CALL_CENTER);
        } catch (ValidationException e) {
            //ignore
        }

        verify(allSubscriptions, never()).add(any(org.motechproject.ananya.kilkari.subscription.domain.Subscription.class));
        verify(messageCampaignService, never()).start(any(MessageCampaignRequest.class), any(Integer.class), any(Integer.class));
        verify(reportingServiceImpl, never()).reportSubscriptionCreation(any(SubscriptionCreationReportRequest.class));
        verify(onMobileSubscriptionManagerPublisher, never()).sendActivationRequest(any(OMSubscriptionRequest.class));
    }

    @Test
    public void shouldPublishReportingEventOnCreatingSubscription() {
        String msisdn = "1234567890";
        Channel channel = Channel.IVR;
        SubscriptionPack subscriptionPack = SubscriptionPack.TWELVE_MONTHS;
        ArgumentCaptor<SubscriptionCreationReportRequest> subscriptionReportRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionCreationReportRequest.class);
        SubscriptionRequest subscription = new SubscriptionRequestBuilder().withDefaults().withMsisdn(msisdn).withPack(subscriptionPack).build();

        subscriptionService.createSubscription(subscription, channel);

        verify(reportingServiceImpl).reportSubscriptionCreation(subscriptionReportRequestArgumentCaptor.capture());
        SubscriptionCreationReportRequest actualSubscriptionCreationReportRequest = subscriptionReportRequestArgumentCaptor.getValue();
        assertEquals(msisdn, actualSubscriptionCreationReportRequest.getMsisdn());
        assertEquals(subscriptionPack.name(), actualSubscriptionCreationReportRequest.getPack());
        assertEquals(channel.name(), actualSubscriptionCreationReportRequest.getChannel());
    }

    @Test
    public void shouldSendActivationRequestToOMSubscriptionManager_OnCreatingSubscription() {
        String msisdn = "1234567890";
        Channel channel = Channel.IVR;
        SubscriptionPack subscriptionPack = SubscriptionPack.TWELVE_MONTHS;
        ArgumentCaptor<OMSubscriptionRequest> subscriptionActivationRequestArgumentCaptor = ArgumentCaptor.forClass(OMSubscriptionRequest.class);
        SubscriptionRequest subscription = new SubscriptionRequestBuilder().withDefaults().withMsisdn(msisdn).withPack(subscriptionPack).build();

        subscriptionService.createSubscription(subscription, channel);

        verify(onMobileSubscriptionManagerPublisher).sendActivationRequest(subscriptionActivationRequestArgumentCaptor.capture());
        OMSubscriptionRequest actualOMSubscriptionRequest = subscriptionActivationRequestArgumentCaptor.getValue();
        assertEquals(msisdn, actualOMSubscriptionRequest.getMsisdn());
        assertEquals(subscriptionPack, actualOMSubscriptionRequest.getPack());
        assertEquals(channel, actualOMSubscriptionRequest.getChannel());

    }

    @Test
    public void shouldGetSubscriptionsResponseForAGivenMsisdn() {
        String msisdn = "1234567890";
        ArrayList<org.motechproject.ananya.kilkari.subscription.domain.Subscription> subscriptionsToBeReturned = new ArrayList<>();
        subscriptionsToBeReturned.add(new org.motechproject.ananya.kilkari.subscription.domain.Subscription(msisdn, SubscriptionPack.TWELVE_MONTHS, DateTime.now()));
        subscriptionsToBeReturned.add(new org.motechproject.ananya.kilkari.subscription.domain.Subscription(msisdn, SubscriptionPack.FIFTEEN_MONTHS, DateTime.now()));
        when(allSubscriptions.findByMsisdn(msisdn)).thenReturn(subscriptionsToBeReturned);

        List<Subscription> subscriptions = subscriptionService.findByMsisdn(msisdn);

        assertEquals(2, subscriptions.size());
        assertEquals(msisdn, subscriptions.get(0).getMsisdn());
        assertEquals(SubscriptionPack.TWELVE_MONTHS, subscriptions.get(0).getPack());
        assertEquals(msisdn, subscriptions.get(1).getMsisdn());
        assertEquals(SubscriptionPack.FIFTEEN_MONTHS, subscriptions.get(1).getPack());
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
        org.motechproject.ananya.kilkari.subscription.domain.Subscription mockedSubscription = mock(org.motechproject.ananya.kilkari.subscription.domain.Subscription.class);
        OMSubscriptionRequest omSubscriptionRequest = new OMSubscriptionRequest("1234567890", SubscriptionPack.FIFTEEN_MONTHS, Channel.IVR, subscriptionId);
        when(mockedSubscription.getStatus()).thenReturn(status);
        when(mockedSubscription.getSubscriptionId()).thenReturn(subscriptionId);
        when(allSubscriptions.findBySubscriptionId(subscriptionId)).thenReturn(mockedSubscription);

        subscriptionService.activationRequested(omSubscriptionRequest);

        InOrder order = inOrder(allSubscriptions, mockedSubscription, reportingServiceImpl);
        order.verify(allSubscriptions).findBySubscriptionId(subscriptionId);
        order.verify(mockedSubscription).activationRequestSent();
        order.verify(allSubscriptions).update(mockedSubscription);
        ArgumentCaptor<SubscriptionStateChangeReportRequest> subscriptionStateChangeReportRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionStateChangeReportRequest.class);
        order.verify(reportingServiceImpl).reportSubscriptionStateChange(subscriptionStateChangeReportRequestArgumentCaptor.capture());
        SubscriptionStateChangeReportRequest subscriptionStateChangeReportRequest = subscriptionStateChangeReportRequestArgumentCaptor.getValue();
        verify(onMobileSubscriptionGateway).activateSubscription(omSubscriptionRequest);
        assertEquals(subscriptionId, subscriptionStateChangeReportRequest.getSubscriptionId());
        assertEquals(status.name(), subscriptionStateChangeReportRequest.getSubscriptionStatus());
        assertNull(subscriptionStateChangeReportRequest.getOperator());
    }

    @Test
    public void shouldUpdateTheSubscriptionStatusToDeactivationRequested_WhenDeactivationIsRequested() {
        String subscriptionId = "abcd1234";
        SubscriptionStatus status = SubscriptionStatus.ACTIVE;
        org.motechproject.ananya.kilkari.subscription.domain.Subscription mockedSubscription = mock(org.motechproject.ananya.kilkari.subscription.domain.Subscription.class);

        when(mockedSubscription.getStatus()).thenReturn(status);
        when(mockedSubscription.getSubscriptionId()).thenReturn(subscriptionId);
        when(mockedSubscription.isInProgress()).thenReturn(true);
        when(allSubscriptions.findBySubscriptionId(subscriptionId)).thenReturn(mockedSubscription);

        subscriptionService.requestDeactivation(new DeactivationRequest(subscriptionId, Channel.IVR, DateTime.now()));

        InOrder order = inOrder(allSubscriptions, mockedSubscription, reportingServiceImpl);
        order.verify(allSubscriptions).findBySubscriptionId(subscriptionId);
        order.verify(mockedSubscription).deactivationRequestReceived();
        order.verify(allSubscriptions).update(mockedSubscription);
        ArgumentCaptor<SubscriptionStateChangeReportRequest> subscriptionStateChangeReportRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionStateChangeReportRequest.class);
        order.verify(reportingServiceImpl).reportSubscriptionStateChange(subscriptionStateChangeReportRequestArgumentCaptor.capture());
        SubscriptionStateChangeReportRequest subscriptionStateChangeReportRequest = subscriptionStateChangeReportRequestArgumentCaptor.getValue();

        assertEquals(subscriptionId, subscriptionStateChangeReportRequest.getSubscriptionId());
        assertEquals(status.name(), subscriptionStateChangeReportRequest.getSubscriptionStatus());
        assertNull(subscriptionStateChangeReportRequest.getOperator());
    }

    @Test
    public void shouldUpdateTheSubscriptionStatusToPendingDeactivation_WhenDeactivationRequestedIsComplete() {
        String subscriptionId = "abcd1234";
        SubscriptionStatus status = SubscriptionStatus.DEACTIVATION_REQUEST_RECEIVED;
        org.motechproject.ananya.kilkari.subscription.domain.Subscription mockedSubscription = mock(org.motechproject.ananya.kilkari.subscription.domain.Subscription.class);
        OMSubscriptionRequest omSubscriptionRequest = new OMSubscriptionRequest("1234567890", SubscriptionPack.FIFTEEN_MONTHS, Channel.IVR, subscriptionId);
        when(mockedSubscription.getStatus()).thenReturn(status);
        when(mockedSubscription.getSubscriptionId()).thenReturn(subscriptionId);
        when(allSubscriptions.findBySubscriptionId(subscriptionId)).thenReturn(mockedSubscription);

        subscriptionService.deactivationRequested(omSubscriptionRequest);

        InOrder order = inOrder(allSubscriptions, mockedSubscription, reportingServiceImpl);
        order.verify(allSubscriptions).findBySubscriptionId(subscriptionId);
        order.verify(mockedSubscription).deactivationRequestSent();
        order.verify(allSubscriptions).update(mockedSubscription);
        ArgumentCaptor<SubscriptionStateChangeReportRequest> subscriptionStateChangeReportRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionStateChangeReportRequest.class);
        order.verify(reportingServiceImpl).reportSubscriptionStateChange(subscriptionStateChangeReportRequestArgumentCaptor.capture());
        SubscriptionStateChangeReportRequest subscriptionStateChangeReportRequest = subscriptionStateChangeReportRequestArgumentCaptor.getValue();
        verify(onMobileSubscriptionGateway).deactivateSubscription(omSubscriptionRequest);
        assertEquals(subscriptionId, subscriptionStateChangeReportRequest.getSubscriptionId());
        assertEquals(status.name(), subscriptionStateChangeReportRequest.getSubscriptionStatus());
        assertNull(subscriptionStateChangeReportRequest.getOperator());
    }

    @Test
    public void shouldUpdateTheSubscriptionStatusToActivationFailed_GivenSubscriptionId() {
        SubscriptionStatus status = SubscriptionStatus.ACTIVATION_FAILED;
        org.motechproject.ananya.kilkari.subscription.domain.Subscription mockedSubscription = mock(org.motechproject.ananya.kilkari.subscription.domain.Subscription.class);
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
        ArgumentCaptor<SubscriptionStateChangeReportRequest> subscriptionStateChangeReportRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionStateChangeReportRequest.class);
        order.verify(reportingServiceImpl).reportSubscriptionStateChange(subscriptionStateChangeReportRequestArgumentCaptor.capture());
        SubscriptionStateChangeReportRequest subscriptionStateChangeReportRequest = subscriptionStateChangeReportRequestArgumentCaptor.getValue();

        assertEquals(subscriptionId, subscriptionStateChangeReportRequest.getSubscriptionId());
        assertEquals(status.name(), subscriptionStateChangeReportRequest.getSubscriptionStatus());
        assertEquals(reason, subscriptionStateChangeReportRequest.getReason());
        assertEquals(operator, subscriptionStateChangeReportRequest.getOperator());
    }

    @Test
    public void shouldActivateSubscriptionAndScheduleCampaign() {
        org.motechproject.ananya.kilkari.subscription.domain.Subscription subscription = new SubscriptionBuilder().withDefaults().build();
        String operator = "airtel";
        DateTime activatedOn = DateTime.now();
        String subscriptionId = subscription.getSubscriptionId();
        int deltaDays = 2;
        int deltaMinutes = 30;
        when(allSubscriptions.findBySubscriptionId(subscriptionId)).thenReturn(subscription);
        when(kilkariPropertiesData.getCampaignScheduleDeltaDays()).thenReturn(deltaDays);
        when(kilkariPropertiesData.getCampaignScheduleDeltaMinutes()).thenReturn(deltaMinutes);

        subscriptionService.activate(subscriptionId, activatedOn, operator);

        ArgumentCaptor<org.motechproject.ananya.kilkari.subscription.domain.Subscription> captor = ArgumentCaptor.forClass(org.motechproject.ananya.kilkari.subscription.domain.Subscription.class);
        verify(allSubscriptions).update(captor.capture());
        org.motechproject.ananya.kilkari.subscription.domain.Subscription actualSubscription = captor.getValue();
        assertEquals(SubscriptionStatus.ACTIVE, actualSubscription.getStatus());
        assertEquals(subscriptionId, actualSubscription.getSubscriptionId());

        ArgumentCaptor<SubscriptionStateChangeReportRequest> subscriptionStateChangeReportRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionStateChangeReportRequest.class);
        verify(reportingServiceImpl).reportSubscriptionStateChange(subscriptionStateChangeReportRequestArgumentCaptor.capture());
        SubscriptionStateChangeReportRequest stateChangeReportRequest = subscriptionStateChangeReportRequestArgumentCaptor.getValue();
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

        org.motechproject.ananya.kilkari.subscription.domain.Subscription subscription = new org.motechproject.ananya.kilkari.subscription.domain.Subscription() {
            public String getSubscriptionId() {
                return subscriptionId;
            }
        };
        subscription.setStatus(SubscriptionStatus.SUSPENDED);
        when(allSubscriptions.findBySubscriptionId(subscriptionId)).thenReturn(subscription);

        subscriptionService.renewSubscription(subscriptionId, renewalDate, graceCount);

        verify(allSubscriptions).update(subscription);
        ArgumentCaptor<SubscriptionStateChangeReportRequest> subscriptionStateChangeReportRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionStateChangeReportRequest.class);
        verify(reportingServiceImpl).reportSubscriptionStateChange(subscriptionStateChangeReportRequestArgumentCaptor.capture());
        SubscriptionStateChangeReportRequest subscriptionStateChangeReportRequest = subscriptionStateChangeReportRequestArgumentCaptor.getValue();

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
        org.motechproject.ananya.kilkari.subscription.domain.Subscription subscription = new org.motechproject.ananya.kilkari.subscription.domain.Subscription() {
            public String getSubscriptionId() {
                return subscriptionId;
            }
        };
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        when(allSubscriptions.findBySubscriptionId(subscriptionId)).thenReturn(subscription);

        subscriptionService.suspendSubscription(subscriptionId, renewalDate, reason, graceCount);

        verify(allSubscriptions).update(subscription);
        ArgumentCaptor<SubscriptionStateChangeReportRequest> subscriptionStateChangeReportRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionStateChangeReportRequest.class);
        verify(reportingServiceImpl).reportSubscriptionStateChange(subscriptionStateChangeReportRequestArgumentCaptor.capture());
        SubscriptionStateChangeReportRequest subscriptionStateChangeReportRequest = subscriptionStateChangeReportRequestArgumentCaptor.getValue();

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
        org.motechproject.ananya.kilkari.subscription.domain.Subscription subscription = new org.motechproject.ananya.kilkari.subscription.domain.Subscription("1234567890", SubscriptionPack.FIFTEEN_MONTHS, DateTime.now()) {
            public String getSubscriptionId() {
                return subscriptionId;
            }
        };
        subscription.setStatus(SubscriptionStatus.SUSPENDED);

        when(allSubscriptions.findBySubscriptionId(subscriptionId)).thenReturn(subscription);

        subscriptionService.deactivateSubscription(subscriptionId, date, reason, graceCount);

        verify(allSubscriptions).update(subscription);
        ArgumentCaptor<SubscriptionStateChangeReportRequest> subscriptionStateChangeReportRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionStateChangeReportRequest.class);
        verify(reportingServiceImpl).reportSubscriptionStateChange(subscriptionStateChangeReportRequestArgumentCaptor.capture());
        SubscriptionStateChangeReportRequest subscriptionStateChangeReportRequest = subscriptionStateChangeReportRequestArgumentCaptor.getValue();

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
        org.motechproject.ananya.kilkari.subscription.domain.Subscription subscription = new org.motechproject.ananya.kilkari.subscription.domain.Subscription(msisdn, SubscriptionPack.SEVEN_MONTHS, DateTime.now());
        when(allSubscriptions.findBySubscriptionId(subscriptionId)).thenReturn(subscription);
        Channel channel = Channel.IVR;

        subscriptionService.requestDeactivation(new DeactivationRequest(subscriptionId, channel, DateTime.now()));

        ArgumentCaptor<org.motechproject.ananya.kilkari.subscription.domain.Subscription> captor = ArgumentCaptor.forClass(org.motechproject.ananya.kilkari.subscription.domain.Subscription.class);
        ArgumentCaptor<OMSubscriptionRequest> captor1 = ArgumentCaptor.forClass(OMSubscriptionRequest.class);
        verify(allSubscriptions).update(captor.capture());
        org.motechproject.ananya.kilkari.subscription.domain.Subscription actualSubscription = captor.getValue();
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
        final SubscriptionPack pack = SubscriptionPack.TWELVE_MONTHS;
        org.motechproject.ananya.kilkari.subscription.domain.Subscription subscription = new SubscriptionBuilder().withMsisdn(msisdn).withStatus(SubscriptionStatus.ACTIVE).build();
        final String subscriptionId = subscription.getSubscriptionId();
        final OMSubscriptionRequest omSubscriptionRequest = new OMSubscriptionRequest(msisdn, pack, null, subscriptionId);
        when(allSubscriptions.findBySubscriptionId(subscriptionId)).thenReturn(subscription);

        subscriptionService.subscriptionComplete(omSubscriptionRequest);

        ArgumentCaptor<org.motechproject.ananya.kilkari.subscription.domain.Subscription> subscriptionArgumentCaptor = ArgumentCaptor.forClass(org.motechproject.ananya.kilkari.subscription.domain.Subscription.class);
        verify(allSubscriptions).update(subscriptionArgumentCaptor.capture());
        org.motechproject.ananya.kilkari.subscription.domain.Subscription actualSubscription = subscriptionArgumentCaptor.getValue();
        Assert.assertEquals(msisdn, actualSubscription.getMsisdn());
        Assert.assertEquals(SubscriptionStatus.PENDING_COMPLETION, actualSubscription.getStatus());

        ArgumentCaptor<SubscriptionStateChangeReportRequest> subscriptionStateChangeReportRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionStateChangeReportRequest.class);
        verify(reportingServiceImpl).reportSubscriptionStateChange(subscriptionStateChangeReportRequestArgumentCaptor.capture());
        SubscriptionStateChangeReportRequest subscriptionStateChangeReportRequest = subscriptionStateChangeReportRequestArgumentCaptor.getValue();
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
        SubscriptionPack pack = SubscriptionPack.SEVEN_MONTHS;
        org.motechproject.ananya.kilkari.subscription.domain.Subscription subscription = new org.motechproject.ananya.kilkari.subscription.domain.Subscription(msisdn, pack, DateTime.now());
        String subscriptionId = subscription.getSubscriptionId();
        when(allSubscriptions.findBySubscriptionId(subscriptionId)).thenReturn(subscription);

        subscriptionService.subscriptionComplete(new OMSubscriptionRequest(msisdn, pack, Channel.IVR, subscriptionId));

        verify(kilkariInboxService).scheduleInboxDeletion(subscription);
    }

    @Test
    public void shouldNotSendDeactivationRequestAgainIfTheExistingSubscriptionIsAlreadyInDeactivatedState() {
        final String msisdn = "9988776655";
        final org.motechproject.ananya.kilkari.subscription.domain.Subscription subscription = new SubscriptionBuilder().withDefaults().withStatus(SubscriptionStatus.DEACTIVATED).build();
        final String subscriptionId = subscription.getSubscriptionId();
        final SubscriptionPack pack = SubscriptionPack.TWELVE_MONTHS;
        OMSubscriptionRequest value = new OMSubscriptionRequest(msisdn, pack, null, subscriptionId);
        when(allSubscriptions.findBySubscriptionId(subscriptionId)).thenReturn(subscription);

        subscriptionService.subscriptionComplete(value);

        verify(onMobileSubscriptionGateway, never()).deactivateSubscription(any(OMSubscriptionRequest.class));
        verify(onMobileSubscriptionGateway, never()).deactivateSubscription(any(OMSubscriptionRequest.class));
        verify(kilkariInboxService, never()).scheduleInboxDeletion(any(org.motechproject.ananya.kilkari.subscription.domain.Subscription.class));
        verify(campaignMessageAlertService, never()).deleteFor(any(String.class));
    }

    @Test
    public void shouldScheduleInboxDeletionUponSubscriptionDeactivation() {
        org.motechproject.ananya.kilkari.subscription.domain.Subscription subscription = new org.motechproject.ananya.kilkari.subscription.domain.Subscription("1234567890", SubscriptionPack.SEVEN_MONTHS, DateTime.now());
        String subscriptionId = subscription.getSubscriptionId();
        when(allSubscriptions.findBySubscriptionId(subscriptionId)).thenReturn(subscription);

        subscriptionService.deactivateSubscription(subscriptionId, DateTime.now(), null, null);

        verify(kilkariInboxService).scheduleInboxDeletion(subscription);
    }

    @Test
    public void shouldUnScheduleCurrentCampaignAndScheduleNewCampaignForMCOrID() {
        String subscriptionId = "subscriptionId";
        String msisdn = "1234567890";
        SubscriptionPack subscriptionPack = SubscriptionPack.FIFTEEN_MONTHS;
        int deltaMinutes = 30;
        DateTime friday = new DateTime(2011, 11, 25, 12, 30, 30);
        DateTime existingCampaignStartDate = friday;
        CampaignChangeReason campaignChangeReason = CampaignChangeReason.MISCARRIAGE;
        org.motechproject.ananya.kilkari.subscription.domain.Subscription subscription = new SubscriptionBuilder().withDefaults().withMsisdn(msisdn).withPack(subscriptionPack).withStartDate(existingCampaignStartDate).build();
        when(allSubscriptions.findBySubscriptionId(subscriptionId)).thenReturn(subscription);
        when(messageCampaignService.getActiveCampaignStartDate(subscriptionId)).thenReturn(existingCampaignStartDate);
        when(kilkariPropertiesData.getCampaignScheduleDeltaMinutes()).thenReturn(deltaMinutes);

        DateTime saturday = existingCampaignStartDate.plusMonths(3);
        DateTime rescheduleRequestedDate = saturday;
        subscriptionService.rescheduleCampaign(new CampaignRescheduleRequest(subscriptionId, campaignChangeReason, rescheduleRequestedDate));

        InOrder order = inOrder(messageCampaignService, campaignMessageService, campaignMessageAlertService);
        ArgumentCaptor<MessageCampaignRequest> campaignUnEnrollmentRequestArgumentCaptor = ArgumentCaptor.forClass(MessageCampaignRequest.class);
        order.verify(messageCampaignService).stop(campaignUnEnrollmentRequestArgumentCaptor.capture());
        MessageCampaignRequest campaignRequest = campaignUnEnrollmentRequestArgumentCaptor.getValue();
        assertEquals(subscription.getSubscriptionId(), campaignRequest.getExternalId());
        assertEquals(subscription.getPack().name(), campaignRequest.getSubscriptionPack());
        assertEquals(subscription.getStartDate(), campaignRequest.getSubscriptionStartDate());

        order.verify(campaignMessageService).deleteCampaignMessagesFor(subscriptionId);
        order.verify(campaignMessageAlertService).clearMessageId(subscriptionId);

        order.verify(messageCampaignService).getActiveCampaignStartDate(subscriptionId);
        ArgumentCaptor<MessageCampaignRequest> campaignEnrollmentRequestArgumentCaptor = ArgumentCaptor.forClass(MessageCampaignRequest.class);
        ArgumentCaptor<Integer> deltaDaysCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> deltaMinutesCaptor = ArgumentCaptor.forClass(Integer.class);
        order.verify(messageCampaignService).start(campaignEnrollmentRequestArgumentCaptor.capture(), deltaDaysCaptor.capture(), deltaMinutesCaptor.capture());
        MessageCampaignRequest campaignEnrollmentRequest = campaignEnrollmentRequestArgumentCaptor.getValue();
        assertEquals(subscriptionId, campaignEnrollmentRequest.getExternalId());
        assertEquals(campaignChangeReason.name(), campaignEnrollmentRequest.getSubscriptionPack());
        assertEquals(nextFriday(rescheduleRequestedDate), campaignEnrollmentRequest.getSubscriptionStartDate());
        assertEquals(0, deltaDaysCaptor.getValue().intValue());
        assertEquals(deltaMinutes, deltaMinutesCaptor.getValue().intValue());
    }

    @Test
    public void shouldThrowExceptionWhenUnScheduleCurrentCampaignIsInvokedForANonActivePack() {
        String subscriptionId = "subscriptionId";
        String message = "some error";
        CampaignChangeReason campaignChangeReason = CampaignChangeReason.MISCARRIAGE;
        org.motechproject.ananya.kilkari.subscription.domain.Subscription subscription = new SubscriptionBuilder().withDefaults().withMsisdn("1234567890").withPack(SubscriptionPack.FIFTEEN_MONTHS).withCreationDate(DateTime.now()).withStatus(SubscriptionStatus.PENDING_COMPLETION).build();
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
        SubscriberUpdateRequest request = mock(SubscriberUpdateRequest.class);
        String message = "some error";
        doThrow(new ValidationException(message)).when(subscriptionValidator).validateSubscriberDetails(request);
        expectedException.expect(ValidationException.class);
        expectedException.expectMessage(message);

        subscriptionService.updateSubscriberDetails(request);

        verify(reportingServiceImpl, never()).reportSubscriberDetailsChange(any(SubscriberReportRequest.class));
    }

    @Test
    public void shouldPublishASubscriberUpdateEvent() {
        String subscriptionId = "subscriptionId";
        Location location = new Location("district", "block", "panchayat");

        subscriptionService.updateSubscriberDetails(new SubscriberUpdateRequest(subscriptionId, Channel.CALL_CENTER.name(), DateTime.now(), "name", "23",
                "20-10-2038", "20-10-1985", location));

        ArgumentCaptor<SubscriberReportRequest> captor = ArgumentCaptor.forClass(SubscriberReportRequest.class);
        verify(reportingServiceImpl).reportSubscriberDetailsChange(captor.capture());
        SubscriberReportRequest reportRequest = captor.getValue();

        assertEquals(subscriptionId, reportRequest.getSubscriptionId());
        assertEquals("20-10-2038", reportRequest.getExpectedDateOfDelivery());
        assertEquals("20-10-1985", reportRequest.getDateOfBirth());
        assertEquals("23", reportRequest.getBeneficiaryAge());
        assertEquals("name", reportRequest.getBeneficiaryName());
        assertEquals("district", reportRequest.getLocation().getDistrict());
        assertEquals("block", reportRequest.getLocation().getBlock());
        assertEquals("panchayat", reportRequest.getLocation().getPanchayat());
    }

    @Test
    public void shouldNotProcessDeactivationRequestWhenSubscriptionIsNotInProgress() {
        String subscriptionId = "subsId";
        org.motechproject.ananya.kilkari.subscription.domain.Subscription mockedSubscription = mock(org.motechproject.ananya.kilkari.subscription.domain.Subscription.class);
        when(allSubscriptions.findBySubscriptionId(subscriptionId)).thenReturn(mockedSubscription);

        subscriptionService.requestDeactivation(new DeactivationRequest(subscriptionId, Channel.IVR, DateTime.now()));

        verify(mockedSubscription, never()).deactivationRequestReceived();
        verify(onMobileSubscriptionManagerPublisher, never()).processDeactivation(Matchers.<OMSubscriptionRequest>any());
        verify(reportingServiceImpl, never()).reportSubscriptionStateChange(Matchers.<SubscriptionStateChangeReportRequest>any());
        verify(allSubscriptions, never()).update(mockedSubscription);
    }

    @Test
    public void shouldBackDateStartDateForALateSubscriptionWhenEDDIsGiven() {
        DateTime edd = DateTime.now().plusWeeks(1);
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withDefaults().withPack(SubscriptionPack.FIFTEEN_MONTHS).withExpectedDateOfDelivery(edd).build();

        subscriptionService.createSubscription(subscriptionRequest, Channel.CALL_CENTER);

        ArgumentCaptor<org.motechproject.ananya.kilkari.subscription.domain.Subscription> subscriptionArgumentCaptor = ArgumentCaptor.forClass(org.motechproject.ananya.kilkari.subscription.domain.Subscription.class);
        verify(allSubscriptions).add(subscriptionArgumentCaptor.capture());
        org.motechproject.ananya.kilkari.subscription.domain.Subscription subscriptionArgumentCaptorValue = subscriptionArgumentCaptor.getValue();
        assertTrue(Weeks.weeksBetween(subscriptionArgumentCaptorValue.getStartDate(), edd).getWeeks() >= 12);
    }

    @Test
    public void shouldBackDateStartDateForALateSubscriptionWhenDOBIsGivenForFifteenMonthsPack() {
        DateTime dob = DateTime.now().minusMonths(1);
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withDefaults().withPack(SubscriptionPack.FIFTEEN_MONTHS).withDateOfBirth(dob).build();

        subscriptionService.createSubscription(subscriptionRequest, Channel.CALL_CENTER);

        ArgumentCaptor<org.motechproject.ananya.kilkari.subscription.domain.Subscription> subscriptionArgumentCaptor = ArgumentCaptor.forClass(org.motechproject.ananya.kilkari.subscription.domain.Subscription.class);
        verify(allSubscriptions).add(subscriptionArgumentCaptor.capture());
        org.motechproject.ananya.kilkari.subscription.domain.Subscription subscriptionArgumentCaptorValue = subscriptionArgumentCaptor.getValue();
        assertTrue(Weeks.weeksBetween(subscriptionArgumentCaptorValue.getStartDate(), dob).getWeeks() >= 12);

    }

    @Test
    public void shouldBackDateStartDateForALateSubscriptionWhenDOBIsGivenForTwelveMonthsPack() {
        DateTime dob = DateTime.now().minusMonths(1);
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withDefaults().withPack(SubscriptionPack.TWELVE_MONTHS).withDateOfBirth(dob).build();

        subscriptionService.createSubscription(subscriptionRequest, Channel.CALL_CENTER);

        ArgumentCaptor<org.motechproject.ananya.kilkari.subscription.domain.Subscription> subscriptionArgumentCaptor = ArgumentCaptor.forClass(org.motechproject.ananya.kilkari.subscription.domain.Subscription.class);
        verify(allSubscriptions).add(subscriptionArgumentCaptor.capture());
        org.motechproject.ananya.kilkari.subscription.domain.Subscription subscriptionArgumentCaptorValue = subscriptionArgumentCaptor.getValue();
        Assert.assertEquals(dob, subscriptionArgumentCaptorValue.getStartDate());

    }

    @Test
    public void shouldBackDateStartDateForALateSubscriptionWhenDOBIsGivenForSevenMonthsPack() {
        DateTime dob = DateTime.now().minusMonths(1);
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withDefaults().withPack(SubscriptionPack.SEVEN_MONTHS).withDateOfBirth(dob).build();

        subscriptionService.createSubscription(subscriptionRequest, Channel.CALL_CENTER);

        ArgumentCaptor<org.motechproject.ananya.kilkari.subscription.domain.Subscription> subscriptionArgumentCaptor = ArgumentCaptor.forClass(org.motechproject.ananya.kilkari.subscription.domain.Subscription.class);
        verify(allSubscriptions).add(subscriptionArgumentCaptor.capture());
        org.motechproject.ananya.kilkari.subscription.domain.Subscription subscriptionArgumentCaptorValue = subscriptionArgumentCaptor.getValue();
        assertTrue(Weeks.weeksBetween(dob, subscriptionArgumentCaptorValue.getStartDate()).getWeeks() >= 20);

    }

    @Test
    public void shouldBackDateStartDateForALateSubscriptionWhenWeekNumberIsGivenForSevenMonthsPack() {
        Integer weekNumber = 28;
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withDefaults().withPack(SubscriptionPack.SEVEN_MONTHS).withWeek(weekNumber).build();

        subscriptionService.createSubscription(subscriptionRequest, Channel.CALL_CENTER);

        ArgumentCaptor<org.motechproject.ananya.kilkari.subscription.domain.Subscription> subscriptionArgumentCaptor = ArgumentCaptor.forClass(org.motechproject.ananya.kilkari.subscription.domain.Subscription.class);
        verify(allSubscriptions).add(subscriptionArgumentCaptor.capture());
        org.motechproject.ananya.kilkari.subscription.domain.Subscription subscriptionArgumentCaptorValue = subscriptionArgumentCaptor.getValue();
        assertEquals(subscriptionRequest.getCreationDate().minusWeeks(27), subscriptionArgumentCaptorValue.getStartDate());

    }

    @Test
    public void shouldSetDateForAnEarlySubscriptionWhenEDDIsGivenForFifteenMonths() {
        DateTime now = DateTime.now();
        DateTime edd = now.plusMonths(4);
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withDefaults().withPack(SubscriptionPack.FIFTEEN_MONTHS).withExpectedDateOfDelivery(edd).build();

        subscriptionService.createSubscription(subscriptionRequest, Channel.CALL_CENTER);

        ArgumentCaptor<org.motechproject.ananya.kilkari.subscription.domain.Subscription> subscriptionArgumentCaptor = ArgumentCaptor.forClass(org.motechproject.ananya.kilkari.subscription.domain.Subscription.class);
        verify(allSubscriptions).add(subscriptionArgumentCaptor.capture());
        org.motechproject.ananya.kilkari.subscription.domain.Subscription subscriptionArgumentCaptorValue = subscriptionArgumentCaptor.getValue();
        assertTrue(Weeks.weeksBetween(subscriptionArgumentCaptorValue.getStartDate(), edd).getWeeks() >= 12);
    }

    @Test
    public void shouldSetDateForAnEarlySubscriptionWhenEDDIsGivenForTwelveMonths() {
        DateTime now = DateTime.now();
        DateTime edd = now.plusMonths(4);
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withDefaults().withPack(SubscriptionPack.TWELVE_MONTHS).withExpectedDateOfDelivery(edd).build();

        subscriptionService.createSubscription(subscriptionRequest, Channel.CALL_CENTER);

        ArgumentCaptor<org.motechproject.ananya.kilkari.subscription.domain.Subscription> subscriptionArgumentCaptor = ArgumentCaptor.forClass(org.motechproject.ananya.kilkari.subscription.domain.Subscription.class);
        verify(allSubscriptions).add(subscriptionArgumentCaptor.capture());
        org.motechproject.ananya.kilkari.subscription.domain.Subscription subscriptionArgumentCaptorValue = subscriptionArgumentCaptor.getValue();
        assertEquals(edd, subscriptionArgumentCaptorValue.getStartDate());
    }

    @Test
    public void shouldSetDateForAnEarlySubscriptionWhenEDDIsGivenForSevenMonths() {
        DateTime now = DateTime.now();
        DateTime edd = now.plusMonths(4);
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withDefaults().withPack(SubscriptionPack.SEVEN_MONTHS).withExpectedDateOfDelivery(edd).build();

        subscriptionService.createSubscription(subscriptionRequest, Channel.CALL_CENTER);

        ArgumentCaptor<org.motechproject.ananya.kilkari.subscription.domain.Subscription> subscriptionArgumentCaptor = ArgumentCaptor.forClass(org.motechproject.ananya.kilkari.subscription.domain.Subscription.class);
        verify(allSubscriptions).add(subscriptionArgumentCaptor.capture());
        org.motechproject.ananya.kilkari.subscription.domain.Subscription subscriptionArgumentCaptorValue = subscriptionArgumentCaptor.getValue();
        assertTrue(Weeks.weeksBetween(edd, subscriptionArgumentCaptorValue.getStartDate()).getWeeks() >= 20);
    }

    @Test
    public void shouldSetDateForAnEarlySubscriptionWhenDOBIsGivenForTwelveMonths() {
        DateTime dob = DateTime.now().plusMonths(3);
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withDefaults().withPack(SubscriptionPack.TWELVE_MONTHS).withDateOfBirth(dob).build();

        subscriptionService.createSubscription(subscriptionRequest, Channel.CALL_CENTER);

        ArgumentCaptor<org.motechproject.ananya.kilkari.subscription.domain.Subscription> subscriptionArgumentCaptor = ArgumentCaptor.forClass(org.motechproject.ananya.kilkari.subscription.domain.Subscription.class);
        verify(allSubscriptions).add(subscriptionArgumentCaptor.capture());
        org.motechproject.ananya.kilkari.subscription.domain.Subscription subscriptionArgumentCaptorValue = subscriptionArgumentCaptor.getValue();
        assertEquals(dob, subscriptionArgumentCaptorValue.getStartDate());

    }

    @Test
    public void shouldSetDateForAnEarlySubscriptionWhenDOBIsGivenForSevenMonths() {
        DateTime dob = DateTime.now().plusMonths(3);
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withDefaults().withPack(SubscriptionPack.SEVEN_MONTHS).withDateOfBirth(dob).build();

        subscriptionService.createSubscription(subscriptionRequest, Channel.CALL_CENTER);

        ArgumentCaptor<org.motechproject.ananya.kilkari.subscription.domain.Subscription> subscriptionArgumentCaptor = ArgumentCaptor.forClass(org.motechproject.ananya.kilkari.subscription.domain.Subscription.class);
        verify(allSubscriptions).add(subscriptionArgumentCaptor.capture());
        org.motechproject.ananya.kilkari.subscription.domain.Subscription subscriptionArgumentCaptorValue = subscriptionArgumentCaptor.getValue();
        assertTrue(Weeks.weeksBetween(dob, subscriptionArgumentCaptorValue.getStartDate()).getWeeks() >= 20);

    }

    @Test
    public void shouldUnScheduleMessageCampaignAndDeleteCampaignMessageAlertOnSuccessfulDeactivationRequest() {
        DateTime createdAt = DateTime.now();
        org.motechproject.ananya.kilkari.subscription.domain.Subscription subscription = new org.motechproject.ananya.kilkari.subscription.domain.Subscription("1234567890", SubscriptionPack.TWELVE_MONTHS, createdAt);
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
        when(subscriptionPack.adjustStartDate(subscriptionRequest.getCreationDate(), weekNumber)).thenReturn(dob);

        subscriptionService.createSubscription(subscriptionRequest, Channel.CALL_CENTER);

        verify(subscriptionPack).adjustStartDate(Matchers.<DateTime>any(), Matchers.<Integer>any());
        verify(subscriptionPack, never()).adjustStartDate(Matchers.<DateTime>any());
    }

    @Test
    public void shouldScheduleEarlySubscription() {
        DateTime dob = DateTime.now().plusMonths(3);
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withDefaults().withPack(SubscriptionPack.TWELVE_MONTHS).withDateOfBirth(dob).build();

        subscriptionService.createSubscription(subscriptionRequest, Channel.CALL_CENTER);

        ArgumentCaptor<RunOnceSchedulableJob> runOnceSchedulableJobArgumentCaptor = ArgumentCaptor.forClass(RunOnceSchedulableJob.class);
        verify(motechSchedulerService).safeScheduleRunOnceJob(runOnceSchedulableJobArgumentCaptor.capture());
        ArgumentCaptor<SubscriptionCreationReportRequest> subscriptionCreationReportRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionCreationReportRequest.class);
        verify(reportingServiceImpl).reportSubscriptionCreation(subscriptionCreationReportRequestArgumentCaptor.capture());

        RunOnceSchedulableJob runOnceSchedulableJobArgumentCaptorValue = runOnceSchedulableJobArgumentCaptor.getValue();
        assertEquals(dob.toDate(), runOnceSchedulableJobArgumentCaptorValue.getStartDate());
        assertEquals(SubscriptionEventKeys.EARLY_SUBSCRIPTION, runOnceSchedulableJobArgumentCaptorValue.getMotechEvent().getSubject());
        assertEquals(OMSubscriptionRequest.class, runOnceSchedulableJobArgumentCaptorValue.getMotechEvent().getParameters().get("0").getClass());

        SubscriptionCreationReportRequest subscriptionCreationReportRequest = subscriptionCreationReportRequestArgumentCaptor.getValue();
        assertEquals(subscriptionRequest.getMsisdn(),subscriptionCreationReportRequest.getMsisdn());
        assertEquals(subscriptionRequest.getPack().toString(),subscriptionCreationReportRequest.getPack());
        assertEquals(subscriptionRequest.getSubscriber().getDateOfBirth(),subscriptionCreationReportRequest.getDob());
    }

    private DateTime nextFriday(DateTime rescheduleRequestedDate) {
        return rescheduleRequestedDate.withDayOfWeek(DayOfWeek.Friday.getValue()).plusWeeks(1);
    }
}

