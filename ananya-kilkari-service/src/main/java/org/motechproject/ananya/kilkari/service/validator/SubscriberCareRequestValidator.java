package org.motechproject.ananya.kilkari.service.validator;

import org.motechproject.ananya.kilkari.subscription.domain.SubscriberCareReasons;
import org.motechproject.ananya.kilkari.subscription.service.request.SubscriberCareRequest;
import org.motechproject.ananya.kilkari.subscription.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.domain.PhoneNumber;
import org.springframework.stereotype.Component;

@Component
public class SubscriberCareRequestValidator {
    public void validate(SubscriberCareRequest subscriberCareRequest) {
        validateMsisdn(subscriberCareRequest);
        assertSubscriberCareReason(subscriberCareRequest);
        assertChannel(subscriberCareRequest);
    }

    private void validateMsisdn(SubscriberCareRequest subscriberCareRequest) {
        String msisdn = subscriberCareRequest.getMsisdn();
        if(PhoneNumber.isNotValid(msisdn)) {
            throw new ValidationException(String.format("Invalid msisdn %s", msisdn));
        }
    }

    private void assertChannel(SubscriberCareRequest subscriberCareRequest) {
        String channel = subscriberCareRequest.getChannel();
        if (!Channel.isValid(channel)) {
            throw new ValidationException(String.format("Invalid channel %s", channel));
        }
    }

    private void assertSubscriberCareReason(SubscriberCareRequest subscriberCareRequest) {
        String reason = subscriberCareRequest.getReason();
        if (!SubscriberCareReasons.isValid(reason))
            throw new ValidationException(String.format("Invalid subscriber care reason %s", reason));
    }
}
