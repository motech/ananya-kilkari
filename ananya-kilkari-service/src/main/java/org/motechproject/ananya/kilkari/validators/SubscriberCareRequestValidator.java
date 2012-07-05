package org.motechproject.ananya.kilkari.validators;

import org.motechproject.ananya.kilkari.domain.SubscriberCareReasons;
import org.motechproject.ananya.kilkari.domain.SubscriberCareRequest;
import org.motechproject.ananya.kilkari.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.validation.ValidationUtils;

public class SubscriberCareRequestValidator {
    public static void validate(SubscriberCareRequest subscriberCareRequest) {
        ValidationUtils.assertMsisdn(subscriberCareRequest.getMsisdn());
        assertSubscriberCareReason(subscriberCareRequest);
    }

    private static void assertSubscriberCareReason(SubscriberCareRequest subscriberCareRequest) {
        String reason = subscriberCareRequest.getReason();
        if (!SubscriberCareReasons.isValid(reason))
            throw new ValidationException(String.format("Invalid subscriber care reason %s", reason));
    }
}
