package org.motechproject.ananya.kilkari.web.response;

import org.joda.time.DateTime;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class UserScheduleTest {

    @Test
    public void shouldFindTheEarliestDateOfAllCampaigns() {
        UserSchedule userSchedule = new UserSchedule("123");
        DateTime today = DateTime.now();
        DateTime tomorrow = DateTime.now().plusDays(1);
        DateTime yesterday = DateTime.now().minusDays(1);

        userSchedule.addCampaignSchedule(new Schedule("1", Arrays.asList(today, tomorrow)));
        userSchedule.addCampaignSchedule(new Schedule("1", Arrays.asList(tomorrow, yesterday)));

        Long earliestDateTimeInMillis = userSchedule.earliestCampaignDateTimeInMillis();
        assertThat(earliestDateTimeInMillis, is(yesterday.getMillis()));
    }
}
