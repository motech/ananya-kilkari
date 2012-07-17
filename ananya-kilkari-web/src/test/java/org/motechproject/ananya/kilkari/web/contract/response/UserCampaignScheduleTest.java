package org.motechproject.ananya.kilkari.web.contract.response;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ananya.kilkari.web.response.CampaignSchedule;
import org.motechproject.ananya.kilkari.web.response.UserCampaignSchedule;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class UserCampaignScheduleTest {

    @Test
    public void shouldFindTheEarliestDateOfAllCampaigns() {
        UserCampaignSchedule userCampaignSchedule = new UserCampaignSchedule("123");
        DateTime today = DateTime.now();
        DateTime tomorrow = DateTime.now().plusDays(1);
        DateTime yesterday = DateTime.now().minusDays(1);

        userCampaignSchedule.addCampaignSchedule(new CampaignSchedule("1", Arrays.asList(today, tomorrow)));
        userCampaignSchedule.addCampaignSchedule(new CampaignSchedule("1", Arrays.asList(tomorrow, yesterday)));

        Long earliestDateTimeInMillis = userCampaignSchedule.earliestCampaignDateTimeInMillis();
        assertThat(earliestDateTimeInMillis, is(yesterday.getMillis()));
    }
}
