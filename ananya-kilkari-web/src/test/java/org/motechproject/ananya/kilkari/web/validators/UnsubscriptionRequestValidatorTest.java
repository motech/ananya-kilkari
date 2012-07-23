package org.motechproject.ananya.kilkari.web.validators;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.request.UnsubscriptionRequest;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
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
        UnsubscriptionRequest unsubscriptionRequest = new UnsubscriptionRequest();
        unsubscriptionRequest.setMsisdn("1234567890");
        unsubscriptionRequest.setSubscriptionId("abcd1234");
        unsubscriptionRequest.setPack(SubscriptionPack.FIFTEEN_MONTHS.name());
        unsubscriptionRequest.setReason("some reason");

        when(subscriptionService.findBySubscriptionId("abcd1234")).thenReturn(new Subscription());

        assertTrue(unsubscriptionRequestValidator.validate(unsubscriptionRequest).isEmpty());
    }

    @Test
    public void shouldReturnInValidIfMsisdnIsAnInvalidNumber() {
        UnsubscriptionRequest unsubscriptionRequest = new UnsubscriptionRequest();
        unsubscriptionRequest.setMsisdn("12345");
        unsubscriptionRequest.setSubscriptionId("abcd1234");
        unsubscriptionRequest.setPack(SubscriptionPack.FIFTEEN_MONTHS.name());
        unsubscriptionRequest.setReason("some reason");

        when(subscriptionService.findBySubscriptionId(anyString())).thenReturn(new Subscription());

        List<String> errors = unsubscriptionRequestValidator.validate(unsubscriptionRequest);

        assertFalse(errors.isEmpty());
        assertEquals("Invalid msisdn 12345", errors.get(0));
    }

    @Test
    public void shouldReturnInValidIfMsisdnIsAlphaNumeric() {
        UnsubscriptionRequest unsubscriptionRequest = new UnsubscriptionRequest();
        unsubscriptionRequest.setMsisdn("123456789a");
        unsubscriptionRequest.setSubscriptionId("abcd1234");
        unsubscriptionRequest.setPack(SubscriptionPack.FIFTEEN_MONTHS.name());
        unsubscriptionRequest.setReason("some reason");

        when(subscriptionService.findBySubscriptionId(anyString())).thenReturn(new Subscription());

        List<String> errors = unsubscriptionRequestValidator.validate(unsubscriptionRequest);

        assertFalse(errors.isEmpty());
        assertEquals("Invalid msisdn 123456789a", errors.get(0));
    }

    @Test
    public void shouldReturnInvalidIfSubscriptionPackIsInvalid() {
        UnsubscriptionRequest unsubscriptionRequest = new UnsubscriptionRequest();
        unsubscriptionRequest.setMsisdn("1234567890");
        unsubscriptionRequest.setSubscriptionId("abcd1234");
        unsubscriptionRequest.setPack("some_invalid_pack");
        unsubscriptionRequest.setReason("some reason");

        when(subscriptionService.findBySubscriptionId(anyString())).thenReturn(new Subscription());

        List<String> errors = unsubscriptionRequestValidator.validate(unsubscriptionRequest);

        assertFalse(errors.isEmpty());
        assertEquals("Invalid pack some_invalid_pack", errors.get(0));
    }

    @Test
    public void shouldReturnInvalidIfSubscriptionDoesNotExist() {
        UnsubscriptionRequest unsubscriptionRequest = new UnsubscriptionRequest();
        unsubscriptionRequest.setMsisdn("1234567890");
        unsubscriptionRequest.setSubscriptionId("abcd1234");
        unsubscriptionRequest.setPack(SubscriptionPack.FIFTEEN_MONTHS.name());
        unsubscriptionRequest.setReason("some reason");

        when(subscriptionService.findBySubscriptionId(anyString())).thenReturn(null);

        List<String> errors = unsubscriptionRequestValidator.validate(unsubscriptionRequest);

        assertFalse(errors.isEmpty());
        assertEquals("Invalid subscriptionId abcd1234", errors.get(0));
    }
}
