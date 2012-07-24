package org.motechproject.ananya.kilkari.messagecampaign.mapper;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.kilkari.messagecampaign.contract.MessageCampaignRequest;
import org.motechproject.ananya.kilkari.messagecampaign.contract.mapper.MessageCampaignRequestMapper;
import org.motechproject.ananya.kilkari.messagecampaign.service.KilkariMessageCampaignService;
import org.motechproject.ananya.kilkari.messagecampaign.utils.KilkariPropertiesData;
import org.motechproject.model.Time;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class MessageCampaignRequestMapperTest {

    @Mock
    private KilkariPropertiesData kilkariPropertiesData;

    @Test
    public void shouldMapKilkariMessageCampaignRequestToCampaignRequest() throws Exception {
        int deltaDays = 2;
        when(kilkariPropertiesData.getCampaignScheduleDeltaDays()).thenReturn(deltaDays);
        int deltaMinutes = 30;
        when(kilkariPropertiesData.getCampaignScheduleDeltaMinutes()).thenReturn(deltaMinutes);
        MessageCampaignRequest messageCampaignRequest = new MessageCampaignRequest(
                "externalId", "twelve_months", new DateTime(2012, 5, 5, 0, 0));

        CampaignRequest campaignRequest = MessageCampaignRequestMapper.newRequestFrom(messageCampaignRequest, kilkariPropertiesData);

        assertEquals(messageCampaignRequest.getExternalId(), campaignRequest.externalId());
        assertEquals(KilkariMessageCampaignService.TWELVE_MONTHS, campaignRequest.campaignName());
        assertEquals(messageCampaignRequest.getSubscriptionCreationDate().plusDays(deltaDays).toLocalDate(), campaignRequest.referenceDate());
        assertEquals(new Time(messageCampaignRequest.getSubscriptionCreationDate().plusMinutes(deltaMinutes).toLocalTime()), campaignRequest.reminderTime());
        assertEquals(new Time(messageCampaignRequest.getSubscriptionCreationDate().plusMinutes(deltaMinutes).toLocalTime()), campaignRequest.deliverTime());
    }
}
