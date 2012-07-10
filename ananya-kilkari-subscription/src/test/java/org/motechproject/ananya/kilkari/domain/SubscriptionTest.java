package org.motechproject.ananya.kilkari.domain;

import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.*;

public class SubscriptionTest {

    @Test
    public void shouldInitializeSubscription() {
        DateTime beforeCreation = DateTime.now();
        Subscription subscription = new Subscription("mymsisdn", SubscriptionPack.FIFTEEN_MONTHS, DateTime.now());
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
        Subscription subscription = new Subscription("mymsisnd", SubscriptionPack.FIFTEEN_MONTHS, DateTime.now());
        subscription.activationRequested();

        assertEquals(SubscriptionStatus.PENDING_ACTIVATION, subscription.getStatus());
        assertNull(subscription.getOperator());
    }

    @Test
    public void shouldChangeStatusOfSubscriptionToActiveForSuccessfulActivation() {
        Subscription subscription = new Subscription("mymsisnd", SubscriptionPack.FIFTEEN_MONTHS, DateTime.now());
        String operator = Operator.AIRTEL.name();
        subscription.activate(operator);

        assertEquals(SubscriptionStatus.ACTIVE, subscription.getStatus());
        assertEquals(operator, subscription.getOperator());
    }

    @Test
    public void shouldChangeStatusOfSubscriptionToActivationFailedForUnsuccessfulActivation() {
        Subscription subscription = new Subscription("mymsisnd", SubscriptionPack.FIFTEEN_MONTHS, DateTime.now());
        String operator = Operator.AIRTEL.name();
        subscription.activationFailed(operator);

        assertEquals(SubscriptionStatus.ACTIVATION_FAILED, subscription.getStatus());
        assertEquals(operator, subscription.getOperator());
    }

    @Test
    public void shouldChangeStatusOfSubscriptionToActivatedAndUpdateRenewalDate() {
        Subscription subscription = new Subscription("mymsisnd", SubscriptionPack.FIFTEEN_MONTHS, DateTime.now());
        subscription.activateOnRenewal();

        assertEquals(SubscriptionStatus.ACTIVE, subscription.getStatus());
    }

    @Test
    public void shouldChangeStatusOfSubscriptionSuspendedAndUpdateRenewalDate() {
        Subscription subscription = new Subscription("mymsisnd", SubscriptionPack.FIFTEEN_MONTHS, DateTime.now());
        subscription.suspendOnRenewal();

        assertEquals(SubscriptionStatus.SUSPENDED, subscription.getStatus());
    }

    @Test
    public void shouldChangeStatusOfSubscriptionDeactivatedWithDeactivatedDate() {
        Subscription subscription = new Subscription("mymsisnd", SubscriptionPack.FIFTEEN_MONTHS, DateTime.now());
        subscription.deactivate();

        assertEquals(SubscriptionStatus.DEACTIVATED, subscription.getStatus());
    }

    @Test
    public void shouldReturnIsActiveBasedOnStatus() {
        String msisdn = "9876534211";
        SubscriptionPack pack = SubscriptionPack.TWELVE_MONTHS;
        Subscription subscription = new Subscription(msisdn, pack, DateTime.now());

        subscription.setStatus(SubscriptionStatus.ACTIVE);
        assertTrue(subscription.isActive());

        subscription.setStatus(SubscriptionStatus.COMPLETED);
        assertFalse(subscription.isActive());

        subscription.setStatus(SubscriptionStatus.NEW);
        assertTrue(subscription.isActive());

        subscription.setStatus(SubscriptionStatus.PENDING_ACTIVATION);
        assertTrue(subscription.isActive());

        subscription.setStatus(SubscriptionStatus.DEACTIVATED);
        assertFalse(subscription.isActive());
    }
}