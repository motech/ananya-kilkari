package org.motechproject.ananya.kilkari.subscription.service;

import junit.framework.Assert;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.reporting.domain.SubscriptionCreationReportRequest;
import org.motechproject.ananya.kilkari.reporting.domain.SubscriptionStateChangeReportRequest;
import org.motechproject.ananya.kilkari.reporting.service.ReportingServiceImpl;
import org.motechproject.ananya.kilkari.subscription.builder.SubscriptionRequestBuilder;
import org.motechproject.ananya.kilkari.subscription.domain.*;
import org.motechproject.ananya.kilkari.subscription.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.subscription.repository.AllInboxMessages;
import org.motechproject.ananya.kilkari.subscription.repository.AllSubscriptions;
import org.motechproject.ananya.kilkari.subscription.validators.SubscriptionRequestValidator;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
    private SubscriptionRequest mockedSubscriptionRequest;
    @Mock
    private SubscriptionRequestValidator subscriptionRequestValidator;
    @Mock
    private ReportingServiceImpl reportingServiceImpl;
    @Mock
    private AllInboxMessages allInboxMessages;
    @Mock
    private KilkariInboxService kilkariInboxService;

    @Before
    public void setUp() {
        initMocks(this);
        subscriptionService = new SubscriptionService(allSubscriptions, onMobileSubscriptionManagerPublisher, subscriptionRequestValidator, reportingServiceImpl, allInboxMessages, kilkariInboxService);
    }

    @Test
    public void shouldCreateNewSubscription() {
        String msisdn = "1234567890";
        Channel channel = Channel.IVR;
        SubscriptionPack subscriptionPack = SubscriptionPack.TWELVE_MONTHS;

        ArgumentCaptor<Subscription> subscriptionArgumentCaptor = ArgumentCaptor.forClass(Subscription.class);
        ArgumentCaptor<ProcessSubscriptionRequest> subscriptionActivationRequestArgumentCaptor = ArgumentCaptor.forClass(ProcessSubscriptionRequest.class);
        ArgumentCaptor<SubscriptionCreationReportRequest> subscriptionReportRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionCreationReportRequest.class);

        SubscriptionRequest subscriptionRequest = createSubscriptionRequest(msisdn, subscriptionPack.name(), channel.name());

        Subscription subscription = subscriptionService.createSubscription(subscriptionRequest);

        assertNotNull(subscription);

        verify(allSubscriptions).add(subscriptionArgumentCaptor.capture());
        verify(onMobileSubscriptionManagerPublisher).processActivation(subscriptionActivationRequestArgumentCaptor.capture());
        verify(reportingServiceImpl).reportSubscriptionCreation(subscriptionReportRequestArgumentCaptor.capture());

        Subscription subscriptionSaved = subscriptionArgumentCaptor.getValue();
        Assert.assertEquals(subscription, subscriptionSaved);

        assertEquals(msisdn, subscriptionSaved.getMsisdn());
        assertEquals(subscriptionPack, subscriptionSaved.getPack());

        ProcessSubscriptionRequest actualProcessSubscriptionRequest = subscriptionActivationRequestArgumentCaptor.getValue();
        assertEquals(msisdn, actualProcessSubscriptionRequest.getMsisdn());
        assertEquals(subscriptionPack, actualProcessSubscriptionRequest.getPack());
        assertEquals(channel, actualProcessSubscriptionRequest.getChannel());

        SubscriptionCreationReportRequest actualSubscriptionCreationReportRequest = subscriptionReportRequestArgumentCaptor.getValue();
        assertEquals(msisdn, actualSubscriptionCreationReportRequest.getMsisdn());
        assertEquals(subscriptionPack.name(), actualSubscriptionCreationReportRequest.getPack());
        assertEquals(channel.name(), actualSubscriptionCreationReportRequest.getChannel());
    }

    @Test
    public void shouldValidateSubscriptionRequestOnlyForIVR() {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withDefaults().withChannel(Channel.IVR.toString()).build();

        subscriptionService.createSubscription(subscriptionRequest);

        verify(subscriptionRequestValidator).validate(subscriptionRequest);
    }

    @Test
    public void shouldNotValidateSubscriptionRequest_ForChannelsOtherThanIVR() {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withDefaults().withChannel(Channel.CALL_CENTER.toString()).build();

        subscriptionService.createSubscription(subscriptionRequest);

        verify(subscriptionRequestValidator, never()).validate(subscriptionRequest);
    }

    @Test
    public void shouldGetSubscriptionsForAGivenMsisdn() {
        String msisdn = "1234567890";
        ArrayList<Subscription> subscriptionsToBeReturned = new ArrayList<>();
        subscriptionsToBeReturned.add(new Subscription(msisdn, SubscriptionPack.TWELVE_MONTHS, DateTime.now()));
        subscriptionsToBeReturned.add(new Subscription(msisdn, SubscriptionPack.FIFTEEN_MONTHS, DateTime.now()));
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
        Subscription mockedSubscription = mock(Subscription.class);

        when(mockedSubscription.getStatus()).thenReturn(status);
        when(mockedSubscription.getSubscriptionId()).thenReturn(subscriptionId);
        when(allSubscriptions.findBySubscriptionId(subscriptionId)).thenReturn(mockedSubscription);

        subscriptionService.activationRequested(subscriptionId);

        InOrder order = inOrder(allSubscriptions, mockedSubscription, reportingServiceImpl);
        order.verify(allSubscriptions).findBySubscriptionId(subscriptionId);
        order.verify(mockedSubscription).activationRequestSent();
        order.verify(allSubscriptions).update(mockedSubscription);
        ArgumentCaptor<SubscriptionStateChangeReportRequest> subscriptionStateChangeReportRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionStateChangeReportRequest.class);
        order.verify(reportingServiceImpl).reportSubscriptionStateChange(subscriptionStateChangeReportRequestArgumentCaptor.capture());
        SubscriptionStateChangeReportRequest subscriptionStateChangeReportRequest = subscriptionStateChangeReportRequestArgumentCaptor.getValue();

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
        Subscription mockedSubscription = mock(Subscription.class);

        when(mockedSubscription.getStatus()).thenReturn(status);
        when(mockedSubscription.getSubscriptionId()).thenReturn(subscriptionId);
        when(allSubscriptions.findBySubscriptionId(subscriptionId)).thenReturn(mockedSubscription);

        subscriptionService.deactivationRequested(subscriptionId);

        InOrder order = inOrder(allSubscriptions, mockedSubscription, reportingServiceImpl);
        order.verify(allSubscriptions).findBySubscriptionId(subscriptionId);
        order.verify(mockedSubscription).deactivationRequestSent();
        order.verify(allSubscriptions).update(mockedSubscription);
        ArgumentCaptor<SubscriptionStateChangeReportRequest> subscriptionStateChangeReportRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionStateChangeReportRequest.class);
        order.verify(reportingServiceImpl).reportSubscriptionStateChange(subscriptionStateChangeReportRequestArgumentCaptor.capture());
        SubscriptionStateChangeReportRequest subscriptionStateChangeReportRequest = subscriptionStateChangeReportRequestArgumentCaptor.getValue();

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
        ArgumentCaptor<SubscriptionStateChangeReportRequest> subscriptionStateChangeReportRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionStateChangeReportRequest.class);
        order.verify(reportingServiceImpl).reportSubscriptionStateChange(subscriptionStateChangeReportRequestArgumentCaptor.capture());
        SubscriptionStateChangeReportRequest subscriptionStateChangeReportRequest = subscriptionStateChangeReportRequestArgumentCaptor.getValue();

        assertEquals(subscriptionId, subscriptionStateChangeReportRequest.getSubscriptionId());
        assertEquals(status.name(), subscriptionStateChangeReportRequest.getSubscriptionStatus());
        assertEquals(reason, subscriptionStateChangeReportRequest.getReason());
        assertEquals(operator, subscriptionStateChangeReportRequest.getOperator());
    }

    @Test
    public void shouldActivateTheSubscriptionGivenTheSubscriptionId() {
        Subscription mockedSubscription = mock(Subscription.class);
        String subscriptionId = "abcd1234";
        SubscriptionStatus subscriptionStatus = SubscriptionStatus.ACTIVE;
        String operator = Operator.AIRTEL.name();

        when(mockedSubscription.getStatus()).thenReturn(subscriptionStatus);
        when(mockedSubscription.getSubscriptionId()).thenReturn(subscriptionId);
        when(allSubscriptions.findBySubscriptionId(subscriptionId)).thenReturn(mockedSubscription);

        subscriptionService.activate(subscriptionId, DateTime.now(), operator);

        InOrder order = inOrder(allSubscriptions, mockedSubscription, reportingServiceImpl);
        order.verify(allSubscriptions).findBySubscriptionId(subscriptionId);
        order.verify(mockedSubscription).activate(operator);
        order.verify(allSubscriptions).update(mockedSubscription);
        ArgumentCaptor<SubscriptionStateChangeReportRequest> subscriptionStateChangeReportRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionStateChangeReportRequest.class);
        order.verify(reportingServiceImpl).reportSubscriptionStateChange(subscriptionStateChangeReportRequestArgumentCaptor.capture());
        SubscriptionStateChangeReportRequest subscriptionStateChangeReportRequest = subscriptionStateChangeReportRequestArgumentCaptor.getValue();

        assertEquals(subscriptionId, subscriptionStateChangeReportRequest.getSubscriptionId());
        assertEquals(subscriptionStatus.name(), subscriptionStateChangeReportRequest.getSubscriptionStatus());
        assertEquals(operator, subscriptionStateChangeReportRequest.getOperator());
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
        Subscription subscription = new Subscription() {
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
        Subscription subscription = new Subscription() {
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
        Subscription subscription = new Subscription(msisdn, SubscriptionPack.SEVEN_MONTHS, DateTime.now());
        when(allSubscriptions.findBySubscriptionId(subscriptionId)).thenReturn(subscription);
        Channel channel = Channel.IVR;

        subscriptionService.requestDeactivation(new DeactivationRequest(subscriptionId, channel, DateTime.now()));

        ArgumentCaptor<Subscription> captor = ArgumentCaptor.forClass(Subscription.class);
        ArgumentCaptor<ProcessSubscriptionRequest> captor1 = ArgumentCaptor.forClass(ProcessSubscriptionRequest.class);
        verify(allSubscriptions).update(captor.capture());
        Subscription actualSubscription = captor.getValue();
        Assert.assertEquals(msisdn, actualSubscription.getMsisdn());
        Assert.assertEquals(SubscriptionStatus.DEACTIVATION_REQUEST_RECEIVED, actualSubscription.getStatus());

        verify(onMobileSubscriptionManagerPublisher).processDeactivation(captor1.capture());
        ProcessSubscriptionRequest actualProcessSubscriptionRequest = captor1.getValue();
        assertEquals(msisdn, actualProcessSubscriptionRequest.getMsisdn());
        assertEquals(channel, actualProcessSubscriptionRequest.getChannel());
        assertEquals(subscription.getSubscriptionId(), actualProcessSubscriptionRequest.getSubscriptionId());
    }

    @Test
    public void shouldProcessSubscriptionCompletion() {
        String msisdn = "1234567890";
        Subscription subscription = new Subscription(msisdn, SubscriptionPack.SEVEN_MONTHS, DateTime.now());
        String subscriptionId = subscription.getSubscriptionId();
        when(allSubscriptions.findBySubscriptionId(subscriptionId)).thenReturn(subscription);

        subscriptionService.subscriptionComplete(subscriptionId);

        ArgumentCaptor<Subscription> subscriptionArgumentCaptor = ArgumentCaptor.forClass(Subscription.class);
        verify(allSubscriptions).update(subscriptionArgumentCaptor.capture());
        Subscription actualSubscription = subscriptionArgumentCaptor.getValue();
        Assert.assertEquals(msisdn, actualSubscription.getMsisdn());
        Assert.assertEquals(SubscriptionStatus.PENDING_COMPLETION, actualSubscription.getStatus());

        ArgumentCaptor<SubscriptionStateChangeReportRequest> subscriptionStateChangeReportRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionStateChangeReportRequest.class);
        verify(reportingServiceImpl).reportSubscriptionStateChange(subscriptionStateChangeReportRequestArgumentCaptor.capture());
        SubscriptionStateChangeReportRequest subscriptionStateChangeReportRequest = subscriptionStateChangeReportRequestArgumentCaptor.getValue();
        assertEquals(subscriptionId, subscriptionStateChangeReportRequest.getSubscriptionId());
        assertEquals(SubscriptionStatus.PENDING_COMPLETION.name(), subscriptionStateChangeReportRequest.getSubscriptionStatus());
        assertEquals("Subscription completed", subscriptionStateChangeReportRequest.getReason());
    }

    @Test
    public void shouldScheduleInboxDeletionUponSubscriptionCompletion() {
        Subscription subscription = new Subscription("1234567890", SubscriptionPack.SEVEN_MONTHS, DateTime.now());
        String subscriptionId = subscription.getSubscriptionId();
        when(allSubscriptions.findBySubscriptionId(subscriptionId)).thenReturn(subscription);

        subscriptionService.subscriptionComplete(subscriptionId);
        
        verify(kilkariInboxService).scheduleInboxDeletion(subscription);
    }

    @Test
    public void shouldScheduleInboxDeletionUponSubscriptionDeactivation() {
        Subscription subscription = new Subscription("1234567890", SubscriptionPack.SEVEN_MONTHS, DateTime.now());
        String subscriptionId = subscription.getSubscriptionId();
        when(allSubscriptions.findBySubscriptionId(subscriptionId)).thenReturn(subscription);

        subscriptionService.deactivateSubscription(subscriptionId, DateTime.now(), null, null);

        verify(kilkariInboxService).scheduleInboxDeletion(subscription);
    }

    @Test
    public void shouldScheduleInboxDeletionWhenDeactivationOfSubscriptionWasRequested() {
        Subscription subscription = new Subscription("1234567890", SubscriptionPack.SEVEN_MONTHS, DateTime.now());
        String subscriptionId = subscription.getSubscriptionId();
        when(allSubscriptions.findBySubscriptionId(subscriptionId)).thenReturn(subscription);

        subscriptionService.deactivationRequested(subscriptionId);

        verify(kilkariInboxService).scheduleInboxDeletion(subscription);
    }

    private SubscriptionRequest createSubscriptionRequest(String msisdn, String pack, String channel) {
        return new SubscriptionRequestBuilder().withDefaults().withMsisdn(msisdn).withPack(pack).withChannel(channel).build();
    }
}

