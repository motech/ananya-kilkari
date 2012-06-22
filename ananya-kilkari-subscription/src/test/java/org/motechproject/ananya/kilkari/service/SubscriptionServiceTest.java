package org.motechproject.ananya.kilkari.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.domain.Channel;
import org.motechproject.ananya.kilkari.domain.Subscription;
import org.motechproject.ananya.kilkari.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.domain.SubscriptionRequest;
import org.motechproject.ananya.kilkari.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.handlers.ProcessSubscriptionHandler;
import org.motechproject.ananya.kilkari.repository.AllSubscriptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
        String channel = "ivr";
        SubscriptionPack subscriptionPack = SubscriptionPack.TWELVE_MONTHS;

        ArgumentCaptor<Subscription> subscriptionArgumentCaptor = ArgumentCaptor.forClass(Subscription.class);
        ArgumentCaptor<SubscriptionRequest> subscriptionRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionRequest.class);

        SubscriptionRequest subscriptionRequest = new SubscriptionRequest(msisdn, subscriptionPack.name(), channel);
        subscriptionService.createSubscription(subscriptionRequest);

        verify(allSubscriptions).add(subscriptionArgumentCaptor.capture());
        verify(publisher).processSubscription(subscriptionRequestArgumentCaptor.capture());

        Subscription subscription = subscriptionArgumentCaptor.getValue();
        assertEquals(msisdn, subscription.getMsisdn());
        assertEquals(subscriptionPack, subscription.getPack());

        SubscriptionRequest actualSubscriptionRequest = subscriptionRequestArgumentCaptor.getValue();
        assertEquals(msisdn, actualSubscriptionRequest.getMsisdn());
        assertEquals(subscriptionPack.name(), actualSubscriptionRequest.getPack());
        assertEquals(channel, actualSubscriptionRequest.getChannel());
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
}
