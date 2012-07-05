package org.motechproject.ananya.kilkari.validators;

import org.junit.Test;
import org.motechproject.ananya.kilkari.domain.SubscriberCareReasons;
import org.motechproject.ananya.kilkari.domain.SubscriberCareRequest;
import org.motechproject.ananya.kilkari.exceptions.ValidationException;

public class SubscriberCareRequestValidatorTest {

    @Test(expected = ValidationException.class)
    public void shouldThrowExceptionForInvalidMsisdnNumber() {
        SubscriberCareRequest subscriberCareRequest = new SubscriberCareRequest();
        subscriberCareRequest.setMsisdn("12345");
        subscriberCareRequest.setReason(SubscriberCareReasons.CHANGE_PACK.name());

        SubscriberCareRequestValidator.validate(subscriberCareRequest);
    }

    @Test(expected = ValidationException.class)
    public void shouldThrowExceptionForNonNumericMsisdn() {
        SubscriberCareRequest subscriberCareRequest = new SubscriberCareRequest();
        subscriberCareRequest.setMsisdn("1234556789o");
        subscriberCareRequest.setReason(SubscriberCareReasons.CHANGE_PACK.name());

        SubscriberCareRequestValidator.validate(subscriberCareRequest);
    }

    @Test(expected = ValidationException.class)
    public void shouldThrowExceptionForInvalidSubscriberCareReason() {
        SubscriberCareRequest subscriberCareRequest = new SubscriberCareRequest();
        subscriberCareRequest.setMsisdn("1234567890");
        subscriberCareRequest.setReason("Invalid-reason");

        SubscriberCareRequestValidator.validate(subscriberCareRequest);
    }
}
