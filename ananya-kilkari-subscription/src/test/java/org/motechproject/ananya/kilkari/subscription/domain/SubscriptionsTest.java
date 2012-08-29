package org.motechproject.ananya.kilkari.subscription.domain;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.kilkari.subscription.builder.SubscriptionBuilder;

import java.util.List;

import static org.junit.Assert.*;

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

    @Test
    public void shouldReturnUpdatableSubscriptions() {
        subscriptions.add(new SubscriptionBuilder().withDefaults().withStatus(SubscriptionStatus.SUSPENDED).build());

        List<Subscription> subscriptions = this.subscriptions.updatableSubscriptions();

        assertEquals(1, subscriptions.size());
    }

    @Test
    public void shouldReturnAnEmptyListIfNoUpdatableSubscriptionsPresent() {
        subscriptions.add(new SubscriptionBuilder().withDefaults().withStatus(SubscriptionStatus.PENDING_ACTIVATION).build());

        List<Subscription> subscriptions = this.subscriptions.updatableSubscriptions();

        assertEquals(0, subscriptions.size());
    }
}
