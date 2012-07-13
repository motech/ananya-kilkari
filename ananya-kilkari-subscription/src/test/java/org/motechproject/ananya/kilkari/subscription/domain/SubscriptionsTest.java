package org.motechproject.ananya.kilkari.subscription.domain;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.kilkari.subscription.builder.SubscriptionBuilder;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class SubscriptionsTest {
    private Subscriptions subscriptions;

    @Before
    public void setUp() {
        subscriptions = new Subscriptions();
    }

    @Test
    public void shouldReturnSubscriptionInProgress() {
        subscriptions.add(new SubscriptionBuilder().withDefaults().withStatus(SubscriptionStatus.ACTIVE).build());
        
        Subscription subscription = subscriptions.subscriptionInProgress();

        assertNotNull(subscription);
    }

    @Test
    public void shouldReturnNullIfNoSubscriptionIsInProgress() {
        Subscription subscription = subscriptions.subscriptionInProgress();
        assertNull(subscription);

        subscriptions.add(new SubscriptionBuilder().withDefaults().withStatus(SubscriptionStatus.COMPLETED).build());
        assertNull(subscription);
    }
}
