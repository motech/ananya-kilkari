package org.motechproject.ananya.kilkari.subscription.domain;

import org.joda.time.DateTime;
import org.joda.time.Weeks;
import org.junit.Test;
import org.motechproject.ananya.kilkari.subscription.builder.SubscriptionBuilder;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SubscriptionTest {

    @Test
    public void foo() {
        DateTime now = DateTime.now();
        DateTime twoDaysLater = now.plusDays(2);
        DateTime twoDaysEarlier = now.minusDays(2);
        DateTime nineDaysLater = now.plusDays(9);
        DateTime nineDaysEarlier = now.minusDays(9);

        int count1 = Weeks.weeksBetween(now, twoDaysEarlier).getWeeks();
        int count2 = Weeks.weeksBetween(now, twoDaysLater).getWeeks();
        int count3 = Weeks.weeksBetween(now, nineDaysEarlier).getWeeks();
        int count4 = Weeks.weeksBetween(now, nineDaysLater).getWeeks();
    }

    @Test
    public void shouldInitializeSubscription() {
        DateTime beforeCreation = DateTime.now();
        String msisdn = "1234567890";
        Subscription subscription = new Subscription(msisdn, SubscriptionPack.BARI_KILKARI, DateTime.now(), DateTime.now());
        subscription.setStatus(SubscriptionStatus.NEW);

        DateTime afterCreation = DateTime.now();

        assertEquals(SubscriptionStatus.NEW, subscription.getStatus());
        assertEquals(msisdn, subscription.getMsisdn());
        assertEquals(SubscriptionPack.BARI_KILKARI, subscription.getPack());
        assertNotNull(subscription.getSubscriptionId());

        DateTime creationDate = subscription.getCreationDate();
        assertTrue(creationDate.isEqual(beforeCreation) || creationDate.isAfter(beforeCreation));
        assertTrue(creationDate.isEqual(afterCreation) || creationDate.isBefore(afterCreation));
    }


    @Test
    public void shouldChangeStatusOfSubscriptionToPendingDuringActivationRequest() {
        Subscription subscription = new Subscription("1234567890", SubscriptionPack.BARI_KILKARI, DateTime.now(), DateTime.now());
        subscription.setStatus(SubscriptionStatus.NEW);

        subscription.activationRequestSent();

        assertEquals(SubscriptionStatus.PENDING_ACTIVATION, subscription.getStatus());
        assertNull(subscription.getOperator());
    }

    @Test
    public void shouldChangeStatusOfSubscriptionToActiveForSuccessfulActivation() {
        DateTime createdAt = DateTime.now();
        DateTime activatedOn = createdAt.plus(5000);
        Subscription subscription = new Subscription("1234567890", SubscriptionPack.BARI_KILKARI, createdAt, DateTime.now());
        subscription.setStatus(SubscriptionStatus.NEW);

        subscription.setStartDate(activatedOn);
        Operator operator = Operator.AIRTEL;

        subscription.activate(operator.name(), activatedOn);

        assertEquals(SubscriptionStatus.ACTIVE, subscription.getStatus());
        assertEquals(operator, subscription.getOperator());
        assertEquals(createdAt.plus(5000), subscription.getStartDate());
    }

    @Test
    public void shouldChangeStatusOfSubscriptionToActivationFailedForUnsuccessfulActivation() {
        Subscription subscription = new Subscription("1234567890", SubscriptionPack.BARI_KILKARI, DateTime.now(), DateTime.now());
        subscription.setStatus(SubscriptionStatus.NEW);

        Operator operator = Operator.AIRTEL;
        subscription.activationFailed(operator.name());

        assertEquals(SubscriptionStatus.ACTIVATION_FAILED, subscription.getStatus());
        assertEquals(operator, subscription.getOperator());
    }

    @Test
    public void shouldChangeStatusOfSubscriptionToActivatedAndUpdateRenewalDate() {
        Subscription subscription = new Subscription("1234567890", SubscriptionPack.BARI_KILKARI, DateTime.now(), DateTime.now());
        subscription.setStatus(SubscriptionStatus.NEW);

        subscription.activateOnRenewal();

        assertEquals(SubscriptionStatus.ACTIVE, subscription.getStatus());
    }

    @Test
    public void shouldChangeStatusOfSubscriptionSuspendedAndUpdateRenewalDate() {
        Subscription subscription = new Subscription("1234567890", SubscriptionPack.BARI_KILKARI, DateTime.now(), DateTime.now());
        subscription.setStatus(SubscriptionStatus.NEW);

        subscription.suspendOnRenewal();

        assertEquals(SubscriptionStatus.SUSPENDED, subscription.getStatus());
    }

    @Test
    public void shouldChangeStatusToDeactivatedOnDeactivationOnlyIfPriorStatusIsNotPendingCompleted() {
        Subscription subscription = new Subscription("1234567890", SubscriptionPack.BARI_KILKARI, DateTime.now(), DateTime.now());
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.deactivate();

        assertEquals(SubscriptionStatus.DEACTIVATED, subscription.getStatus());
    }
    
    @Test
    public void shouldChangeStatusToCompletedOnDeactivationOnlyIfPriorStatusIsPendingCompleted() {
        Subscription subscription = new Subscription("1234567890", SubscriptionPack.BARI_KILKARI, DateTime.now(), DateTime.now());
        subscription.setStatus(SubscriptionStatus.PENDING_COMPLETION);
        subscription.deactivate();

        assertEquals(SubscriptionStatus.COMPLETED, subscription.getStatus());
    }

    @Test
    public void shouldChangeStatusOfSubscriptionToPendingCompletion() {
        Subscription subscription = new Subscription("1234567890", SubscriptionPack.BARI_KILKARI, DateTime.now(), DateTime.now());
        subscription.setStatus(SubscriptionStatus.NEW);

        subscription.complete();

        assertEquals(SubscriptionStatus.PENDING_COMPLETION, subscription.getStatus());
    }

    @Test
    public void shouldReturnIsActiveBasedOnStatus() {
        String msisdn = "9876534211";
        SubscriptionPack pack = SubscriptionPack.CHOTI_KILKARI;
        Subscription subscription = new Subscription(msisdn, pack, DateTime.now(), DateTime.now());

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
    public void shouldReturnIsActiveOrSuspendedBasedOnStatus() {
        String msisdn = "9876534211";
        SubscriptionPack pack = SubscriptionPack.CHOTI_KILKARI;
        Subscription subscription = new Subscription(msisdn, pack, DateTime.now(), DateTime.now());
        subscription.setStatus(SubscriptionStatus.ACTIVE);


        assertTrue(subscription.isActiveOrSuspended());

        subscription.setStatus(SubscriptionStatus.SUSPENDED);
        assertTrue(subscription.isActiveOrSuspended());

        subscription.setStatus(SubscriptionStatus.NEW);
        assertFalse(subscription.isActiveOrSuspended());

        subscription.setStatus(SubscriptionStatus.PENDING_ACTIVATION);
        assertFalse(subscription.isActiveOrSuspended());
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
    
    @Test
    public void shouldReturnCurrentWeekNumber(){
        Subscription subscription = new SubscriptionBuilder().withDefaults().withPack(SubscriptionPack.BARI_KILKARI).withStartDate(DateTime.now().minusWeeks(10)).build();
        assertEquals(11,subscription.getCurrentWeekOfSubscription());

        subscription = new SubscriptionBuilder().withDefaults().withPack(SubscriptionPack.CHOTI_KILKARI).withStartDate(DateTime.now().minusWeeks(2)).build();
        assertEquals(15,subscription.getCurrentWeekOfSubscription());

        subscription = new SubscriptionBuilder().withDefaults().withPack(SubscriptionPack.NANHI_KILKARI).withStartDate(DateTime.now().minusWeeks(2)).build();
        assertEquals(35,subscription.getCurrentWeekOfSubscription());
    }

    @Test
    public void shouldCheckIfTransitionToActiveStateIsPossible(){
        Subscription subscription = new Subscription();
        SubscriptionStatus currentStatus = mock(SubscriptionStatus.class);
        SubscriptionStatus toStatus = SubscriptionStatus.ACTIVE;
        subscription.setStatus(currentStatus);
        when(currentStatus.canTransitionTo(toStatus)).thenReturn(true);

        boolean canActivate = subscription.canActivate();

        assertTrue(canActivate);
        verify(currentStatus).canTransitionTo(toStatus);
    }

    @Test
    public void shouldCheckIfTransitionToDeactivateStateIsPossible(){
        Subscription subscription = new Subscription();
        SubscriptionStatus currentStatus = mock(SubscriptionStatus.class);
        SubscriptionStatus toStatus = SubscriptionStatus.DEACTIVATED;
        subscription.setStatus(currentStatus);
        when(currentStatus.canTransitionTo(toStatus)).thenReturn(true);

        boolean canDeactivate = subscription.canDeactivate();

        assertTrue(canDeactivate);
        verify(currentStatus).canTransitionTo(toStatus);
    }

    @Test
    public void shouldCheckIfTransitionToDeactivationRequestReceivedIsPossible(){
        Subscription subscription = new Subscription();
        SubscriptionStatus currentStatus = mock(SubscriptionStatus.class);
        SubscriptionStatus toStatus = SubscriptionStatus.DEACTIVATION_REQUEST_RECEIVED;
        subscription.setStatus(currentStatus);
        when(currentStatus.canTransitionTo(toStatus)).thenReturn(true);

        boolean canReceiveDeactivationRequest = subscription.canReceiveDeactivationRequest();

        assertTrue(canReceiveDeactivationRequest);
        verify(currentStatus).canTransitionTo(toStatus);
    }

    @Test
    public void shouldCheckIfTransitionToSuspensionStateIsPossible(){
        Subscription subscription = new Subscription();
        SubscriptionStatus currentStatus = mock(SubscriptionStatus.class);
        SubscriptionStatus toStatus = SubscriptionStatus.SUSPENDED;
        subscription.setStatus(currentStatus);
        when(currentStatus.canTransitionTo(toStatus)).thenReturn(true);

        boolean canSuspend = subscription.canSuspend();

        assertTrue(canSuspend);
        verify(currentStatus).canTransitionTo(toStatus);
    }

    @Test
    public void shouldCheckIfTransitionToNewStateIsPossible(){
        Subscription subscription = new Subscription();
        SubscriptionStatus currentStatus = mock(SubscriptionStatus.class);
        SubscriptionStatus toStatus = SubscriptionStatus.NEW;
        subscription.setStatus(currentStatus);
        when(currentStatus.canTransitionTo(toStatus)).thenReturn(true);

        boolean result = subscription.canCreateNewSubscription();

        assertTrue(result);
        verify(currentStatus).canTransitionTo(toStatus);
    }

    @Test
    public void shouldCheckIfTransitionToNewEarlyStateIsPossible(){
        Subscription subscription = new Subscription();
        SubscriptionStatus currentStatus = mock(SubscriptionStatus.class);
        SubscriptionStatus toStatus = SubscriptionStatus.NEW_EARLY;
        subscription.setStatus(currentStatus);
        when(currentStatus.canTransitionTo(toStatus)).thenReturn(true);

        boolean result = subscription.canCreateANewEarlySubscription();

        assertTrue(result);
        verify(currentStatus).canTransitionTo(toStatus);
    }

    @Test
    public void shouldCheckIfTransitionToActivationFailedStateIsPossible(){
        Subscription subscription = new Subscription();
        SubscriptionStatus currentStatus = mock(SubscriptionStatus.class);
        SubscriptionStatus toStatus = SubscriptionStatus.ACTIVATION_FAILED;
        subscription.setStatus(currentStatus);
        when(currentStatus.canTransitionTo(toStatus)).thenReturn(true);

        boolean canFailActivation = subscription.canFailActivation();

        assertTrue(canFailActivation);
        verify(currentStatus).canTransitionTo(toStatus);
    }

    @Test
    public void shouldCheckIfTransitionToPendingActivationStateIsPossible(){
        Subscription subscription = new Subscription();
        SubscriptionStatus currentStatus = mock(SubscriptionStatus.class);
        SubscriptionStatus toStatus = SubscriptionStatus.PENDING_ACTIVATION;
        subscription.setStatus(currentStatus);
        when(currentStatus.canTransitionTo(toStatus)).thenReturn(true);

        boolean canSendActivationRequest = subscription.canSendActivationRequest();

        assertTrue(canSendActivationRequest);
        verify(currentStatus).canTransitionTo(toStatus);
    }

    @Test
    public void shouldCheckIfTransitionToPendingDeactivationStateIsPossible(){
        Subscription subscription = new Subscription();
        SubscriptionStatus currentStatus = mock(SubscriptionStatus.class);
        SubscriptionStatus toStatus = SubscriptionStatus.PENDING_DEACTIVATION;
        subscription.setStatus(currentStatus);
        when(currentStatus.canTransitionTo(toStatus)).thenReturn(true);

        boolean result = subscription.canMoveToPendingDeactivation();

        assertTrue(result);
        verify(currentStatus).canTransitionTo(toStatus);
    }

    @Test
    public void shouldCheckIfTransitionToPendingCompletionStateIsPossible(){
        Subscription subscription = new Subscription();
        SubscriptionStatus currentStatus = mock(SubscriptionStatus.class);
        SubscriptionStatus toStatus = SubscriptionStatus.PENDING_COMPLETION;
        subscription.setStatus(currentStatus);
        when(currentStatus.canTransitionTo(toStatus)).thenReturn(true);

        boolean result = subscription.canMoveToPendingCompletion();

        assertTrue(result);
        verify(currentStatus).canTransitionTo(toStatus);
    }

    @Test
    public void shouldCheckIfTransitionToCompleteStateIsPossible(){
        Subscription subscription = new Subscription();
        SubscriptionStatus currentStatus = mock(SubscriptionStatus.class);
        SubscriptionStatus toStatus = SubscriptionStatus.COMPLETED;
        subscription.setStatus(currentStatus);
        when(currentStatus.canTransitionTo(toStatus)).thenReturn(true);

        boolean canComplete = subscription.canComplete();

        assertTrue(canComplete);
        verify(currentStatus).canTransitionTo(toStatus);
    }
}