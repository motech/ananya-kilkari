package org.motechproject.ananya.kilkari.functional.test.domain;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.functional.test.utils.FakeTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

@Component
public class Time {

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;


    public void isMovedToFuture(DateTime dateTime) {
        FakeTimeUtils.moveToFutureTime(dateTime);
    }
}
