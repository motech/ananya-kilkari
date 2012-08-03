package org.motechproject.ananya.kilkari.validators;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.motechproject.ananya.kilkari.domain.SubscriberCareReasons;
import org.motechproject.ananya.kilkari.domain.SubscriberCareRequest;
import org.motechproject.ananya.kilkari.subscription.exceptions.ValidationException;

public class SubscriberCareRequestValidatorTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private SubscriberCareRequestValidator subscriberCareRequestValidator;

    @Before
    public void setUp() throws Exception {
        subscriberCareRequestValidator = new SubscriberCareRequestValidator();
    }

    @Test(expected = ValidationException.class)
    public void shouldThrowExceptionForInvalidMsisdnNumber() {
        SubscriberCareRequest subscriberCareRequest = new SubscriberCareRequest("12345", SubscriberCareReasons.HELP.name(), "ivr", DateTime.now());

        subscriberCareRequestValidator.validate(subscriberCareRequest);
    }

    @Test(expected = ValidationException.class)
    public void shouldThrowExceptionForNonNumericMsisdn() {
        SubscriberCareRequest subscriberCareRequest = new SubscriberCareRequest("1234556789o", SubscriberCareReasons.HELP.name(), "ivr", DateTime.now());

        subscriberCareRequestValidator.validate(subscriberCareRequest);
    }

    @Test(expected = ValidationException.class)
    public void shouldThrowExceptionForInvalidSubscriberCareReason() {
        SubscriberCareRequest subscriberCareRequest = new SubscriberCareRequest("1234567890", "Invalid-reason", "ivr", DateTime.now());

        subscriberCareRequestValidator.validate(subscriberCareRequest);
    }

    @Test
    public void shouldValidateForInvalidChannel() {
        SubscriberCareRequest subscriberCareRequest = new SubscriberCareRequest("1234567890", "Invalid-reason", "invalid-channel", DateTime.now());

        expectedException.expect(ValidationException.class);
        subscriberCareRequestValidator.validate(subscriberCareRequest);

    }
}
