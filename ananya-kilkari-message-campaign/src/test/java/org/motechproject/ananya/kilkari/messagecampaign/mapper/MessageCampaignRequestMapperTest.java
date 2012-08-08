package org.motechproject.ananya.kilkari.messagecampaign.mapper;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.kilkari.messagecampaign.domain.MessageCampaignPack;
import org.motechproject.ananya.kilkari.messagecampaign.request.MessageCampaignRequest;
import org.motechproject.ananya.kilkari.messagecampaign.request.MessageCampaignRequestMapper;
import org.motechproject.ananya.kilkari.messagecampaign.service.MessageCampaignService;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;

import static org.junit.Assert.assertEquals;


@RunWith(MockitoJUnitRunner.class)
public class MessageCampaignRequestMapperTest {

    @Test
    public void shouldMapKilkariMessageCampaignRequestToCampaignRequest() throws Exception {
        int deltaDays = 2;
        int deltaMinutes = 30;
        MessageCampaignRequest messageCampaignRequest = new MessageCampaignRequest("externalId",
                MessageCampaignPack.CHOTI_KILKARI.getCampaignName(), new DateTime(2012, 5, 5, 20, 30, DateTimeZone.UTC));

        CampaignRequest campaignRequest = MessageCampaignRequestMapper.newRequestFrom(messageCampaignRequest, deltaDays, deltaMinutes);

        assertEquals(messageCampaignRequest.getExternalId(), campaignRequest.externalId());
        assertEquals(MessageCampaignService.TWELVE_MONTHS_CAMPAIGN_KEY, campaignRequest.campaignName());
        assertEquals("08-05-2012", campaignRequest.referenceDate().toString("dd-MM-yyyy"));
        assertEquals(2, (int)campaignRequest.deliverTime().getHour());
        assertEquals(30, (int)campaignRequest.deliverTime().getMinute());
    }
}
