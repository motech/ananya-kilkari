package org.motechproject.ananya.kilkari.domain;

import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SubscriptionTest {

    @Test
    public void shouldInitializeSubscription() {
        DateTime beforeCreation = DateTime.now();
        Subscription subscription = new Subscription("mymsisdn", SubscriptionPack.FIFTEEN_MONTHS);
        DateTime afterCreation = DateTime.now();

        assertEquals(SubscriptionStatus.NEW, subscription.getStatus());
        assertEquals("mymsisdn", subscription.getMsisdn());
        assertEquals(SubscriptionPack.FIFTEEN_MONTHS, subscription.getPack());
        assertNotNull(subscription.getSubscriptionId());

        DateTime creationDate = subscription.getCreationDate();
        assertTrue(creationDate.isEqual(beforeCreation) || creationDate.isAfter(beforeCreation));
        assertTrue(creationDate.isEqual(afterCreation) || creationDate.isBefore(afterCreation));
    }


    @Test
    public void shouldChangeStatusOfSubscriptionToPendingDuringActivationRequest() {
        Subscription subscription = new Subscription("mymsisnd", SubscriptionPack.FIFTEEN_MONTHS);
        subscription.activationRequested();

        assertEquals(SubscriptionStatus.PENDING_ACTIVATION, subscription.getStatus());
    }

    @Test
    public void shouldChangeStatusOfSubscriptionToActiveForSuccessfulActivation() {
        Subscription subscription = new Subscription("mymsisnd", SubscriptionPack.FIFTEEN_MONTHS);
        subscription.activationRequested();
        subscription.activate();

        assertEquals(SubscriptionStatus.ACTIVE, subscription.getStatus());
    }

    @Test
    public void shouldChangeStatusOfSubscriptionToActivationFailedForUnsuccessfulActivation() {
        Subscription subscription = new Subscription("mymsisnd", SubscriptionPack.FIFTEEN_MONTHS);
        subscription.activationRequested();
        subscription.activationFailed();

        assertEquals(SubscriptionStatus.ACTIVATION_FAILED, subscription.getStatus());
    }
}