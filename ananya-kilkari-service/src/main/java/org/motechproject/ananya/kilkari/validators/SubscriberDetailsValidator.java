package org.motechproject.ananya.kilkari.validators;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.request.SubscriberUpdateWebRequest;
import org.motechproject.ananya.kilkari.subscription.validators.Errors;
import org.motechproject.ananya.kilkari.subscription.validators.ValidationUtils;
import org.springframework.stereotype.Component;

@Component
public class SubscriberDetailsValidator {
    public Errors validate(SubscriberUpdateWebRequest request) {
        Errors errors = new Errors();
        validateChannel(request.getChannel(), errors);
        validateAge(request.getBeneficiaryAge(), errors);
        validateDOB(request.getDateOfBirth(), request.getCreatedAt(), errors);
        validateEDD(request.getExpectedDateOfDelivery(), request.getCreatedAt(), errors);
        return errors;
    }

    private void validateChannel(String channel, Errors errors) {
        if (!ValidationUtils.assertChannel(channel)) {
            errors.add("Invalid channel %s", channel);
        }
    }

    private void validateEDD(String expectedDateOfDelivery, DateTime createdAt, Errors errors) {
        if (!ValidationUtils.assertEDD(expectedDateOfDelivery, createdAt))
            errors.add("Invalid expected date of delivery %s", expectedDateOfDelivery);
    }

    private void validateDOB(String dateOfBirth, DateTime createdAt, Errors errors) {
        if (!ValidationUtils.assertDOB(dateOfBirth, createdAt))
            errors.add("Invalid date of birth %s", dateOfBirth);
    }

    private void validateAge(String beneficiaryAge, Errors errors) {
        if (!ValidationUtils.assertAge(beneficiaryAge))
            errors.add("Invalid beneficiary age %s", beneficiaryAge);
    }
}
