package org.motechproject.ananya.kilkari.functional.test.utils;

import org.joda.time.DateTime;

import java.util.Date;

import static junit.framework.Assert.assertEquals;

public class FakeTimeUtils {

    public static void moveToFutureTime(DateTime dateTime) {
        String offsetValue = System.getProperty("faketime.offset.seconds");
        long currentOffset = Long.parseLong(offsetValue == null ? "0" : offsetValue);

        System.out.println("Changing fake time from " + DateTime.now().toString("dd/MM/yyyy HH") + " hrs. to " + dateTime.toString("dd/MM/yyyy HH") + " hrs.");
        Date newDateTime = dateTime.toDate();
        long newOffset = ((newDateTime.getTime() - System.currentTimeMillis()) / 1000) + currentOffset;
        System.setProperty("faketime.offset.seconds", String.valueOf(newOffset));

        assertEquals("Fake time did not work.", dateTime.toString("dd/MM/yyyy HH"), new DateTime().toString("dd/MM/yyyy HH"));
    }
}
