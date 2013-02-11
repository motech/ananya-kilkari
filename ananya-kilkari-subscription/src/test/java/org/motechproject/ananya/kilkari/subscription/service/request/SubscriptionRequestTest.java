package org.motechproject.ananya.kilkari.subscription.service.request;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ananya.kilkari.subscription.builder.SubscriptionRequestBuilder;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class SubscriptionRequestTest {

    private SubscriptionRequest subscriptionRequest;

    @Test
    public void shouldGetSubscriptionStartDateBasedOnWeekNumber() {
        SubscriptionPack subscriptionPack = mock(SubscriptionPack.class);
        DateTime creationDate = DateTime.now();
        int week = 38;
        subscriptionRequest = new SubscriptionRequestBuilder().withDefaults().withPack(subscriptionPack).withWeek(week).withCreationDate(creationDate).build();

        subscriptionRequest.getSubscriptionStartDate();

        verify(subscriptionPack).getStartDateForWeek(creationDate, week);
        verify(subscriptionPack, never()).getStartDate(any(DateTime.class));
    }

    @Test
    public void shouldGetSubscriptionStartDateBasedOnDOB() {
        SubscriptionPack subscriptionPack = mock(SubscriptionPack.class);
        DateTime creationDate = DateTime.now();
        DateTime dob = DateTime.now().minusDays(2);
        DateTime edd = null;
        subscriptionRequest = new SubscriptionRequestBuilder().withDefaults().withPack(subscriptionPack).withWeek(null).withCreationDate(creationDate).withDateOfBirth(dob).withExpectedDateOfDelivery(edd).build();

        subscriptionRequest.getSubscriptionStartDate();

        verify(subscriptionPack, never()).getStartDateForWeek(any(DateTime.class), anyInt());
        verify(subscriptionPack).getStartDate(dob);
        verify(subscriptionPack, never()).getStartDate(edd);
    }

    @Test
    public void shouldGetSubscriptionStartDateBasedOnEDD() {
        SubscriptionPack subscriptionPack = mock(SubscriptionPack.class);
        DateTime creationDate = DateTime.now();
        DateTime dob = null;
        DateTime edd = DateTime.now().plusDays(2);
        subscriptionRequest = new SubscriptionRequestBuilder().withDefaults().withPack(subscriptionPack).withWeek(null).withCreationDate(creationDate).withDateOfBirth(dob).withExpectedDateOfDelivery(edd).build();

        subscriptionRequest.getSubscriptionStartDate();

        verify(subscriptionPack, never()).getStartDateForWeek(any(DateTime.class), anyInt());
        verify(subscriptionPack, never()).getStartDate(dob);
        verify(subscriptionPack).getStartDate(edd);
    }
}
