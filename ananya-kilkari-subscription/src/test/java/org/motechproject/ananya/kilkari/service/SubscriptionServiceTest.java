package org.motechproject.ananya.kilkari.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.domain.Subscription;
import org.motechproject.ananya.kilkari.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.repository.AllSubscriptions;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SubscriptionServiceTest {

    private SubscriptionService subscriptionService;

    @Mock
    private AllSubscriptions allSubscriptions;

    @Before
    public void setUp() {
        initMocks(this);
        subscriptionService = new SubscriptionService(allSubscriptions);
    }

    @Test
    public void shouldCreateNewSubscription() throws ValidationException {
        String msisdn = "1234567890";
        subscriptionService.createSubscription(msisdn, "TWELVE_MONTHS");

        ArgumentCaptor<Subscription> subscriptionArgumentCaptor = ArgumentCaptor.forClass(Subscription.class);
        verify(allSubscriptions).add(subscriptionArgumentCaptor.capture());

        Subscription subscription = subscriptionArgumentCaptor.getValue();
        assertEquals(msisdn, subscription.getMsisdn());
        assertEquals(SubscriptionPack.TWELVE_MONTHS, subscription.getPack());
    }

    @Test(expected = ValidationException.class)
    public void shouldThrowExceptionWhenInvalidSubscriptionPackIsGivenToCreateNewSubscription() throws ValidationException {
        String msisdn = "1234567890";
        subscriptionService.createSubscription(msisdn, "INVALID_PACK");
    }

    @Test(expected = ValidationException.class)
    public void shouldThrowExceptionWhenInvalidMsisdnNumberIsGivenToCreateNewSubscription() throws ValidationException {
        String msisdn = "12345";
        subscriptionService.createSubscription(msisdn, "TWELVE_MONTHS");
    }

    @Test(expected = ValidationException.class)
    public void shouldThrowExceptionWhenNonNumericMsisdnNumberIsGivenToCreateNewSubscription() throws ValidationException {
        String msisdn = "123456789a";
        subscriptionService.createSubscription(msisdn, "TWELVE_MONTHS");
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
