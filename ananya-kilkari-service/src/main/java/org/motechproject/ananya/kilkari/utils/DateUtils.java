package org.motechproject.ananya.kilkari.utils;


import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

public class DateUtils {

    public static DateTime parseDate(String date) {
        return StringUtils.isNotEmpty(date) ? DateTimeFormat.forPattern("dd-MM-yyyy").parseDateTime(date) : null;
    }

    public static DateTime parseDateTime(String dateTime) {
        return StringUtils.isNotEmpty(dateTime) ? DateTimeFormat.forPattern("dd-MM-yyyy HH-mm-ss").parseDateTime(dateTime): null;
    }
}
