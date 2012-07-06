package org.motechproject.ananya.kilkari.messagecampaign.service;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.kilkari.messagecampaign.request.KilkariMessageCampaignRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationKilkariMessageCampaignContext.xml")
public class KilkariMessageCampaignServiceIT {

    @Autowired
    private KilkariMessageCampaignService kilkariMessageCampaignService;

    @Test
    public void shouldCreateMessageCampaign() {
        DateTime referenceDate = DateTime.now().plusDays(1);

        int startWeek = 0;
        KilkariMessageCampaignRequest messageCampaignRequest = new KilkariMessageCampaignRequest(
                "my_id", KilkariMessageCampaignService.CAMPAIGN_NAME, null, referenceDate, startWeek);

        kilkariMessageCampaignService.start(messageCampaignRequest);

        List<DateTime> dateTimeList = kilkariMessageCampaignService.getMessageTimings(
                messageCampaignRequest.getExternalId(),
                messageCampaignRequest.getCampaignName(),
                DateTime.now(), DateTime.now().plusWeeks(61));

        assertThat(dateTimeList.size(), is(60));
    }
}
