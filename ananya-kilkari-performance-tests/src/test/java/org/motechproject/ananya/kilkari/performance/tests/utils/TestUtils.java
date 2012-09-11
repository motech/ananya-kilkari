package org.motechproject.ananya.kilkari.performance.tests.utils;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.joda.time.DateTime;

public class TestUtils {

    public static String getRandomMsisdn() {
        return "9" + RandomStringUtils.randomNumeric(9);
    }

    public static <T> T getRandomElementFromList(T[] objects) {
        return objects[RandomUtils.nextInt(objects.length)];
    }

    public static String dateString(DateTime dateTime) {
        return dateTime.toString("dd-MM-yyyy hh-mm-ss");
    }

    public static String getRandomCampaignId() {
        return "WEEK" + Integer.toString(RandomUtils.nextInt(60) + 1);
    }
}
