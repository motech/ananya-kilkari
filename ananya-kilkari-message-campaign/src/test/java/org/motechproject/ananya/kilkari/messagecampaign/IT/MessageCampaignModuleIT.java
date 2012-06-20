package org.motechproject.ananya.kilkari.messagecampaign.IT;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.kilkari.messagecampaign.request.KilkariMessageCampaignRequest;
import org.motechproject.ananya.kilkari.messagecampaign.service.KilkariMessageCampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationKilkariMessageCampaignContext.xml")
public class MessageCampaignModuleIT {

    @Autowired
    private KilkariMessageCampaignService kilkariMessageCampaignService;

    @Test
    public void shouldCreateMessageCampaign() {
        KilkariMessageCampaignRequest messageCampaignRequest = new KilkariMessageCampaignRequest(
                "my_id", "kilkari-mother-child-campaign", new DateTime(2012, 5, 5, 13, 30, 30), new DateTime(2012, 5, 5, 0, 0));

        kilkariMessageCampaignService.start(messageCampaignRequest);

        List<DateTime> dateTimeList = kilkariMessageCampaignService.getMessageTimings(messageCampaignRequest.getExternalId(), messageCampaignRequest.getCampaignName());

        assertNotNull(dateTimeList);
    }

}
