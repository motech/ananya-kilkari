package org.motechproject.ananya.kilkari.validators;


import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.domain.Channel;
import org.motechproject.ananya.kilkari.domain.SubscriptionPack;
import org.motechproject.common.domain.PhoneNumber;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ValidationUtils {

    public static boolean assertNumeric(String value) {
        return !StringUtils.isEmpty(value) && StringUtils.isNumeric(value);
    }

    public static boolean assertDateFormat(String value, String format) {
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

    public static boolean assertChannel(String channel) {
        return Channel.isValid(channel);
    }

    public static boolean assertPack(String pack) {
        return SubscriptionPack.isValid(pack);
    }

    public static boolean assertMsisdn(String msisdn) {
        return PhoneNumber.isValid(msisdn);
    }

    public static boolean assertNotNull(Object object) {
        return object != null;
    }

    public static boolean assertNull(Object object) {
        return object == null;
    }

    public static boolean assertDateBefore(DateTime before, DateTime now) {
        return before.isBefore(now);
    }
}
