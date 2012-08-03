package org.motechproject.ananya.kilkari.subscription.validators;


import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ValidationUtils {

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

    public static boolean assertNumeric(String value) {
        return StringUtils.isNotEmpty(value) && StringUtils.isNumeric(value);
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

    public static boolean assertOnlyOnePresent(String... args) {
        List<Boolean> checks = new ArrayList<>();
        for (String arg : args)
            checks.add(StringUtils.isNotEmpty(arg));

        int numberOfOptions = CollectionUtils.countMatches(checks, new Predicate() {
            @Override
            public boolean evaluate(Object o) {
                return (Boolean) o;
            }
        });

        return !(numberOfOptions > 1);
    }
}
