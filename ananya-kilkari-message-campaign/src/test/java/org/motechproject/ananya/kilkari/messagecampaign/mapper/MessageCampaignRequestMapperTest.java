package org.motechproject.ananya.kilkari.messagecampaign.mapper;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ananya.kilkari.messagecampaign.contract.MessageCampaignRequest;
import org.motechproject.ananya.kilkari.messagecampaign.contract.mapper.MessageCampaignRequestMapper;
import org.motechproject.ananya.kilkari.messagecampaign.service.KilkariMessageCampaignService;
import org.motechproject.model.Time;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;

import static org.junit.Assert.assertEquals;


public class MessageCampaignRequestMapperTest {

    @Test
    public void shouldMapKilkariMessageCampaignRequestToCampaignRequest() throws Exception {
        MessageCampaignRequest messageCampaignRequest = new MessageCampaignRequest(
                "externalId", "twelve_months", new DateTime(2012, 5, 5, 0, 0));

        CampaignRequest campaignRequest = MessageCampaignRequestMapper.newRequestFrom(messageCampaignRequest, 0, 0);

        assertEquals(messageCampaignRequest.getExternalId(), campaignRequest.externalId());
        assertEquals(KilkariMessageCampaignService.TWELVE_MONTHS, campaignRequest.campaignName());
        assertEquals(messageCampaignRequest.getSubscriptionCreationDate().toLocalDate(), campaignRequest.referenceDate());
        assertEquals(new Time(messageCampaignRequest.getSubscriptionCreationDate().toLocalTime()), campaignRequest.reminderTime());
        assertEquals(new Time(messageCampaignRequest.getSubscriptionCreationDate().toLocalTime()), campaignRequest.deliverTime());
    }
}
