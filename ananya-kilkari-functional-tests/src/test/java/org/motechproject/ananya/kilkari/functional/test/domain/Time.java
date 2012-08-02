package org.motechproject.ananya.kilkari.functional.test.domain;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.functional.test.utils.FakeTimeUtils;
import org.springframework.stereotype.Component;

@Component
public class Time {
    public void isMovedToFuture(DateTime dateTime) {
        FakeTimeUtils.moveToFutureTime(dateTime);
    }
}
