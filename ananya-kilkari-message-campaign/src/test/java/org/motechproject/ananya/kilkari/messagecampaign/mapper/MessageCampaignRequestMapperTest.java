package org.motechproject.ananya.kilkari.messagecampaign.mapper;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.kilkari.messagecampaign.request.MessageCampaignRequest;
import org.motechproject.ananya.kilkari.messagecampaign.request.MessageCampaignRequestMapper;
import org.motechproject.ananya.kilkari.messagecampaign.service.MessageCampaignService;
import org.motechproject.model.Time;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;

import static org.junit.Assert.assertEquals;


@RunWith(MockitoJUnitRunner.class)
public class MessageCampaignRequestMapperTest {

    @Test
    public void shouldMapKilkariMessageCampaignRequestToCampaignRequest() throws Exception {
        int deltaDays = 2;
        int deltaMinutes = 30;
        MessageCampaignRequest messageCampaignRequest = new MessageCampaignRequest(
                "externalId", "choti_kilkari", new DateTime(2012, 5, 5, 0, 0));

        CampaignRequest campaignRequest = MessageCampaignRequestMapper.newRequestFrom(messageCampaignRequest, deltaDays, deltaMinutes);

        assertEquals(messageCampaignRequest.getExternalId(), campaignRequest.externalId());
        assertEquals(MessageCampaignService.TWELVE_MONTHS_CAMPAIGN_KEY, campaignRequest.campaignName());
        assertEquals(messageCampaignRequest.getSubscriptionStartDate().plusDays(deltaDays).toLocalDate(), campaignRequest.referenceDate());
        assertEquals(new Time(messageCampaignRequest.getSubscriptionStartDate().plusMinutes(deltaMinutes).toLocalTime()), campaignRequest.deliverTime());
        assertEquals(new Time(messageCampaignRequest.getSubscriptionStartDate().plusMinutes(deltaMinutes).toLocalTime()), campaignRequest.deliverTime());
    }
}
