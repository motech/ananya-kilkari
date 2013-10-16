package org.motechproject.ananya.kilkari.subscription.validators;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.obd.service.validator.Errors;
import org.motechproject.ananya.kilkari.subscription.builder.SubscriptionBuilder;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionStatus;
import org.motechproject.ananya.kilkari.subscription.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.subscription.repository.AllSubscriptions;
import org.motechproject.ananya.kilkari.subscription.service.SubscriptionService;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


public class UnsubscriptionValidatorTest {

    private UnsubscriptionValidator unsubscriptionValidator;
    @Mock
    private AllSubscriptions allSubscriptions;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setup() {
        initMocks(this);
        unsubscriptionValidator = new UnsubscriptionValidator(allSubscriptions);
    }

    @Test
    public void shouldReturnValidIfUnsubscriptionRequestDetailsAreCorrect() {

        Subscription subscription = new SubscriptionBuilder().withDefaults().build();
        subscription.setStatus(SubscriptionStatus.ACTIVE);

        when(allSubscriptions.findBySubscriptionId(subscription.getSubscriptionId())).thenReturn(subscription);

        unsubscriptionValidator.validate(subscription.getSubscriptionId());
    }

    @Test
    public void shouldReturnInvalidIfSubscriptionDoesNotExist() {
        String subscriptionId = "abcd1234";
        when(allSubscriptions.findBySubscriptionId(anyString())).thenReturn(null);

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage("Invalid subscriptionId abcd1234");

        unsubscriptionValidator.validate(subscriptionId);
    }

    @Test
    public void shouldReturnInvalidIfSubscriptionIsNotUpdatable() {
        String subscriptionId = "abcd";
        Subscription mockedSubscription = mock(Subscription.class);
        when(allSubscriptions.findBySubscriptionId(anyString())).thenReturn(mockedSubscription);
        when(mockedSubscription.isInUpdatableState()).thenReturn(false);
        when(mockedSubscription.getStatus()).thenReturn(SubscriptionStatus.COMPLETED);

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage("Cannot unsubscribe. Subscription in COMPLETED status");

        unsubscriptionValidator.validate(subscriptionId);
    }

    @Test
    public void shouldReturnValidIfSubscriptionIsUpdatable() {
        String subscriptionId = "abcd";

        Subscription mockedSubscription = mock(Subscription.class);
        when(allSubscriptions.findBySubscriptionId(anyString())).thenReturn(mockedSubscription);
        when(mockedSubscription.isInUpdatableState()).thenReturn(true);
        when(mockedSubscription.getStatus()).thenReturn(SubscriptionStatus.ACTIVE);

        unsubscriptionValidator.validate(subscriptionId);
    }
}