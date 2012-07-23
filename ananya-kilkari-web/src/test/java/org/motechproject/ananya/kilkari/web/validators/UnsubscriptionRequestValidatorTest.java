package org.motechproject.ananya.kilkari.web.validators;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.service.SubscriptionService;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
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
        String subscriptionId = "abcd1234";

        when(subscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(new Subscription());

        assertTrue(unsubscriptionRequestValidator.validate(subscriptionId).isEmpty());
    }

    @Test
    public void shouldReturnInvalidIfSubscriptionDoesNotExist() {
        String subscriptionId = "abcd1234";

        when(subscriptionService.findBySubscriptionId(anyString())).thenReturn(null);

        List<String> errors = unsubscriptionRequestValidator.validate(subscriptionId);

        assertFalse(errors.isEmpty());
        assertEquals("Invalid subscriptionId " + subscriptionId, errors.get(0));
    }
}
