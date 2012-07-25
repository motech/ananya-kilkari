package org.motechproject.ananya.kilkari.subscription.validators;


import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.ananya.kilkari.subscription.domain.CampaignChangeReason;
import org.motechproject.ananya.kilkari.subscription.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;

import java.util.regex.Pattern;

public class ValidationUtils {

    public static boolean assertNumeric(String value) {
        return !StringUtils.isEmpty(value) && StringUtils.isNumeric(value);
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
            DateTimeFormat.forPattern("dd-MM-yyyy HH-mm-ss").parseDateTime(value);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean assertCampaignChangeReason(String reason) {
        return CampaignChangeReason.isValid(reason);
    }
}
