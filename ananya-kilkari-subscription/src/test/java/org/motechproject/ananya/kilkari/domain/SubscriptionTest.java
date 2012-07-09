package org.motechproject.ananya.kilkari.domain;

import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

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
        assertNull(subscription.getOperator());
    }

    @Test
    public void shouldChangeStatusOfSubscriptionToActiveForSuccessfulActivation() {
        Subscription subscription = new Subscription("mymsisnd", SubscriptionPack.FIFTEEN_MONTHS);
        String operator = Operator.AIRTEL.name();
        subscription.activate(operator);

        assertEquals(SubscriptionStatus.ACTIVE, subscription.getStatus());
        assertEquals(operator, subscription.getOperator());
    }

    @Test
    public void shouldChangeStatusOfSubscriptionToActivationFailedForUnsuccessfulActivation() {
        Subscription subscription = new Subscription("mymsisnd", SubscriptionPack.FIFTEEN_MONTHS);
        String operator = Operator.AIRTEL.name();
        subscription.activationFailed(operator);

        assertEquals(SubscriptionStatus.ACTIVATION_FAILED, subscription.getStatus());
        assertEquals(operator, subscription.getOperator());
    }

    @Test
    public void shouldChangeStatusOfSubscriptionToActivatedAndUpdateRenewalDate() {
        DateTime renewedDate = DateTime.now();
        Subscription subscription = new Subscription("mymsisnd", SubscriptionPack.FIFTEEN_MONTHS);
        subscription.activateOnRenewal(renewedDate);

        assertEquals(SubscriptionStatus.ACTIVE, subscription.getStatus());
        assertEquals(renewedDate, subscription.getRenewalDate());
    }

    @Test
    public void shouldChangeStatusOfSubscriptionSuspendedAndUpdateRenewalDate() {
        DateTime renewedDate = DateTime.now();
        Subscription subscription = new Subscription("mymsisnd", SubscriptionPack.FIFTEEN_MONTHS);
        subscription.suspendOnRenewal(renewedDate);

        assertEquals(SubscriptionStatus.SUSPENDED, subscription.getStatus());
        assertEquals(renewedDate, subscription.getRenewalDate());
    }
}