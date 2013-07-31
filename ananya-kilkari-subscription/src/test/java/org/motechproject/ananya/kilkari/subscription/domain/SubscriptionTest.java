package org.motechproject.ananya.kilkari.subscription.domain;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ananya.kilkari.messagecampaign.domain.MessageCampaignPack;
import org.motechproject.ananya.kilkari.subscription.builder.SubscriptionBuilder;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SubscriptionTest {
    @Test
    public void shouldInitializeSubscription() {
        DateTime beforeCreation = DateTime.now().withSecondOfMinute(0).withMillisOfSecond(0);
        String msisdn = "1234567890";
        Subscription subscription = new Subscription(msisdn, SubscriptionPack.BARI_KILKARI, DateTime.now(), DateTime.now(), null, null);
        subscription.setStatus(SubscriptionStatus.NEW);

        DateTime afterCreation = DateTime.now().withSecondOfMinute(0).withMillisOfSecond(0);

        assertEquals(SubscriptionStatus.NEW, subscription.getStatus());
        assertEquals(msisdn, subscription.getMsisdn());
        assertEquals(SubscriptionPack.BARI_KILKARI, subscription.getPack());
        assertEquals(MessageCampaignPack.BARI_KILKARI, subscription.getMessageCampaignPack());
        assertNotNull(subscription.getSubscriptionId());

        DateTime creationDate = subscription.getCreationDate();
        assertTrue(creationDate.isEqual(beforeCreation) || creationDate.isAfter(beforeCreation));
        assertTrue(creationDate.isEqual(afterCreation) || creationDate.isBefore(afterCreation));
    }

    @Test
    public void shouldChangeStatusOfSubscriptionToPendingDuringActivationRequest() {
        Subscription subscription = new Subscription("1234567890", SubscriptionPack.BARI_KILKARI, DateTime.now(), DateTime.now(), null, null);
        subscription.setStatus(SubscriptionStatus.NEW);

        subscription.activationRequestSent();

        assertEquals(SubscriptionStatus.PENDING_ACTIVATION, subscription.getStatus());
        assertNull(subscription.getOperator());
    }

    @Test
    public void shouldChangeStatusOfSubscriptionToActiveForSuccessfulActivation() {
        DateTime createdAt = DateTime.now();
        DateTime activatedOn = createdAt.plus(5000);
        DateTime scheduleStartDate = activatedOn.plusDays(2).plusMinutes(30);
        Subscription subscription = new Subscription("1234567890", SubscriptionPack.BARI_KILKARI, createdAt, activatedOn, null, null);
        subscription.setStatus(SubscriptionStatus.NEW);

        Operator operator = Operator.AIRTEL;

        subscription.activate(operator.name(), scheduleStartDate, activatedOn);

        assertEquals(SubscriptionStatus.ACTIVE, subscription.getStatus());
        assertEquals(operator, subscription.getOperator());
        assertEquals(activatedOn.withSecondOfMinute(0).withMillisOfSecond(0), subscription.getActivationDate());
        assertEquals(scheduleStartDate.withSecondOfMinute(0).withMillisOfSecond(0), subscription.getScheduleStartDate());
    }

    @Test
    public void shouldChangeStatusOfSubscriptionToActivationFailedForUnsuccessfulActivation() {
        Subscription subscription = new Subscription("1234567890", SubscriptionPack.BARI_KILKARI, DateTime.now(), DateTime.now(), null, null);
        subscription.setStatus(SubscriptionStatus.NEW);

        Operator operator = Operator.AIRTEL;
        subscription.activationFailed(operator.name());

        assertEquals(SubscriptionStatus.ACTIVATION_FAILED, subscription.getStatus());
        assertEquals(operator, subscription.getOperator());
    }

    @Test
    public void shouldChangeStatusOfSubscriptionToActivatedAndUpdateRenewalDate() {
        Subscription subscription = new Subscription("1234567890", SubscriptionPack.BARI_KILKARI, DateTime.now(), DateTime.now(), null, null);
        subscription.setStatus(SubscriptionStatus.NEW);

        subscription.activateOnRenewal();

        assertEquals(SubscriptionStatus.ACTIVE, subscription.getStatus());
    }

    @Test
    public void shouldChangeStatusOfSubscriptionSuspendedAndUpdateRenewalDate() {
        Subscription subscription = new Subscription("1234567890", SubscriptionPack.BARI_KILKARI, DateTime.now(), DateTime.now(), null, null);
        subscription.setStatus(SubscriptionStatus.NEW);

        subscription.suspendOnRenewal();

        assertEquals(SubscriptionStatus.SUSPENDED, subscription.getStatus());
    }

    @Test
    public void shouldChangeStatusToDeactivatedOnDeactivationOnlyIfPriorStatusIsNotPendingCompleted() {
        Subscription subscription = new Subscription("1234567890", SubscriptionPack.BARI_KILKARI, DateTime.now(), DateTime.now(), null, null);
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.deactivate();

        assertEquals(SubscriptionStatus.DEACTIVATED, subscription.getStatus());
    }

    @Test
    public void shouldChangeStatusToCompletedOnDeactivationOnlyIfPriorStatusIsPendingCompleted() {
        Subscription subscription = new Subscription("1234567890", SubscriptionPack.BARI_KILKARI, DateTime.now(), DateTime.now(), null, null);
        subscription.setStatus(SubscriptionStatus.PENDING_COMPLETION);
        subscription.deactivate();

        assertEquals(SubscriptionStatus.COMPLETED, subscription.getStatus());
    }

    @Test
    public void shouldChangeStatusOfSubscriptionToPendingCompletion() {
        Subscription subscription = new Subscription("1234567890", SubscriptionPack.BARI_KILKARI, DateTime.now(), DateTime.now(), null, null);
        subscription.setStatus(SubscriptionStatus.NEW);

        subscription.complete();

        assertEquals(SubscriptionStatus.PENDING_COMPLETION, subscription.getStatus());
    }

    @Test
    public void shouldReturnIsActiveBasedOnStatus() {
        String msisdn = "9876534211";
        SubscriptionPack pack = SubscriptionPack.NAVJAAT_KILKARI;
        Subscription subscription = new Subscription(msisdn, pack, DateTime.now(), DateTime.now(), null, null);

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
        SubscriptionPack pack = SubscriptionPack.NAVJAAT_KILKARI;
        Subscription subscription = new Subscription(msisdn, pack, DateTime.now(), DateTime.now(), null, null);
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
    public void shouldReturnIsUpdatableBasedOnStatus() {
        String msisdn = "9876534211";
        SubscriptionPack pack = SubscriptionPack.NAVJAAT_KILKARI;
        Subscription subscription = new Subscription(msisdn, pack, DateTime.now(), DateTime.now(), null, null);

        subscription.setStatus(SubscriptionStatus.ACTIVE);
        assertTrue(subscription.isInUpdatableState());

        subscription.setStatus(SubscriptionStatus.SUSPENDED);
        assertTrue(subscription.isInUpdatableState());

        subscription.setStatus(SubscriptionStatus.PENDING_DEACTIVATION);
        assertFalse(subscription.isInUpdatableState());

        subscription.setStatus(SubscriptionStatus.NEW);
        assertFalse(subscription.isInUpdatableState());

        subscription.setStatus(SubscriptionStatus.PENDING_ACTIVATION);
        assertFalse(subscription.isInUpdatableState());
    }

    @Test
    public void expiryDateShouldBeEndDateOfTheCurrentWeek_inReferenceToScheduleStartDate() {
        DateTime scheduleStartDate = DateTime.now().minusWeeks(1);
        Subscription subscription = new SubscriptionBuilder()
                .withDefaults()
                .withStartDate(scheduleStartDate.minusWeeks(3))
                .withScheduleStartDate(scheduleStartDate)
                .build();

        DateTime expiryDate = subscription.getCurrentWeeksMessageExpiryDate();
        assertEquals(expiryDate, subscription.getScheduleStartDate().plusWeeks(2));

        subscription = new SubscriptionBuilder()
                .withDefaults()
                .withStartDate(scheduleStartDate.minusWeeks(3))
                .withScheduleStartDate(null)
                .build();
        assertNull(subscription.getCurrentWeeksMessageExpiryDate());
    }

    @Test
    public void shouldReturnFalseForIsActiveWhenTheStatusIsPendingActivation() {
        Subscription subscription = new SubscriptionBuilder().withDefaults().withStatus(SubscriptionStatus.PENDING_ACTIVATION).build();

        assertFalse(subscription.hasBeenActivated());
    }

    @Test
    public void shouldReturnFalseForIsActiveWhenTheStatusIsActivationFailed() {
        Subscription subscription = new SubscriptionBuilder().withDefaults().withStatus(SubscriptionStatus.ACTIVATION_FAILED).build();

        assertFalse(subscription.hasBeenActivated());
    }

    @Test
    public void shouldReturnTrueForIsActiveForAnyOtherStatus() {
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
    public void shouldCheckIfTransitionToActiveStateIsPossible() {
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
    public void shouldCheckIfTransitionToDeactivateStateIsPossible() {
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
    public void shouldCheckIfTransitionToDeactivationRequestReceivedIsPossible() {
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
    public void shouldCheckIfTransitionToSuspensionStateIsPossible() {
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
    public void shouldCheckIfTransitionToNewStateIsPossible() {
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
    public void shouldCheckIfTransitionToNewEarlyStateIsPossible() {
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
    public void shouldCheckIfTransitionToActivationFailedStateIsPossible() {
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
    public void shouldCheckIfTransitionToPendingActivationStateIsPossible() {
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
    public void shouldCheckIfTransitionToPendingDeactivationStateIsPossible() {
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
    public void shouldCheckIfTransitionToPendingCompletionStateIsPossible() {
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
    public void shouldCheckIfTransitionToCompleteStateIsPossible() {
        Subscription subscription = new Subscription();
        SubscriptionStatus currentStatus = mock(SubscriptionStatus.class);
        SubscriptionStatus toStatus = SubscriptionStatus.COMPLETED;
        subscription.setStatus(currentStatus);
        when(currentStatus.canTransitionTo(toStatus)).thenReturn(true);

        boolean canComplete = subscription.canComplete();

        assertTrue(canComplete);
        verify(currentStatus).canTransitionTo(toStatus);
    }

    @Test
    public void shouldFloorStartDateToExactMinutes() {
        DateTime dateWithSeconds = DateTime.now().withMinuteOfHour(22).withSecondOfMinute(42);

        SubscriptionBuilder builder = new SubscriptionBuilder().withDefaults();
        Subscription subscription = builder.withStartDate(dateWithSeconds).build();
        assertEquals(dateWithSeconds.withSecondOfMinute(0).withMillisOfSecond(0), subscription.getStartDate());

        dateWithSeconds = DateTime.now().withMinuteOfHour(23).withSecondOfMinute(41);
        subscription = builder.withStartDate(dateWithSeconds).build();
        assertEquals(dateWithSeconds.withSecondOfMinute(0).withMillisOfSecond(0), subscription.getStartDate());
    }

    @Test
    public void shouldFloorActivationDateAndScheduleStartDateToExactMinutes() {
        DateTime activationDateWithSeconds = DateTime.now().withMinuteOfHour(22).withSecondOfMinute(42);
        DateTime scheduleDateWithSeconds = DateTime.now().withMinuteOfHour(25).withSecondOfMinute(22);

        Subscription subscription = new SubscriptionBuilder().withDefaults().build();

        subscription.activate("airtel", scheduleDateWithSeconds, activationDateWithSeconds);
        assertEquals(activationDateWithSeconds.withSecondOfMinute(0).withMillisOfSecond(0), subscription.getActivationDate());
        assertEquals(scheduleDateWithSeconds.withSecondOfMinute(0).withMillisOfSecond(0), subscription.getScheduleStartDate());
    }

    @Test
    public void shouldFloorCreationDateToExactMinutes() {
        DateTime dateWithSeconds = DateTime.now().withMinuteOfHour(22).withSecondOfMinute(42);

        Subscription subscription = new Subscription("abcd", SubscriptionPack.BARI_KILKARI, dateWithSeconds, DateTime.now(), null, null);
        assertEquals(dateWithSeconds.withSecondOfMinute(0).withMillisOfSecond(0), subscription.getCreationDate());
    }

    @Test
    public void shouldFloorStartDateForSubscriptionToExactMinutes_forLateSubscription() {
        DateTime now = DateTime.now();
        Subscription subscription = new Subscription("abcd", SubscriptionPack.BARI_KILKARI, now, now.minusWeeks(2), null, null);

        DateTime startDateForSubscription = subscription.getStartDateForSubscription(now.plusWeeks(1));

        assertEquals(now.minusWeeks(1).withSecondOfMinute(0).withMillisOfSecond(0), startDateForSubscription);
    }

    @Test
    public void shouldFloorStartDateForSubscriptionToExactMinutes_forNonLateSubscription() {
        DateTime now = DateTime.now();
        Subscription subscription = new Subscription("abcd", SubscriptionPack.BARI_KILKARI, now.minusWeeks(2), now, null, null);

        DateTime startDateForSubscription = subscription.getStartDateForSubscription(now.plusWeeks(1));

        assertEquals(now.plusWeeks(1).withSecondOfMinute(0).withMillisOfSecond(0), startDateForSubscription);
    }

    @Test
    public void shouldReturnNextWeek_basedOnScheduleStartDate() {
        Subscription subscription = new SubscriptionBuilder().withDefaults().withScheduleStartDate(DateTime.now().minusWeeks(3)).build();
        int nextWeekNumber = subscription.getNextWeekNumber();
        assertEquals(5, nextWeekNumber);

        subscription = new SubscriptionBuilder().withDefaults().withScheduleStartDate(null).build();
        nextWeekNumber = subscription.getNextWeekNumber();
        assertEquals(1, nextWeekNumber);
    }

    @Test
    public void shouldReturnEndDate_withReferenceToScheduleStartDate() {
        DateTime scheduleStartDate = DateTime.now().withSecondOfMinute(0).withMillisOfSecond(0).minusWeeks(3);
        SubscriptionPack pack = SubscriptionPack.NAVJAAT_KILKARI;
        Subscription subscription = new SubscriptionBuilder()
                .withDefaults()
                .withPack(pack)
                .withScheduleStartDate(scheduleStartDate).build();

        DateTime endDate = subscription.endDate();

        assertEquals(scheduleStartDate.plusWeeks(pack.getTotalWeeks()), endDate);
    }
}