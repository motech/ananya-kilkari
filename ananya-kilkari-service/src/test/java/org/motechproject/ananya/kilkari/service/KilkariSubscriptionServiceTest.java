package org.motechproject.ananya.kilkari.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.domain.SubscriptionRequest;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class KilkariSubscriptionServiceTest {
    private KilkariSubscriptionService kilkariSubscriptionService;
    @Mock
    private SubscriptionPublisher subscriptionPublisher;
    @Mock
    private SubscriptionService subscriptionService;

    @Before
    public void setup() {
        initMocks(this);
        kilkariSubscriptionService = new KilkariSubscriptionService(subscriptionPublisher, subscriptionService);
    }

    @Test
    public void shouldCreateSubscription() {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequest();
        kilkariSubscriptionService.createSubscription(subscriptionRequest);
        verify(subscriptionPublisher).createSubscription(subscriptionRequest);
    }

    @Test
    public void shouldGetSubscriptionsFor() {
        String msisdn = "998800";
        kilkariSubscriptionService.getSubscriptionsFor(msisdn);
        verify(subscriptionService).findByMsisdn(msisdn);
    }
}
