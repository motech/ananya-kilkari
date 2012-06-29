package org.motechproject.ananya.kilkari.validation;


import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.kilkari.exceptions.ValidationException;

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
        if(StringUtils.isEmpty(value)) {
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
}
