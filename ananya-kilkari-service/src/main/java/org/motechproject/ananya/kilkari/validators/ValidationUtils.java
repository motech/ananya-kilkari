package org.motechproject.ananya.kilkari.validators;


import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.ananya.kilkari.subscription.domain.CampaignChangeReason;
import org.motechproject.ananya.kilkari.subscription.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.utils.DateUtils;

import java.util.regex.Pattern;

public class ValidationUtils {

    public static boolean assertNumeric(String value) {
        return StringUtils.isNotEmpty(value) && StringUtils.isNumeric(value);
    }

    public static boolean assertDateFormat(String value) {
        if (StringUtils.isEmpty(value) || !Pattern.matches("^\\d{2}-\\d{2}-\\d{4}$", value)) {
            return false;
        }
        try {
            DateTimeFormat.forPattern("dd-MM-yyyy").parseDateTime(value);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean assertChannel(String channel) {
        return Channel.isValid(channel);
    }

    public static boolean assertPack(String pack) {
        return SubscriptionPack.isValid(pack);
    }

    public static boolean assertDateBefore(DateTime before, DateTime now) {
        return before.isBefore(now);
    }

    public static boolean assertDateTimeFormat(String value) {
        if (StringUtils.isEmpty(value) || !Pattern.matches("^\\d{2}-\\d{2}-\\d{4} \\d{2}-\\d{2}-\\d{2}$", value)) {
            return false;
        }
        try {
            DateUtils.parseDateTime(value);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean assertCampaignChangeReason(String reason) {
        return CampaignChangeReason.isValid(reason);
    }

    public static boolean assertEDD(String expectedDateOfDelivery, DateTime createdAt) {
        if (StringUtils.isNotEmpty(expectedDateOfDelivery)) {
            if (!ValidationUtils.assertDateFormat(expectedDateOfDelivery))
                return false;

            if (!ValidationUtils.assertDateBefore(createdAt, DateUtils.parseDate(expectedDateOfDelivery)))
                return false;
        }
        return true;
    }

    public static boolean assertDOB(String dateOfBirth, DateTime createdAt) {
        if (StringUtils.isNotEmpty(dateOfBirth)) {
            if (!ValidationUtils.assertDateFormat(dateOfBirth))
                return false;

            if (!ValidationUtils.assertDateBefore(DateUtils.parseDate(dateOfBirth), createdAt))
                return false;
        }
        return true;
    }

    public static   boolean assertAge(String beneficiaryAge) {
        if (StringUtils.isNotEmpty(beneficiaryAge)) {
            if (!ValidationUtils.assertNumeric(beneficiaryAge))
                return false;
        }
        return true;
    }
}
