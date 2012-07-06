package org.motechproject.ananya.kilkari.validators;

import org.junit.Test;
import org.motechproject.ananya.kilkari.domain.SubscriberCareReasons;
import org.motechproject.ananya.kilkari.domain.SubscriberCareRequest;
import org.motechproject.ananya.kilkari.exceptions.ValidationException;

public class SubscriberCareRequestValidatorTest {

    @Test(expected = ValidationException.class)
    public void shouldThrowExceptionForInvalidMsisdnNumber() {
        SubscriberCareRequest subscriberCareRequest = new SubscriberCareRequest("12345", SubscriberCareReasons.CHANGE_PACK.name());

        SubscriberCareRequestValidator.validate(subscriberCareRequest);
    }

    @Test(expected = ValidationException.class)
    public void shouldThrowExceptionForNonNumericMsisdn() {
        SubscriberCareRequest subscriberCareRequest = new SubscriberCareRequest("1234556789o", SubscriberCareReasons.CHANGE_PACK.name());

        SubscriberCareRequestValidator.validate(subscriberCareRequest);
    }

    @Test(expected = ValidationException.class)
    public void shouldThrowExceptionForInvalidSubscriberCareReason() {
        SubscriberCareRequest subscriberCareRequest = new SubscriberCareRequest("1234567890", "Invalid-reason");

        SubscriberCareRequestValidator.validate(subscriberCareRequest);
    }
}
