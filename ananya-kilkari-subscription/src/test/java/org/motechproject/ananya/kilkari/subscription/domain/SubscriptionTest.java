package org.motechproject.ananya.kilkari.subscription.domain;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ananya.kilkari.subscription.builder.SubscriptionBuilder;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class SubscriptionTest {

    @Test
    public void shouldInitializeSubscription() {
        DateTime beforeCreation = DateTime.now();
        String msisdn = "1234567890";
        Subscription subscription = new Subscription(msisdn, SubscriptionPack.FIFTEEN_MONTHS, DateTime.now());
        DateTime afterCreation = DateTime.now();

        assertEquals(SubscriptionStatus.NEW, subscription.getStatus());
        assertEquals(msisdn, subscription.getMsisdn());
        assertEquals(SubscriptionPack.FIFTEEN_MONTHS, subscription.getPack());
        assertNotNull(subscription.getSubscriptionId());

        DateTime creationDate = subscription.getCreationDate();
        assertTrue(creationDate.isEqual(beforeCreation) || creationDate.isAfter(beforeCreation));
        assertTrue(creationDate.isEqual(afterCreation) || creationDate.isBefore(afterCreation));
    }


    @Test
    public void shouldChangeStatusOfSubscriptionToPendingDuringActivationRequest() {
        Subscription subscription = new Subscription("1234567890", SubscriptionPack.FIFTEEN_MONTHS, DateTime.now());
        subscription.activationRequestSent();

        assertEquals(SubscriptionStatus.PENDING_ACTIVATION, subscription.getStatus());
        assertNull(subscription.getOperator());
    }

    @Test
    public void shouldChangeStatusOfSubscriptionToActiveForSuccessfulActivation() {
        DateTime createdAt = DateTime.now();
        DateTime activatedOn = createdAt.plus(5000);
        DateTime startDate = DateTime.now().minusDays(23);
        Subscription subscription = new Subscription("1234567890", SubscriptionPack.FIFTEEN_MONTHS, createdAt);
        subscription.setStartDate(startDate);
        Operator operator = Operator.AIRTEL;

        subscription.activate(operator.name(), activatedOn);

        assertEquals(SubscriptionStatus.ACTIVE, subscription.getStatus());
        assertEquals(operator, subscription.getOperator());
        assertEquals(startDate.plus(5000), subscription.getStartDate());
    }

    @Test
    public void shouldChangeStatusOfSubscriptionToActivationFailedForUnsuccessfulActivation() {
        Subscription subscription = new Subscription("1234567890", SubscriptionPack.FIFTEEN_MONTHS, DateTime.now());
        Operator operator = Operator.AIRTEL;
        subscription.activationFailed(operator.name());

        assertEquals(SubscriptionStatus.ACTIVATION_FAILED, subscription.getStatus());
        assertEquals(operator, subscription.getOperator());
    }

    @Test
    public void shouldChangeStatusOfSubscriptionToActivatedAndUpdateRenewalDate() {
        Subscription subscription = new Subscription("1234567890", SubscriptionPack.FIFTEEN_MONTHS, DateTime.now());
        subscription.activateOnRenewal();

        assertEquals(SubscriptionStatus.ACTIVE, subscription.getStatus());
    }

    @Test
    public void shouldChangeStatusOfSubscriptionSuspendedAndUpdateRenewalDate() {
        Subscription subscription = new Subscription("1234567890", SubscriptionPack.FIFTEEN_MONTHS, DateTime.now());
        subscription.suspendOnRenewal();

        assertEquals(SubscriptionStatus.SUSPENDED, subscription.getStatus());
    }

    @Test
    public void shouldChangeStatusToDeactivatedOnDeactivationOnlyIfPriorStatusIsNotPendingCompleted() {
        Subscription subscription = new Subscription("1234567890", SubscriptionPack.FIFTEEN_MONTHS, DateTime.now());
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.deactivate();

        assertEquals(SubscriptionStatus.DEACTIVATED, subscription.getStatus());
    }
    
    @Test
    public void shouldChangeStatusToCompletedOnDeactivationOnlyIfPriorStatusIsPendingCompleted() {
        Subscription subscription = new Subscription("1234567890", SubscriptionPack.FIFTEEN_MONTHS, DateTime.now());
        subscription.setStatus(SubscriptionStatus.PENDING_COMPLETION);
        subscription.deactivate();

        assertEquals(SubscriptionStatus.COMPLETED, subscription.getStatus());
    }

    @Test
    public void shouldChangeStatusOfSubscriptionToPendingCompletion() {
        Subscription subscription = new Subscription("1234567890", SubscriptionPack.FIFTEEN_MONTHS, DateTime.now());
        subscription.complete();

        assertEquals(SubscriptionStatus.PENDING_COMPLETION, subscription.getStatus());
    }

    @Test
    public void shouldReturnIsActiveBasedOnStatus() {
        String msisdn = "9876534211";
        SubscriptionPack pack = SubscriptionPack.TWELVE_MONTHS;
        Subscription subscription = new Subscription(msisdn, pack, DateTime.now());

        subscription.setStatus(SubscriptionStatus.ACTIVE);
        assertTrue(subscription.isInProgress());

        subscription.setStatus(SubscriptionStatus.COMPLETED);
        assertFalse(subscription.isInProgress());

        subscription.setStatus(SubscriptionStatus.NEW);
        assertTrue(subscription.isInProgress());

        subscription.setStatus(SubscriptionStatus.PENDING_ACTIVATION);
        assertTrue(subscription.isInProgress());

        subscription.setStatus(SubscriptionStatus.DEACTIVATED);
        assertFalse(subscription.isInProgress());

        subscription.setStatus(SubscriptionStatus.PENDING_DEACTIVATION);
        assertFalse(subscription.isInProgress());

        subscription.setStatus(SubscriptionStatus.PENDING_COMPLETION);
        assertFalse(subscription.isInProgress());

        subscription.setStatus(SubscriptionStatus.ACTIVATION_FAILED);
        assertFalse(subscription.isInProgress());
    }

    @Test
    public void expiryDateShouldBeEndDateOfTheCurrentWeek() {
        DateTime startedDate = DateTime.now().minusDays(3);
        Subscription subscription = new SubscriptionBuilder().withDefaults().withStartDate(startedDate).build();

        DateTime expiryDate = subscription.getCurrentWeeksMessageExpiryDate();
        assertThat(expiryDate, is(subscription.getStartDate().plusWeeks(1)));
    }

    @Test
    public void shouldReturnFalseForIsActiveWhenTheStatusIsPendingActivation(){
        Subscription subscription = new SubscriptionBuilder().withDefaults().withStatus(SubscriptionStatus.PENDING_ACTIVATION).build();

        assertFalse(subscription.hasBeenActivated());
    }

    @Test
    public void shouldReturnFalseForIsActiveWhenTheStatusIsActivationFailed(){
        Subscription subscription = new SubscriptionBuilder().withDefaults().withStatus(SubscriptionStatus.ACTIVATION_FAILED).build();

        assertFalse(subscription.hasBeenActivated());
    }

    @Test
    public void shouldReturnTrueForIsActiveForAnyOtherStatus(){
        Subscription subscription = new SubscriptionBuilder().withDefaults().withStatus(SubscriptionStatus.ACTIVE).build();

        assertTrue(subscription.hasBeenActivated());
    }

    @Test
    public void shouldReturnTrueIfTheSubscriptionIsInAnyOfTheDeactivatedStates() {
        Subscription subscription = new SubscriptionBuilder().withDefaults().withStatus(SubscriptionStatus.PENDING_DEACTIVATION).build();
        assertTrue(subscription.isInDeactivatedState());

        subscription.setStatus(SubscriptionStatus.DEACTIVATED);
        assertTrue(subscription.isInDeactivatedState());

        subscription.setStatus(SubscriptionStatus.DEACTIVATION_REQUEST_RECEIVED);
        assertTrue(subscription.isInDeactivatedState());
    }

    @Test
    public void shouldReturnFalseIfTheSubscriptionIsNotInTheDeactivatedState() {
        Subscription subscription = new SubscriptionBuilder().withDefaults().withStatus(SubscriptionStatus.ACTIVATION_FAILED).build();
        assertFalse(subscription.isInDeactivatedState());

        subscription.setStatus(SubscriptionStatus.ACTIVE);
        assertFalse(subscription.isInDeactivatedState());

        subscription.setStatus(SubscriptionStatus.NEW);
        assertFalse(subscription.isInDeactivatedState());

        subscription.setStatus(SubscriptionStatus.PENDING_ACTIVATION);
        assertFalse(subscription.isInDeactivatedState());

        subscription.setStatus(SubscriptionStatus.COMPLETED);
        assertFalse(subscription.isInDeactivatedState());

        subscription.setStatus(SubscriptionStatus.PENDING_COMPLETION);
        assertFalse(subscription.isInDeactivatedState());
    }
}