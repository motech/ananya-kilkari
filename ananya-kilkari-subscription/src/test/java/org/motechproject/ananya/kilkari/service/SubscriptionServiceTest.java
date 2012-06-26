package org.motechproject.ananya.kilkari.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
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
    public void shouldCreateNewSubscription() throws ValidationException {
        String msisdn = "1234567890";
        Channel channel = Channel.IVR;
        SubscriptionPack subscriptionPack = SubscriptionPack.TWELVE_MONTHS;

        ArgumentCaptor<Subscription> subscriptionArgumentCaptor = ArgumentCaptor.forClass(Subscription.class);
        ArgumentCaptor<SubscriptionActivationRequest> subscriptionActivationRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionActivationRequest.class);

        SubscriptionRequest subscriptionRequest = new SubscriptionRequest(msisdn, subscriptionPack.name(), channel.name());
        subscriptionService.createSubscription(subscriptionRequest);

        verify(allSubscriptions).add(subscriptionArgumentCaptor.capture());
        verify(publisher).processSubscription(subscriptionActivationRequestArgumentCaptor.capture());

        Subscription subscription = subscriptionArgumentCaptor.getValue();
        assertEquals(msisdn, subscription.getMsisdn());
        assertEquals(subscriptionPack, subscription.getPack());

        SubscriptionActivationRequest actualSubscriptionActivationRequest = subscriptionActivationRequestArgumentCaptor.getValue();
        assertEquals(msisdn, actualSubscriptionActivationRequest.getMsisdn());
        assertEquals(subscriptionPack, actualSubscriptionActivationRequest.getPack());
        assertEquals(channel, actualSubscriptionActivationRequest.getChannel());
    }

    @Test(expected = ValidationException.class)
    public void shouldThrowExceptionWhenInvalidSubscriptionRequestIsGiven() throws ValidationException {
        doThrow(new ValidationException("Invalid Request")).when(mockedSubscriptionRequest).validate();
        subscriptionService.createSubscription(mockedSubscriptionRequest);
    }

    @Test
    public void shouldGetSubscriptionsForAGivenMsisdn() throws ValidationException {
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
    public void shouldThrowAnExceptionForInvalidMsisdnNumbers() throws ValidationException {
        subscriptionService.findByMsisdn("12345");
    }

    @Test(expected = ValidationException.class)
    public void shouldThrowAnExceptionForNonNumericMsisdn() throws ValidationException {
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
    public void shouldUpdateTheSubscriptionDetails() {
        Subscription subscription = new Subscription("123456890", SubscriptionPack.TWELVE_MONTHS);

        subscriptionService.update(subscription);

        verify(allSubscriptions).update(subscription);
    }

    @Test
    public void shouldUpdateTheSubscriptionStatusGivenTheMsisdnAndPack() {
        SubscriptionPack pack = SubscriptionPack.TWELVE_MONTHS;
        String msisdn = "123456890";
        SubscriptionStatus status = SubscriptionStatus.ACTIVE;
        Subscription mockedSubscription = mock(Subscription.class);

        when(allSubscriptions.findByMsisdnAndPack(msisdn, pack)).thenReturn(mockedSubscription);

        subscriptionService.updateSubscriptionStatus(msisdn, pack.name(), status);

        InOrder order = inOrder(allSubscriptions, mockedSubscription);
        order.verify(allSubscriptions).findByMsisdnAndPack(msisdn, pack);
        order.verify(mockedSubscription).setStatus(SubscriptionStatus.ACTIVE);
        order.verify(allSubscriptions).update(mockedSubscription);
    }

    @Test
    public void shouldUpdateTheSubscriptionStatusGivenTheSubscriptionId() {
        SubscriptionStatus status = SubscriptionStatus.ACTIVE;
        Subscription mockedSubscription = mock(Subscription.class);
        String subscriptionId = "abcd1234";

        when(allSubscriptions.findBySubscriptionId(subscriptionId)).thenReturn(mockedSubscription);

        subscriptionService.updateSubscriptionStatus(subscriptionId, status);

        InOrder order = inOrder(allSubscriptions, mockedSubscription);
        order.verify(allSubscriptions).findBySubscriptionId(subscriptionId);
        order.verify(mockedSubscription).setStatus(SubscriptionStatus.ACTIVE);
        order.verify(allSubscriptions).update(mockedSubscription);
    }
}
