package org.motechproject.ananya.kilkari.validators;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.request.SubscriberWebRequest;
import org.motechproject.ananya.kilkari.subscription.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.validators.Errors;
import org.motechproject.ananya.kilkari.subscription.validators.ValidationUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SubscriberDetailsValidator {
    public Errors validate(SubscriberWebRequest request) {
        Errors errors = new Errors();
        validateChannel(request.getChannel(), errors);
        validateAge(request.getBeneficiaryAge(), errors);
        validateDOB(request.getDateOfBirth(), request.getCreatedAt(), errors);
        validateEDD(request.getExpectedDateOfDelivery(), request.getCreatedAt(), errors);
        validateOnlyOneOfEDDOrDOBPresent(request, errors);
        return errors;
    }

    private void validateOnlyOneOfEDDOrDOBPresent(SubscriberWebRequest request, Errors errors) {
        List<Boolean> checks = new ArrayList<>();
        checks.add(StringUtils.isNotEmpty(request.getExpectedDateOfDelivery()));
        checks.add(StringUtils.isNotEmpty(request.getDateOfBirth()));

        int numberOfOptions = CollectionUtils.countMatches(checks, new Predicate() {
            @Override
            public boolean evaluate(Object o) {
                return (Boolean) o;
            }
        });

        if (numberOfOptions > 1) {
            errors.add("Invalid request. Only one of date of delivery, date of birth should be present");
        }
    }


    private void validateChannel(String channel, Errors errors) {
        if (!ValidationUtils.assertChannel(channel) || !Channel.isCallCenter(channel)) {
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
