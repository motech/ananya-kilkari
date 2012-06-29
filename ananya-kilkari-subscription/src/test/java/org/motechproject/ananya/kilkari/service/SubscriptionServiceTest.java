package org.motechproject.ananya.kilkari.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.builder.SubscriptionRequestBuilder;
import org.motechproject.ananya.kilkari.domain.*;
import org.motechproject.ananya.kilkari.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.repository.AllSubscriptions;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class SubscriptionServiceTest {

    private SubscriptionService subscriptionService;

    @Mock
    private AllSubscriptions allSubscriptions;
    @Mock
    private Publisher publisher;
    @Mock
    private SubscriptionRequest mockedSubscriptionRequest;

    @Before
    public void setUp() {
        initMocks(this);
        subscriptionService = new SubscriptionService(allSubscriptions, publisher);
    }

    @Test
    public void shouldCreateNewSubscription() {
        String msisdn = "1234567890";
        Channel channel = Channel.IVR;
        SubscriptionPack subscriptionPack = SubscriptionPack.TWELVE_MONTHS;

        ArgumentCaptor<Subscription> subscriptionArgumentCaptor = ArgumentCaptor.forClass(Subscription.class);
        ArgumentCaptor<SubscriptionActivationRequest> subscriptionActivationRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionActivationRequest.class);
        ArgumentCaptor<SubscriptionCreationReportRequest> subscriptionReportRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionCreationReportRequest.class);

        SubscriptionRequest subscriptionRequest = createSubscriptionRequest(msisdn, subscriptionPack.name(), channel.name());
        subscriptionService.createSubscription(subscriptionRequest);

        verify(allSubscriptions).add(subscriptionArgumentCaptor.capture());
        verify(publisher).processSubscription(subscriptionActivationRequestArgumentCaptor.capture());
        verify(publisher).reportSubscriptionCreation(subscriptionReportRequestArgumentCaptor.capture());

        Subscription subscription = subscriptionArgumentCaptor.getValue();
        assertEquals(msisdn, subscription.getMsisdn());
        assertEquals(subscriptionPack, subscription.getPack());

        SubscriptionActivationRequest actualSubscriptionActivationRequest = subscriptionActivationRequestArgumentCaptor.getValue();
        assertEquals(msisdn, actualSubscriptionActivationRequest.getMsisdn());
        assertEquals(subscriptionPack, actualSubscriptionActivationRequest.getPack());
        assertEquals(channel, actualSubscriptionActivationRequest.getChannel());

        SubscriptionCreationReportRequest actualSubscriptionCreationReportRequest = subscriptionReportRequestArgumentCaptor.getValue();
        assertEquals(msisdn, actualSubscriptionCreationReportRequest.getMsisdn());
        assertEquals(subscriptionPack.name(), actualSubscriptionCreationReportRequest.getPack());
        assertEquals(channel.name(), actualSubscriptionCreationReportRequest.getChannel());
    }

    @Test(expected = ValidationException.class)
    public void shouldThrowExceptionWhenInvalidSubscriptionRequestIsGiven() {
        doThrow(new ValidationException("Invalid Request")).when(mockedSubscriptionRequest).validate();
        subscriptionService.createSubscription(mockedSubscriptionRequest);
    }

    @Test
    public void shouldGetSubscriptionsForAGivenMsisdn()  {
        String msisdn = "1234567890";
        ArrayList<Subscription> subscriptionsToBeReturned = new ArrayList<>();
        subscriptionsToBeReturned.add(new Subscription(msisdn, SubscriptionPack.TWELVE_MONTHS));
        subscriptionsToBeReturned.add(new Subscription(msisdn, SubscriptionPack.FIFTEEN_MONTHS));
        when(allSubscriptions.findByMsisdn(msisdn)).thenReturn(subscriptionsToBeReturned);

        List<Subscription> subscriptions = subscriptionService.findByMsisdn(msisdn);

        assertEquals(2, subscriptions.size());
        assertEquals(msisdn, subscriptions.get(0).getMsisdn());
        assertEquals(SubscriptionPack.TWELVE_MONTHS, subscriptions.get(0).getPack());
        assertEquals(msisdn, subscriptions.get(1).getMsisdn());
        assertEquals(SubscriptionPack.FIFTEEN_MONTHS, subscriptions.get(1).getPack());
    }

    @Test(expected = ValidationException.class)
    public void shouldThrowAnExceptionForInvalidMsisdnNumbers()  {
        subscriptionService.findByMsisdn("12345");
    }

    @Test(expected = ValidationException.class)
    public void shouldThrowAnExceptionForNonNumericMsisdn()  {
        subscriptionService.findByMsisdn("123456789a");
    }

    @Test
    public void shouldFindTheGivenSubscriptionByMsisdnAndPack() {
        String pack = "twelve_months";
        String msisdn = "123456890";
        Subscription subscription = new Subscription(msisdn, SubscriptionPack.TWELVE_MONTHS);
        when(allSubscriptions.findByMsisdnAndPack(msisdn, SubscriptionPack.TWELVE_MONTHS)).thenReturn(subscription);

        Subscription actualSubscription = subscriptionService.findByMsisdnAndPack(msisdn, pack);

        verify(allSubscriptions).findByMsisdnAndPack(msisdn, SubscriptionPack.TWELVE_MONTHS);
        assertEquals(subscription, actualSubscription);
    }

    @Test
    public void shouldUpdateTheSubscriptionStatusGivenTheMsisdnAndPack() {
        SubscriptionPack pack = SubscriptionPack.TWELVE_MONTHS;
        String msisdn = "123456890";
        String subscriptionId = "abcd1234";
        SubscriptionStatus status = SubscriptionStatus.ACTIVE;
        Subscription mockedSubscription = mock(Subscription.class);

        when(mockedSubscription.getStatus()).thenReturn(status);
        when(mockedSubscription.getSubscriptionId()).thenReturn(subscriptionId);
        when(allSubscriptions.findByMsisdnAndPack(msisdn, pack)).thenReturn(mockedSubscription);

        subscriptionService.updateSubscriptionStatus(msisdn, pack.name(), status);

        InOrder order = inOrder(allSubscriptions, mockedSubscription, publisher);
        order.verify(allSubscriptions).findByMsisdnAndPack(msisdn, pack);
        order.verify(mockedSubscription).setStatus(SubscriptionStatus.ACTIVE);
        order.verify(allSubscriptions).update(mockedSubscription);
        ArgumentCaptor<SubscriptionStateChangeReportRequest> subscriptionStateChangeReportRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionStateChangeReportRequest.class);
        order.verify(publisher).reportSubscriptionStateChange(subscriptionStateChangeReportRequestArgumentCaptor.capture());
        SubscriptionStateChangeReportRequest subscriptionStateChangeReportRequest = subscriptionStateChangeReportRequestArgumentCaptor.getValue();

        assertEquals(subscriptionId, subscriptionStateChangeReportRequest.getSubscriptionId());
        assertEquals(status.name(), subscriptionStateChangeReportRequest.getSubscriptionStatus());
    }

    @Test
    public void shouldUpdateTheSubscriptionStatusGivenTheSubscriptionId() {
        SubscriptionStatus status = SubscriptionStatus.ACTIVE;
        Subscription mockedSubscription = mock(Subscription.class);
        String subscriptionId = "abcd1234";

        when(mockedSubscription.getStatus()).thenReturn(status);
        when(mockedSubscription.getSubscriptionId()).thenReturn(subscriptionId);
        when(allSubscriptions.findBySubscriptionId(subscriptionId)).thenReturn(mockedSubscription);

        subscriptionService.updateSubscriptionStatus(subscriptionId, status);

        InOrder order = inOrder(allSubscriptions, mockedSubscription, publisher);
        order.verify(allSubscriptions).findBySubscriptionId(subscriptionId);
        order.verify(mockedSubscription).setStatus(SubscriptionStatus.ACTIVE);
        order.verify(allSubscriptions).update(mockedSubscription);
        ArgumentCaptor<SubscriptionStateChangeReportRequest> subscriptionStateChangeReportRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionStateChangeReportRequest.class);
        order.verify(publisher).reportSubscriptionStateChange(subscriptionStateChangeReportRequestArgumentCaptor.capture());
        SubscriptionStateChangeReportRequest subscriptionStateChangeReportRequest = subscriptionStateChangeReportRequestArgumentCaptor.getValue();

        assertEquals(subscriptionId, subscriptionStateChangeReportRequest.getSubscriptionId());
        assertEquals(status.name(), subscriptionStateChangeReportRequest.getSubscriptionStatus());
    }

    @Test
    public void shouldActivateTheSubscriptionGivenTheSubscriptionId() {
        Subscription mockedSubscription = mock(Subscription.class);
        String subscriptionId = "abcd1234";
        SubscriptionStatus subscriptionStatus = SubscriptionStatus.ACTIVE;

        when(mockedSubscription.getStatus()).thenReturn(subscriptionStatus);
        when(mockedSubscription.getSubscriptionId()).thenReturn(subscriptionId);
        when(allSubscriptions.findBySubscriptionId(subscriptionId)).thenReturn(mockedSubscription);

        subscriptionService.activate(subscriptionId);

        InOrder order = inOrder(allSubscriptions, mockedSubscription, publisher);
        order.verify(allSubscriptions).findBySubscriptionId(subscriptionId);
        order.verify(mockedSubscription).setStatus(subscriptionStatus);
        order.verify(allSubscriptions).update(mockedSubscription);
        ArgumentCaptor<SubscriptionStateChangeReportRequest> subscriptionStateChangeReportRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionStateChangeReportRequest.class);
        order.verify(publisher).reportSubscriptionStateChange(subscriptionStateChangeReportRequestArgumentCaptor.capture());
        SubscriptionStateChangeReportRequest subscriptionStateChangeReportRequest = subscriptionStateChangeReportRequestArgumentCaptor.getValue();

        assertEquals(subscriptionId, subscriptionStateChangeReportRequest.getSubscriptionId());
        assertEquals(subscriptionStatus.name(), subscriptionStateChangeReportRequest.getSubscriptionStatus());
    }

    private SubscriptionRequest createSubscriptionRequest(String msisdn, String pack, String channel) {
        return new SubscriptionRequestBuilder().withDefaults().withMsisdn(msisdn).withPack(pack).withChannel(channel).build();
    }
}
