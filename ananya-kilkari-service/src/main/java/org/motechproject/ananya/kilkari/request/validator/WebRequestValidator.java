package org.motechproject.ananya.kilkari.request.validator;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.ananya.kilkari.subscription.domain.CampaignChangeReason;
import org.motechproject.ananya.kilkari.subscription.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.domain.PhoneNumber;
import org.motechproject.ananya.kilkari.subscription.validators.Errors;
import org.motechproject.ananya.kilkari.subscription.validators.ValidationUtils;

import java.util.List;

public class WebRequestValidator {

    private Errors errors;

    public WebRequestValidator() {
        errors = new Errors();
    }

    public void validateOnlyOneOfEDDOrDOBOrWeekNumberPresent(String... args) {
        if (!ValidationUtils.assertOnlyOnePresent(args))
            errors.add("Invalid request. Only one of expected date of delivery, date of birth and week number should be present");

    }

    public void validateOnlyOneOfEDDOrDOBIsPresent(String... args) {
        if (!ValidationUtils.assertOnlyOnePresent(args))
            errors.add("Invalid request. Only one of expected date of delivery or date of birth should be present");

    }

    public void validateOneOfEDDOrDOBIsPresent(String... args) {
        if (!ValidationUtils.assertOnePresent(args))
            errors.add("Invalid request. One of expected date of delivery or date of birth should be present");

    }

    public void validateCampaignChangeReason(String reason) {
        if (!CampaignChangeReason.isValid(reason))
            errors.add("Invalid reason %s", reason);

    }

    public void validateWeekNumber(String week) {
        if (StringUtils.isNotEmpty(week)) {
            if (!ValidationUtils.assertNumeric(week)) {
                errors.add("Invalid week number %s", week);
            }
        }
    }

    public void validatePack(String pack) {
        if (!SubscriptionPack.isValid(pack)) {
            errors.add("Invalid subscription pack %s", pack);
        }
    }

    public void validateMsisdn(String msisdn) {
        if (PhoneNumber.isNotValid(msisdn)) {
            errors.add("Invalid msisdn %s", msisdn);
        }
    }

    public void validateChannel(String channel) {
        if (!Channel.isValid(channel)) {
            errors.add("Invalid channel %s", channel);
        }
    }

    public void validateChannel(String channelFromRequest, Channel channel) {
        if (!(Channel.isValid(channelFromRequest) && channel.name().equals(channelFromRequest))) {
            errors.add("Invalid channel %s", channelFromRequest);
        }
    }

    public void validateEDD(String expectedDateOfDelivery, DateTime createdAt) {
        if (StringUtils.isNotEmpty(expectedDateOfDelivery)) {
            if (!ValidationUtils.assertDateFormat(expectedDateOfDelivery)) {
                errors.add("Invalid expected date of delivery %s", expectedDateOfDelivery);
                return;
            }

            if (!ValidationUtils.assertDateBefore(createdAt, parseDateTime(expectedDateOfDelivery)))
                errors.add("Invalid expected date of delivery %s", expectedDateOfDelivery);
        }
    }

    public void validateDOB(String dateOfBirth, DateTime createdAt) {
        if (StringUtils.isNotEmpty(dateOfBirth)) {
            if (!ValidationUtils.assertDateFormat(dateOfBirth)) {
                errors.add("Invalid date of birth %s", dateOfBirth);
                return;
            }

            if (!ValidationUtils.assertDateBefore(parseDateTime(dateOfBirth), createdAt))
                errors.add("Invalid date of birth %s", dateOfBirth);
        }
    }

    public void validateAge(String beneficiaryAge) {
        if (StringUtils.isNotEmpty(beneficiaryAge)) {
            if (!ValidationUtils.assertNumeric(beneficiaryAge))
                errors.add("Invalid beneficiary age %s", beneficiaryAge);
        }
    }

    public Errors getErrors() {
        return errors;
    }

    private DateTime parseDateTime(String dateTime) {
        return StringUtils.isNotEmpty(dateTime) ? DateTimeFormat.forPattern("dd-MM-yyyy").parseDateTime(dateTime) : null;
    }

    public void validateSubscriptionPacksForChangeMsisdn(List<String> packs) {
        if (packs.size() <= 0) errors.add("At least one pack should be specified");

        boolean allPackPresent = false;
        for (String pack : packs) {
            if(StringUtils.trim(pack).toUpperCase().equals("ALL")) allPackPresent = true;
        }
        if (allPackPresent && packs.size() != 1) errors.add("No other pack allowed when ALL specified");

        if (allPackPresent) return;

        for (String pack : packs) validatePack(pack);
    }
}
