package org.motechproject.ananya.kilkari.service.validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.subscription.builder.SubscriptionBuilder;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionStatus;
import org.motechproject.ananya.kilkari.subscription.service.SubscriptionService;
import org.motechproject.ananya.kilkari.subscription.validators.Errors;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


public class UnsubscriptionRequestValidatorTest {

    private UnsubscriptionRequestValidator unsubscriptionRequestValidator;
    @Mock
    private SubscriptionService subscriptionService;

    @Before
    public void setup() {
        initMocks(this);
        unsubscriptionRequestValidator = new UnsubscriptionRequestValidator(subscriptionService);
    }

    @Test
    public void shouldReturnValidIfUnsubscriptionRequestDetailsAreCorrect() {

        Subscription subscription = new SubscriptionBuilder().withDefaults().build();
        subscription.setStatus(SubscriptionStatus.ACTIVE);

        when(subscriptionService.findBySubscriptionId(subscription.getSubscriptionId())).thenReturn(subscription);

        assertTrue(unsubscriptionRequestValidator.validate(subscription.getSubscriptionId()).hasNoErrors());
    }

    @Test
    public void shouldReturnInvalidIfSubscriptionDoesNotExist() {
        String subscriptionId = "abcd1234";

        when(subscriptionService.findBySubscriptionId(anyString())).thenReturn(null);

        Errors errors = unsubscriptionRequestValidator.validate(subscriptionId);

        assertTrue(errors.hasErrors());
        assertTrue(errors.hasMessage("Invalid subscriptionId " + subscriptionId));
    }

    @Test
    public void shouldReturnInvalidIfSubscriptionNotInProgress() {
        String subscriptionId = "abcd";

        Subscription mockedSubscription = mock(Subscription.class);
        when(subscriptionService.findBySubscriptionId(anyString())).thenReturn(mockedSubscription);
        when(mockedSubscription.isInProgress()).thenReturn(false);
        when(mockedSubscription.getStatus()).thenReturn(SubscriptionStatus.COMPLETED);

        Errors errors = unsubscriptionRequestValidator.validate(subscriptionId);

        assertTrue(errors.hasErrors());
        assertTrue(errors.hasMessage("Cannot unsubscribe. Subscription in COMPLETED status"));

    }

    @Test
    public void shouldReturnValidIfSubscriptionIsInProgress() {
        String subscriptionId = "abcd";

        Subscription mockedSubscription = mock(Subscription.class);
        when(subscriptionService.findBySubscriptionId(anyString())).thenReturn(mockedSubscription);
        when(mockedSubscription.isInProgress()).thenReturn(true);
        when(mockedSubscription.getStatus()).thenReturn(SubscriptionStatus.ACTIVE);

        Errors errors = unsubscriptionRequestValidator.validate(subscriptionId);

        assertFalse(errors.hasErrors());

    }
}