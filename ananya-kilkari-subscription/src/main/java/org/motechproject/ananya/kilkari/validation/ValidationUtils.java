package org.motechproject.ananya.kilkari.validation;


import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.kilkari.domain.Channel;
import org.motechproject.ananya.kilkari.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.exceptions.ValidationException;
import org.motechproject.common.domain.PhoneNumber;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ValidationUtils {

    public static void assertNumeric(String value, String errorMessage) {
        if (StringUtils.isEmpty(value) || !StringUtils.isNumeric(value)) {
            throw new ValidationException(String.format(errorMessage, value));
        }
    }

    public static void assertDateFormat(String value, String format, String errorMessage) {
        if (!assertDateFormat(value, format)) {
            throw new ValidationException(String.format(errorMessage, value, format));
        }
    }

    private static boolean assertDateFormat(String value, String format) {
        if (StringUtils.isEmpty(value)) {
            return false;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Date parsedDate;
        try {
            simpleDateFormat.setLenient(false);
            parsedDate = simpleDateFormat.parse(value);
        } catch (ParseException e) {
            return false;
        }

        return simpleDateFormat.format(parsedDate).equals(value);
    }

    public static void assertChannel(String channel) {
        if (!Channel.isValid(channel))
            throw new ValidationException(String.format("Invalid channel %s", channel));
    }

    public static void assertPack(String pack) {
        if (!SubscriptionPack.isValid(pack))
            throw new ValidationException(String.format("Invalid subscription pack %s", pack));
    }

    public static void assertMsisdn(String msisdn) {
        if (PhoneNumber.isNotValid(msisdn))
            throw new ValidationException(String.format("Invalid msisdn %s", msisdn));
    }

    public static void assertNotNull(Object object, String message) {
        if (object == null)
            throw new ValidationException(message);
    }

    public static void assertNull(Object object, String message) {
        if (object != null)
            throw new ValidationException(message);
    }
}
